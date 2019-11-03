package de.alpharogroup.spring.batch.cvs2db.configuration;

import de.alpharogroup.spring.batch.cvs2db.dto.BaseName;
import de.alpharogroup.spring.batch.cvs2db.dto.Country;
import de.alpharogroup.spring.batch.cvs2db.entity.BaseNames;
import de.alpharogroup.spring.batch.cvs2db.entity.Countries;
import de.alpharogroup.spring.batch.cvs2db.mapper.BaseNamesMapper;
import de.alpharogroup.spring.batch.cvs2db.mapper.CountriesMapper;
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
public class CsvFileToCountriesStepConfiguration
{

	EntityManagerFactory entityManagerFactory;

	ApplicationProperties applicationProperties;

	StepBuilderFactory stepBuilderFactory;

	PlatformTransactionManager transactionManager;

	@Bean
	public FileSystemResource countriesResource() {
		String filePath = applicationProperties.getCsvDir() + "/" + applicationProperties.getCountriesFileName();
		return new FileSystemResource(filePath);
	}

	@Bean
	public FlatFileItemReader<Country> countriesReader() {
		return SpringBatchObjectFactory.newCsvFileItemReader(countriesResource(), Country.class, ",", 1);
	}

	@Bean
	public JpaItemWriter<Countries> countriesWriter() {
		return SpringBatchObjectFactory.newJpaItemWriter(entityManagerFactory);
	}

	@Bean
	public ItemProcessor<Country, Countries> countriesProcessor() {
		return new ItemProcessor<Country, Countries>() {
			@Override
			public Countries process(Country item) throws Exception {
				return Mappers.getMapper(CountriesMapper.class).toEntity(item);
			}

		};
	}

	@Bean
	public Step csvFileToCountriesStep() {
		return stepBuilderFactory.get("csvFileToCountriesStep").<Country, Countries>chunk(10).reader(countriesReader())
				.processor(countriesProcessor()).writer(countriesWriter()).transactionManager(transactionManager).build();
	}

}