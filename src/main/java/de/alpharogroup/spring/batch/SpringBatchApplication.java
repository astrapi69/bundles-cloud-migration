package de.alpharogroup.spring.batch;

import de.alpharogroup.file.search.PathFinder;
import de.alpharogroup.jdbc.PostgreSQLConnectionsExtensions;
import de.alpharogroup.spring.batch.cvs2db.configuration.ApplicationProperties;
import de.alpharogroup.yaml.YamlToPropertiesExtensions;
import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

@EnableBatchProcessing
@EnableTransactionManagement
@EnableConfigurationProperties({ ApplicationProperties.class })
@SpringBootApplication
@ComponentScan(basePackages={ "de.alpharogroup.spring.batch.cvs2db.configuration",
	"de.alpharogroup.spring.batch.cvs2db.mapper"})
@EntityScan(basePackages={"de.alpharogroup.spring.batch.cvs2db.entity"})
@Log
public class SpringBatchApplication extends SpringBootServletInitializer
{
	 @Getter
	ApplicationProperties applicationProperties;

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(SpringBatchApplication.class);
		addInitHooks(application);
		application.run(args);
	}

	static void addInitHooks(final SpringApplication application) {
		application.addListeners((ApplicationListener<ApplicationStartingEvent>) event -> {

			try
			{
				File yamlFile = new File(PathFinder.getSrcMainResourcesDir(), "application.yml");
				Properties properties = YamlToPropertiesExtensions
					.toProperties(yamlFile.getAbsolutePath());
				String host = properties.getProperty("app.db-host");
				int port = Integer.valueOf(properties.getProperty("app.db-port"));
				String dbName = properties.getProperty("app.db-name");
				String dbUser = properties.getProperty("app.db-username");
				String dbPw = properties.getProperty("app.db-password");
				PostgreSQLConnectionsExtensions.newDatabase(host,
					port,
					dbName,
					dbUser, dbPw, "", "");
			}
			catch (SQLException e)
			{
				throw new RuntimeException(e);
			}
			catch (ClassNotFoundException e)
			{
				throw new RuntimeException(e);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		});
	}

}
