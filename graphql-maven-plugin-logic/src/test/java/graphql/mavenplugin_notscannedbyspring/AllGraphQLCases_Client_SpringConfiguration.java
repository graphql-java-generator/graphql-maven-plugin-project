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
public class AllGraphQLCases_Client_SpringConfiguration extends AbstractSpringConfiguration {

	static List<CustomScalarDefinition> customScalars;
	static {
		customScalars = new ArrayList<>();
		customScalars.add(new CustomScalarDefinition("Date", "java.util.Date", null,
				"com.graphql_java_generator.customscalars.GraphQLScalarTypeDate.Date", null));
		customScalars
				.add(new CustomScalarDefinition("Long", "java.lang.Long", null, "graphql.Scalars.GraphQLLong", null));
		customScalars.add(new CustomScalarDefinition("else", "java.lang.String", null,
				"com.graphql_java_generator.customscalars.GraphQLScalarTypeString.String", null));
		customScalars.add(new CustomScalarDefinition("NonNegativeInt", "java.lang.Integer", null,
				"graphql.scalars.ExtendedScalars.NonNegativeInt", null));
	}

	@Override
	protected void addSpecificConfigurationParameterValue(GraphQLConfigurationTestHelper configuration) {
		// The allGraphQLCases GraphQL schema is located in the allGraphQLCases client sample
		configuration.schemaFileFolder = new File(mavenTestHelper.getModulePathFile(),
				"../graphql-maven-plugin-samples/graphql-maven-plugin-samples-allGraphQLCases-client/src/graphqls/allGraphQLCases");
		configuration.schemaFilePattern = "allGraphQLCases*.graphqls";
		configuration.mode = PluginMode.client;
		configuration.schemaPersonalizationFile = null;
		configuration.customScalars = customScalars;
		configuration.separateUtilityClasses = false;
	}
}
