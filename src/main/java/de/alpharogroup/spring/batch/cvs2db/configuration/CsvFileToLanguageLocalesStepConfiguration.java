package de.alpharogroup.spring.batch.cvs2db.configuration;

import de.alpharogroup.spring.batch.cvs2db.dto.Language;
import de.alpharogroup.spring.batch.cvs2db.dto.LanguageLocale;
import de.alpharogroup.spring.batch.cvs2db.entity.LanguageLocales;
import de.alpharogroup.spring.batch.cvs2db.entity.Languages;
import de.alpharogroup.spring.batch.cvs2db.mapper.LanguageLocalesMapper;
import de.alpharogroup.spring.batch.cvs2db.mapper.LanguagesMapper;
import de.alpharogroup.spring.batch.factory.SpringBatchObjectFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
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

import javax.persistence.EntityManagerFactory;

@Configuration
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CsvFileToLanguageLocalesStepConfiguration
{

	EntityManagerFactory entityManagerFactory;

	ApplicationProperties applicationProperties;

	StepBuilderFactory stepBuilderFactory;

	PlatformTransactionManager transactionManager;

	@Bean
	public FileSystemResource languageLocalesResource() {
		String filePath = applicationProperties.getCsvDir() + "/" + applicationProperties.getLanguageLocalesFileName();
		return new FileSystemResource(filePath);
	}

	@Bean
	public FlatFileItemReader<LanguageLocale> languageLocalesReader() {
		return SpringBatchObjectFactory.newCsvFileItemReader(languageLocalesResource(), LanguageLocale.class, ",", 1);
	}

	@Bean
	public JpaItemWriter<LanguageLocales> languageLocalesWriter() {
		return SpringBatchObjectFactory.newJpaItemWriter(entityManagerFactory);
	}

	@Bean
	public ItemProcessor<LanguageLocale, LanguageLocales> languageLocalesProcessor() {
		return item ->
			Mappers.getMapper(LanguageLocalesMapper.class).toEntity(item);
	}

	@Bean
	public Step csvFileToLanguageLocalesStep() {
		return stepBuilderFactory.get("csvFileToLanguageLocalesStep").<LanguageLocale, LanguageLocales>chunk(10).reader(languageLocalesReader())
				.processor(languageLocalesProcessor()).writer(languageLocalesWriter()).transactionManager(transactionManager).build();
	}

}