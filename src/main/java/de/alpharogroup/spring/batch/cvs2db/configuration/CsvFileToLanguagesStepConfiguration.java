package de.alpharogroup.spring.batch.cvs2db.configuration;

import de.alpharogroup.spring.batch.cvs2db.dto.Country;
import de.alpharogroup.spring.batch.cvs2db.dto.Language;
import de.alpharogroup.spring.batch.cvs2db.entity.Countries;
import de.alpharogroup.spring.batch.cvs2db.entity.Languages;
import de.alpharogroup.spring.batch.cvs2db.mapper.CountriesMapper;
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
public class CsvFileToLanguagesStepConfiguration
{

	EntityManagerFactory entityManagerFactory;

	ApplicationProperties applicationProperties;

	StepBuilderFactory stepBuilderFactory;

	PlatformTransactionManager transactionManager;

	@Bean
	public FileSystemResource languagesResource() {
		String filePath = applicationProperties.getCsvDir() + "/" + applicationProperties.getLanguagesFileName();
		return new FileSystemResource(filePath);
	}

	@Bean
	public FlatFileItemReader<Language> languagesReader() {
		return SpringBatchObjectFactory.newCsvFileItemReader(languagesResource(), Language.class, ",", 1);
	}

	@Bean
	public JpaItemWriter<Languages> languagesWriter() {
		return SpringBatchObjectFactory.newJpaItemWriter(entityManagerFactory);
	}

	@Bean
	public ItemProcessor<Language, Languages> languagesProcessor() {
		return item ->
			Mappers.getMapper(LanguagesMapper.class).toEntity(item);
	}

	@Bean
	public Step csvFileToLanguagesStep() {
		return stepBuilderFactory.get("csvFileToLanguagesStep").<Language, Languages>chunk(10).reader(languagesReader())
				.processor(languagesProcessor()).writer(languagesWriter()).transactionManager(transactionManager).build();
	}

}