/**
 * 
 */
package merge.mavenplugin_notscannedbyspring;

import java.io.File;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import com.graphql_java_generator.plugin.test.helper.GenerateGraphQLSchemaConfigurationTestHelper;

/**
 * The Spring configuration used for JUnit tests
 * 
 * @author etienne-sf
 */
@Configuration
@ComponentScan(basePackages = "com.graphql_java_generator", excludeFilters = {
		@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateRelaySchema.*"),
		@Filter(type = FilterType.REGEX, pattern = "com.graphql_java_generator.plugin.generate_code.*"),
		@Filter(type = FilterType.REGEX, pattern = ".*CompilationTestHelper"),
		@Filter(type = FilterType.REGEX, pattern = "com.graphql_java_generator.client.graphqlrepository.*") })
public class GeneratedAllGraphQLCases_Client_SpringConfiguration extends AbstractSpringConfiguration {

	@Override
	protected void addSpecificConfigurationParameterValue(GenerateGraphQLSchemaConfigurationTestHelper configuration) {
		configuration.schemaFileFolder = new File(mavenTestHelper.getModulePathFile(),
				ROOT_UNIT_TEST_FOLDER + "allGraphQLCases");
		configuration.schemaFilePattern = "allGraphQLCases.graphqls";
		configuration.targetSchemaFileName = "allGraphQLCases.graphqls";
		configuration.targetFolder = new File(getRootUnitTestFolder(), "allGraphQLCases/regenerate");
		configuration.addRelayConnections = false;
	}
}
