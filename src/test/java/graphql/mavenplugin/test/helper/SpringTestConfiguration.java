/**
 * 
 */
package graphql.mavenplugin.test.helper;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.plugin.logging.Log;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.oembedler.moon.graphql.boot.GraphQLJavaToolsAutoConfiguration;

/**
 * The Spring configuration used for JUnit tests
 * 
 * @author EtienneSF
 */
@Configuration
@Import({ JacksonAutoConfiguration.class, GraphQLJavaToolsAutoConfiguration.class })
@ComponentScan(basePackages = "graphql.mavenplugin.test")
public class SpringTestConfiguration {

	public final static String BASE_PACKAGE = "org.graphql.mavenplugin.junittest";
	public final static String ENCODING = "UTF-8";
	public final static String TARGET_SOURCE_FOLDER = "target/junittest/generated-src";

	/** Logger pour cette classe */
	protected Logger logger = LogManager.getLogger();

	@Bean
	String basePackage() {
		return BASE_PACKAGE;
	}

	@Bean
	String encoding() {
		return ENCODING;
	}

	@Bean
	Log log() {
		return new MavenLog(logger);
	}

	@Bean
	File targetSourceFolder() {
		return new File(MavenTestHelper.getModulePathFile(), TARGET_SOURCE_FOLDER);
	}

	/**
	 * Where the compiler code for JUnit tests is to be stored, when validating the generated code. This resource
	 * specific to the JUnit tests.
	 */
	@Bean
	File targetClassFolder(File targetSourceFolder) {
		return new File(targetSourceFolder.getParentFile(), "compilation_test");
	}
}
