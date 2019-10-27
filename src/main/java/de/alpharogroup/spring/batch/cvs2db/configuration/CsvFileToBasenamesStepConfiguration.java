package de.alpharogroup.spring.batch.cvs2db.configuration;

import javax.persistence.EntityManagerFactory;

import de.alpharogroup.spring.batch.cvs2db.dto.BaseName;
import de.alpharogroup.spring.batch.cvs2db.entity.BaseNames;
import de.alpharogroup.spring.batch.cvs2db.mapper.BaseNamesMapper;
import de.alpharogroup.spring.batch.factory.SpringBatchObjectFactory;
import org.mapstruct.factory.Mappers;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
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
public class CsvFileToBasenamesStepConfiguration
{

	EntityManagerFactory entityManagerFactory;

	ApplicationProperties applicationProperties;

	StepBuilderFactory stepBuilderFactory;

	PlatformTransactionManager transactionManager;

	@Bean
	public FileSystemResource basenamesResource() {
		String filePath = applicationProperties.getCsvDir() + "/" + applicationProperties.getBasenamesFileName();
		return new FileSystemResource(filePath);
	}

	@Bean
	public FlatFileItemReader<BaseName> basenamesReader() {
		return SpringBatchObjectFactory.newCsvFileItemReader(basenamesResource(), BaseName.class, ",", 1);
	}

	@Bean
	public JpaItemWriter<BaseNames> basenamesWriter() {
		return SpringBatchObjectFactory.newJpaItemWriter(entityManagerFactory);
	}

	@Bean
	public ItemProcessor<BaseName, BaseNames> basenamesProcessor() {
		return new ItemProcessor<BaseName, BaseNames>() {
			@Override
			public BaseNames process(BaseName item) throws Exception {
				BaseNames entity = Mappers.getMapper(BaseNamesMapper.class).toEntity(item);
				return entity;
			}
		};
	}

	@Bean
	public Step csvFileToBrosStep() {
		return stepBuilderFactory.get("csvFileToBrosStep").<BaseName, BaseNames>chunk(10).reader(basenamesReader())
				.processor(basenamesProcessor()).writer(basenamesWriter()).transactionManager(transactionManager).build();
	}

}