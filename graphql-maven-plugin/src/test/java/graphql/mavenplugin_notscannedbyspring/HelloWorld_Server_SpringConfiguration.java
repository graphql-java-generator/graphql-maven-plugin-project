/**
 * 
 */
package graphql.mavenplugin_notscannedbyspring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import graphql.mavenplugin.PluginMode;

/**
 * The Spring configuration used for JUnit tests
 * 
 * @author EtienneSF
 */
@Configuration
@ComponentScan(basePackages = "graphql.mavenplugin")
public class HelloWorld_Server_SpringConfiguration extends AbstractSpringConfiguration {

	public HelloWorld_Server_SpringConfiguration() {
		super("src/test/resources/helloworld.graphqls", PluginMode.server);
	}
}
