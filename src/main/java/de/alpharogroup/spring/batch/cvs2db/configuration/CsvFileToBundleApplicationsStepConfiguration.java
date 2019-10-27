package de.alpharogroup.spring.batch.cvs2db.configuration;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import de.alpharogroup.spring.batch.cvs2db.dto.BundleApplication;
import de.alpharogroup.spring.batch.cvs2db.entity.BundleApplications;
import de.alpharogroup.spring.batch.cvs2db.mapper.BundleApplicationsMapper;
import de.alpharogroup.spring.batch.factory.SpringBatchObjectFactory;
import org.mapstruct.factory.Mappers;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Configuration
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CsvFileToBundleApplicationsStepConfiguration
{

	EntityManagerFactory entityManagerFactory;

	ApplicationProperties applicationProperties;

	StepBuilderFactory stepBuilderFactory;

	PlatformTransactionManager transactionManager;

	@Bean
	public FileSystemResource ballResource() {
		String filePath = applicationProperties.getCsvDir() + "/" + applicationProperties.getBallFileName();
		return new FileSystemResource(filePath);
	}

	@Bean
	public FlatFileItemReader<BundleApplication> ballReader() {
		return SpringBatchObjectFactory
			.newCsvFileItemReader(ballResource(), BundleApplication.class, ",", 1);
	}

	@Bean
	public JpaItemWriter<BundleApplications> ballWriter() {
		return SpringBatchObjectFactory.newJpaItemWriter(entityManagerFactory);
	}

	@Bean
	public ItemProcessor<BundleApplication, BundleApplications> ballProcessor() {
		return new ItemProcessor<BundleApplication, BundleApplications>() {
			@Override
			public BundleApplications process(BundleApplication item) throws Exception {
				BundleApplications entity = Mappers.getMapper(BundleApplicationsMapper.class).toEntity(item);
				return entity;
			}

		};
	}

	@Bean
	public Step csvFileToBundleApplicationsStep() {
		return stepBuilderFactory.get("csvFileToBundleApplicationsStep").<BundleApplication, BundleApplications>chunk(10)
				.reader(ballReader()).processor(ballProcessor()).writer(ballWriter()).faultTolerant()
				.skip(FlatFileParseException.class).skip(PersistenceException.class).skipLimit(10)
				.allowStartIfComplete(true).transactionManager(transactionManager).build();
	}

}