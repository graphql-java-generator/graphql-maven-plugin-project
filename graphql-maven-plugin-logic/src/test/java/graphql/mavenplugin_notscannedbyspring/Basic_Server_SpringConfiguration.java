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
 * @author etienne-sf
 */
@Configuration
@ComponentScan(basePackages = "com.graphql_java_generator")
public class Basic_Server_SpringConfiguration extends AbstractSpringConfiguration {

	public Basic_Server_SpringConfiguration() {
		super("basic.graphqls", PluginMode.server);
	}
}
