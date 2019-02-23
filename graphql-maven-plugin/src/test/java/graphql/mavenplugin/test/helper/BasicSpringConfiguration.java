/**
 * 
 */
package graphql.mavenplugin.test.helper;

import java.io.File;

import javax.annotation.Resource;

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
public class BasicSpringConfiguration {

	public final static String BASE_PACKAGE = "org.graphql.mavenplugin.junittest";
	public final static String ENCODING = "UTF-8";

	/** Logger pour cette classe */
	protected Logger logger = LogManager.getLogger();

	@Resource
	MavenTestHelper mavenTestHelper;

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
		// Get the folder for this class. If the class name contains a $, like
		// AllGraphQLCasesSpringConfiguration$$EnhancerBySpringCGLIB$$d8ce51ed, then it's a Spring proxy. We keep only
		// what's before the '$', which is the significant part.
		String classname = this.getClass().getSimpleName();
		if (classname.contains("$")) {
			classname = classname.substring(0, classname.indexOf('$'));
		}
		return mavenTestHelper.getTargetSourceFolder(classname);
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
