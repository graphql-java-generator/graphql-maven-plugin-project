package graphql.mavenplugin_notscannedbyspring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;

import com.graphql_java_generator.plugin.conf.PluginMode;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * Spring configuration for {@link CustomTemplatesServerTest}integration test Extends
 * {@link AbstractCustomTemplatesSpringConfiguration} to setup customized templates Schema used for test
 * allGraphQLCases.graphqls
 * 
 * @author ggomez
 */
@Configuration
@ComponentScan(basePackages = "com.graphql_java_generator", excludeFilters = {
		@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateRelaySchema.*"),
		@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateGraphQLSchema.*") })
public class CustomTemplates_Server_SpringConfiguration extends AbstractCustomTemplatesSpringConfiguration {

	public CustomTemplates_Server_SpringConfiguration() {
		super("allGraphQLCases.graphqls", PluginMode.server, customScalars);
	}

}
