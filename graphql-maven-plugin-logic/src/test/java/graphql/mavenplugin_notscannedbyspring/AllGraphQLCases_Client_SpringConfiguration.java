/**
 * 
 */
package graphql.mavenplugin_notscannedbyspring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.graphql_java_generator.plugin.PluginMode;

/**
 * The Spring configuration used for JUnit tests
 * 
 * @author EtienneSF
 */
@Configuration
@ComponentScan(basePackages = "com.graphql_java_generator.plugin")
public class AllGraphQLCases_Client_SpringConfiguration extends AbstractSpringConfiguration {

	public AllGraphQLCases_Client_SpringConfiguration() {
		super("allGraphQLCases.graphqls", PluginMode.client);
	}
}
