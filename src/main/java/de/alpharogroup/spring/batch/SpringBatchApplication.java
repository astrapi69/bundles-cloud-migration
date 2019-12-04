package de.alpharogroup.spring.batch;

import de.alpharogroup.file.search.PathFinder;
import de.alpharogroup.spring.batch.cvs2db.configuration.ApplicationProperties;
import de.alpharogroup.spring.boot.application.ApplicationHooks;
import de.alpharogroup.jdbc.CreationState;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableBatchProcessing
@EnableTransactionManagement
@EnableConfigurationProperties({ ApplicationProperties.class })
@SpringBootApplication
@ComponentScan(basePackages={ "de.alpharogroup.spring.batch.cvs2db.configuration",
	"de.alpharogroup.spring.batch.cvs2db.mapper"})
@EntityScan(basePackages={"de.alpharogroup.spring.batch.cvs2db.entity"})
public class SpringBatchApplication extends SpringBootServletInitializer
{

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(SpringBatchApplication.class);
//		ApplicationHooks instance = ApplicationHooks.INSTANCE;
//		instance.addDatabaseIfNotExists(application, PathFinder.getSrcMainResourcesDir(),
//			"application.yml");
		application.run(args);
	}

}
