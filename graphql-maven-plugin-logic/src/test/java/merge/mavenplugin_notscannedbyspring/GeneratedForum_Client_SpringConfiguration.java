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
		@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateCode.*"),
		@Filter(type = FilterType.REGEX, pattern = ".*CompilationTestHelper") })
public class GeneratedForum_Client_SpringConfiguration extends AbstractSpringConfiguration {

	@Override
	protected void addSpecificConfigurationParameterValue(GenerateGraphQLSchemaConfigurationTestHelper configuration) {
		configuration.schemaFileFolder = new File(mavenTestHelper.getModulePathFile(), ROOT_UNIT_TEST_FOLDER + "forum");
		configuration.schemaFilePattern = "forum.graphqls";
		configuration.targetSchemaFileName = "forum.graphqls";
		configuration.targetFolder = new File(getRootUnitTestFolder(), "forum/regenerate");
		configuration.addRelayConnections = false;
	}
}
