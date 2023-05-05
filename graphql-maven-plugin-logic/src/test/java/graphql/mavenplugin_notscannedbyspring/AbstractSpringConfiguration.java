/**
 * 
 */
package graphql.mavenplugin_notscannedbyspring;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;
import com.graphql_java_generator.plugin.test.helper.MavenTestHelper;
import com.graphql_java_generator.util.GraphqlUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * The Spring configuration used for JUnit tests. To use tit, just create a subclass, and provide the schemaFilePattern
 * to its constructor
 * 
 * @author etienne-sf
 */
@Configuration
@Import({ JacksonAutoConfiguration.class })
public abstract class AbstractSpringConfiguration {

	private static String BASE_PACKAGE = "org.graphql.mavenplugin.junittest";

	@Getter
	@Setter
	private String schemaFileSubFolder;

	@Autowired
	MavenTestHelper mavenTestHelper;

	@Bean
	GraphqlUtils graphQlUtils() {
		return GraphqlUtils.graphqlUtils;
	}

	@Bean
	GraphQLConfiguration graphQLConfigurationTestHelper(MavenTestHelper mavenTestHelper) {
		GraphQLConfigurationTestHelper configuration = new GraphQLConfigurationTestHelper(this);
		configuration.schemaFileFolder = new File(mavenTestHelper.getModulePathFile(), "/src/test/resources");
		if (schemaFileSubFolder != null) {
			configuration.schemaFileFolder = new File(configuration.schemaFileFolder, schemaFileSubFolder);
		}

		String classname = this.getClass().getSimpleName();
		int firstDollar = classname.indexOf('$');
		configuration.packageName = BASE_PACKAGE + "." + classname.substring(0, firstDollar).toLowerCase();
		configuration.projectDir = mavenTestHelper.getModulePathFile();
		configuration.targetSourceFolder = mavenTestHelper.getTargetSourceFolder(
				(classname.contains("$")) ? classname = classname.substring(0, classname.indexOf('$')) : classname);
		configuration.targetResourceFolder = configuration.targetSourceFolder;
		configuration.targetClassFolder = new File(configuration.targetSourceFolder.getParentFile(),
				"compilation_test");

		addSpecificConfigurationParameterValue(configuration);

		return configuration;
	}

	protected abstract void addSpecificConfigurationParameterValue(GraphQLConfigurationTestHelper configuration);

}
