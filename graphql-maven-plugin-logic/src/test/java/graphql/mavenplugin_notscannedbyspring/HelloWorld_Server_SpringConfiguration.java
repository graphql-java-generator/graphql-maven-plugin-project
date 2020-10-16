/**
 * 
 */
package graphql.mavenplugin_notscannedbyspring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;

import com.graphql_java_generator.plugin.conf.PluginMode;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * The Spring configuration used for JUnit tests
 * 
 * @author etienne-sf
 */
@Configuration
@ComponentScan(basePackages = "com.graphql_java_generator", excludeFilters = {
		@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateRelaySchema.*"),
		@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateGraphQLSchema.*") })
public class HelloWorld_Server_SpringConfiguration extends AbstractSpringConfiguration {

	public HelloWorld_Server_SpringConfiguration() {
		super("helloworld.graphqls", PluginMode.server, null, null, false);
	}
}
