/**
 * 
 */
package graphql.mavenplugin_notscannedbyspring;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import com.graphql_java_generator.plugin.conf.CustomScalarDefinition;
import com.graphql_java_generator.plugin.conf.PluginMode;
import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;

/**
 * The Spring configuration used for JUnit tests
 * 
 * @author etienne-sf
 */
@Configuration
@ComponentScan(basePackages = "com.graphql_java_generator", excludeFilters = {
		@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateRelaySchema.*"),
		@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateGraphQLSchema.*"),
		@Filter(type = FilterType.REGEX, pattern = "com.graphql_java_generator.client.graphqlrepository.*") })
public class Forum_Server_SpringConfiguration extends AbstractSpringConfiguration {

	@Override
	protected void addSpecificConfigurationParameterValue(GraphQLConfigurationTestHelper configuration) {
		List<CustomScalarDefinition> customScalars = new ArrayList<>();
		customScalars.add(new CustomScalarDefinition("Date", "java.util.Date", null,
				"com.graphql_java_generator.customscalars.GraphQLScalarTypeDate.Date", null));

		configuration.schemaFilePattern = "forum.graphqls";
		configuration.legacyDataLoaderCall = false;
		configuration.mode = PluginMode.server;
		configuration.schemaPersonalizationFile = new File(mavenTestHelper.getModulePathFile(),
				"src/test/resources/forum_personalization.json");
		configuration.customScalars = customScalars;
		configuration.separateUtilityClasses = false;
		configuration.javaTypeForIDType = "java.lang.String";
	}

}
