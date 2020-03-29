package graphql.mavenplugin_notscannedbyspring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.graphql_java_generator.plugin.PluginMode;

/**
 * Spring configuration for {@link CustomTemplatesClientTest} integration test Extends
 * {@link AbstractCustomTemplatesSpringConfiguration} to setup customized templates Schema used for test
 * allGraphQLCases.graphqls
 * 
 * @author ggomez
 */
@Configuration
@ComponentScan(basePackages = "com.graphql_java_generator")
public class CustomTemplates_Client_SpringConfiguration extends AbstractCustomTemplatesSpringConfiguration {

	public CustomTemplates_Client_SpringConfiguration() {
		super("allGraphQLCases.graphqls", PluginMode.server, customScalars);
	}

}
