package de.alpharogroup.spring.batch.cvs2db.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationProperties {

	String name;
	String dbHost;
	String dbName;
	int dbPort;
	String dbUrlPrefix;
	String dbUsername;
	String dbPassword;
	String dir;
	String csvDir;
	String countriesFileName;
	String languagesFileName;
	String languageLocalesFileName;
	String bundleApplicationsFileName;
	String basenamesFileName;
	String bundlenamesFileName;
	String propertiesKeysFileName;
	String propertiesValuesFileName;
	String resourcebundlesFileName;

}
