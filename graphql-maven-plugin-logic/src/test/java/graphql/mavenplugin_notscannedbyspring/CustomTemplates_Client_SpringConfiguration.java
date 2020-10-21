package graphql.mavenplugin_notscannedbyspring;

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
		@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateGraphQLSchema.*") })
public class CustomTemplates_Client_SpringConfiguration extends AbstractCustomTemplatesSpringConfiguration {

	protected CustomTemplates_Client_SpringConfiguration() {
		super(PluginMode.client);
	}

	@Override
	protected void addSpecificConfigurationParameterValue(GraphQLConfigurationTestHelper configuration) {
		configuration.schemaFilePattern = "allGraphQLCases.graphqls";
		configuration.mode = PluginMode.client;
		configuration.schemaPersonalizationFile = null;
		configuration.customScalars = customScalars;
		configuration.separateUtilityClasses = false;

	}
}
