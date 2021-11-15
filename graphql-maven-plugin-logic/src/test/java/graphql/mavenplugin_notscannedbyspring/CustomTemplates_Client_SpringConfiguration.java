package graphql.mavenplugin_notscannedbyspring;

import java.io.File;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import com.graphql_java_generator.plugin.conf.PluginMode;
import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;

/**
 * Spring configuration for {@link CustomTemplatesClientTest} integration test Extends
 * {@link AbstractCustomTemplatesSpringConfiguration} to setup customized templates Schema used for test
 * allGraphQLCases.graphqls
 * 
 * @author ggomez
 */
@Configuration
@ComponentScan(basePackages = "com.graphql_java_generator", excludeFilters = {
		@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateRelaySchema.*"),
		@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateGraphQLSchema.*"),
		@Filter(type = FilterType.REGEX, pattern = "com.graphql_java_generator.client.graphqlrepository.*") })
public class CustomTemplates_Client_SpringConfiguration extends AbstractCustomTemplatesSpringConfiguration {

	protected CustomTemplates_Client_SpringConfiguration() {
		super(PluginMode.client);
	}

	@Override
	protected void addSpecificConfigurationParameterValue(GraphQLConfigurationTestHelper configuration) {
		// The allGraphQLCases GraphQL schema is located in the allGraphQLCases client sample
		configuration.schemaFileFolder = new File(mavenTestHelper.getModulePathFile(),
				"../graphql-maven-plugin-samples/graphql-maven-plugin-samples-allGraphQLCases-client/src/graphqls/allGraphQLCases");
		configuration.schemaFilePattern = "allGraphQLCases.graphqls";
		configuration.mode = PluginMode.client;
		configuration.schemaPersonalizationFile = null;
		configuration.customScalars = customScalars;
		configuration.separateUtilityClasses = false;

	}
}
