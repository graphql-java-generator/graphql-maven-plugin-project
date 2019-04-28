/**
 * 
 */
package graphql.mavenplugin_notscannedbyspring;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.oembedler.moon.graphql.boot.GraphQLJavaToolsAutoConfiguration;

import graphql.mavenplugin.PluginMode;
import graphql.mavenplugin.SpringConfiguration;
import graphql.mavenplugin.test.helper.MavenLog;
import graphql.mavenplugin.test.helper.MavenTestHelper;

/**
 * The Spring configuration used for JUnit tests. To use tit, just create a subclass, and provide the schemaFilePattern
 * to its constructor
 * 
 * @author EtienneSF
 */
@Configuration
@Import({ JacksonAutoConfiguration.class, GraphQLJavaToolsAutoConfiguration.class })
public abstract class AbstractSpringConfiguration {

	private final static String BASE_PACKAGE = "org.graphql.mavenplugin.junittest";
	private final static String ENCODING = "UTF-8";

	/** Logger pour cette classe */
	protected Logger logger = LogManager.getLogger();
	private final String schemaFilePattern;

	private PluginMode mode;

	@Resource
	MavenTestHelper mavenTestHelper;

	protected AbstractSpringConfiguration(String schemaFilePattern, PluginMode mode) {
		this.schemaFilePattern = schemaFilePattern;
		this.mode = mode;
	}

	/**
	 * This overrides the {@link SpringConfiguration#resourcesFolder()}, so that we read the test resources
	 * 
	 * @return "/src/test/resources"
	 */
	@Bean
	public String resourcesFolder() {
		return "/src/test/resources";
	}

	@Bean
	String packageName() {
		// When I load all the generated code in the eclipse project, Java is unhappy that the same class exists several
		// times. To solve that, every junit test has its own target package, based on the Spring configuration class.
		//
		// Let's extract the simple name from the current class name. As it is proxified by Spring, we must remove
		// everything from the first "$" character
		String classname = this.getClass().getSimpleName().toLowerCase();
		int firstDollar = classname.indexOf('$');
		return BASE_PACKAGE + "." + classname.substring(0, firstDollar);
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
	PluginMode mode() {
		return mode;
	}

	@Bean
	MavenProject project() {
		MavenProject project = mock(MavenProject.class);
		when(project.getBasedir()).thenReturn(mavenTestHelper.getModulePathFile());
		return project;
	}

	@Bean
	String schemaFilePattern() {
		return schemaFilePattern;
	}

	@Bean
	File schemaPersonalizationFile() {
		return new File(mavenTestHelper.getModulePathFile(), "noPersonalization");
	}

	@Bean
	File targetSourceFolder() {
		// Get the folder for this class. If the class name contains a $, like
		// AllGraphQLCases_Server_SpringConfiguration$$EnhancerBySpringCGLIB$$d8ce51ed, then it's a Spring proxy. We
		// keep only
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
