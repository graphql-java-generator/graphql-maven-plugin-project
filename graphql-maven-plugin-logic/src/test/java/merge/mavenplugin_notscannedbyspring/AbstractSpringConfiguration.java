/**
 * 
 */
package merge.mavenplugin_notscannedbyspring;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import com.graphql_java_generator.plugin.test.helper.GenerateGraphQLSchemaConfigurationTestHelper;
import com.graphql_java_generator.plugin.test.helper.MavenTestHelper;

/**
 * The Spring configuration used for JUnit tests. To use tit, just create a subclass, and provide the schemaFilePattern
 * to its constructor
 * 
 * @author etienne-sf
 */
public abstract class AbstractSpringConfiguration {

	public final static String ROOT_UNIT_TEST_FOLDER = "target/junittest_merge/";

	@Autowired
	MavenTestHelper mavenTestHelper;
	@Autowired
	GenerateGraphQLSchemaConfigurationTestHelper configuration;

	protected File getRootUnitTestFolder() {
		return new File(mavenTestHelper.getModulePathFile(), ROOT_UNIT_TEST_FOLDER);
	}

	@Bean
	GenerateGraphQLSchemaConfigurationTestHelper graphQLConfigurationTestHelper() {
		GenerateGraphQLSchemaConfigurationTestHelper configuration = new GenerateGraphQLSchemaConfigurationTestHelper(
				this);

		// Let's update what"s specific to the current test case
		addSpecificConfigurationParameterValue(configuration);

		return configuration;
	}

	/**
	 * Allows the concrete subclasses to supersede the default configuration values to values specific to the current
	 * test case
	 */
	protected abstract void addSpecificConfigurationParameterValue(
			GenerateGraphQLSchemaConfigurationTestHelper configuration);

}
