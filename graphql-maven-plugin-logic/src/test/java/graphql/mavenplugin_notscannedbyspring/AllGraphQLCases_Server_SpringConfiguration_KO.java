/**
 * 
 */
package graphql.mavenplugin_notscannedbyspring;

import java.io.File;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import com.graphql_java_generator.plugin.conf.PluginMode;
import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;

import graphql.schema.GraphQLScalarType;

/**
 * The Spring configuration used for JUnit tests. In this one, no {@link GraphQLScalarType} : used to check that this
 * lack is properly handled.
 * 
 * @author etienne-sf
 */
@Configuration
@ComponentScan(basePackages = "com.graphql_java_generator", excludeFilters = {
		@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateRelaySchema.*"),
		@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateGraphQLSchema.*"),
		@Filter(type = FilterType.REGEX, pattern = "com.graphql_java_generator.client.graphqlrepository.*") })
public class AllGraphQLCases_Server_SpringConfiguration_KO extends AbstractSpringConfiguration {

	@Override
	protected void addSpecificConfigurationParameterValue(GraphQLConfigurationTestHelper configuration) {
		// The allGraphQLCases GraphQL schema is located in the allGraphQLCases client sample
		configuration.schemaFileFolder = new File(mavenTestHelper.getModulePathFile(),
				"../graphql-maven-plugin-samples/graphql-maven-plugin-samples-allGraphQLCases-client/src/graphqls/allGraphQLCases");
		configuration.schemaFilePattern = "allGraphQLCases*.graphqls";
		configuration.mode = PluginMode.server;
		configuration.schemaPersonalizationFile = null;
		configuration.customScalars = null;
		configuration.separateUtilityClasses = false;
	}
}
