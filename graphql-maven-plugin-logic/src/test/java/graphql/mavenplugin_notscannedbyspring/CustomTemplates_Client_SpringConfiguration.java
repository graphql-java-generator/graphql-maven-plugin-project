package graphql.mavenplugin_notscannedbyspring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;

import com.graphql_java_generator.plugin.conf.PluginMode;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

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

	public CustomTemplates_Client_SpringConfiguration() {
		super("allGraphQLCases.graphqls", PluginMode.client, customScalars);
	}

}
