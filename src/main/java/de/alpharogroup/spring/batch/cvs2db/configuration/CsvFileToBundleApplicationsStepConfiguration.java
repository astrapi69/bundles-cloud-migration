package de.alpharogroup.spring.batch.cvs2db.configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import de.alpharogroup.collections.list.ListExtensions;
import de.alpharogroup.spring.batch.cvs2db.dto.BundleApplication;
import de.alpharogroup.spring.batch.cvs2db.entity.BundleApplications;
import de.alpharogroup.spring.batch.cvs2db.entity.LanguageLocales;
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

import java.util.List;

@Configuration
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CsvFileToBundleApplicationsStepConfiguration
{
	@PersistenceContext
	private EntityManager em;

	EntityManagerFactory entityManagerFactory;

	ApplicationProperties applicationProperties;

	StepBuilderFactory stepBuilderFactory;

	PlatformTransactionManager transactionManager;

	@Bean
	public FileSystemResource bundleApplicationsResource() {
		String filePath = applicationProperties.getCsvDir() + "/" + applicationProperties.getBundleApplicationsFileName();
		return new FileSystemResource(filePath);
	}

	@Bean
	public FlatFileItemReader<BundleApplication> bundleApplicationsReader() {
		return SpringBatchObjectFactory
			.newCsvFileItemReader(bundleApplicationsResource(), BundleApplication.class, ",", 1);
	}

	@Bean
	public JpaItemWriter<BundleApplications> bundleApplicationsWriter() {
		return SpringBatchObjectFactory.newJpaItemWriter(entityManagerFactory);
	}

	@Bean
	public ItemProcessor<BundleApplication, BundleApplications> bundleApplicationsProcessor() {
		return new ItemProcessor<BundleApplication, BundleApplications>() {
			@Override
			public BundleApplications process(BundleApplication item) throws Exception {
				Integer defaultLocaleId = item.getDefaultLocale().getId();
				List<?> languageLocales = em.createNativeQuery("SELECT * from language_locales ll where ll.id = ?1")
				.setParameter(1, defaultLocaleId).getResultList();
				LanguageLocales first =(LanguageLocales) ListExtensions.getFirst(languageLocales);
				BundleApplications entity = BundleApplications.builder()
					.defaultLocale(first)
					.name(item.getName())
					.build();
				return entity;
			}

		};
	}

	@Bean
	public Step csvFileToBundleApplicationsStep() {
		return stepBuilderFactory.get("csvFileToBundleApplicationsStep").<BundleApplication, BundleApplications>chunk(10)
				.reader(bundleApplicationsReader()).processor(bundleApplicationsProcessor()).writer(bundleApplicationsWriter()).faultTolerant()
				.skip(FlatFileParseException.class).skip(PersistenceException.class).skipLimit(10)
				.allowStartIfComplete(true).transactionManager(transactionManager).build();
	}

}