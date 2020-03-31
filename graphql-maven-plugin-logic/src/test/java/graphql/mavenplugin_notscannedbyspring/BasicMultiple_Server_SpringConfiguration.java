/**
 * 
 */
package graphql.mavenplugin_notscannedbyspring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.graphql_java_generator.plugin.PluginMode;

@Configuration
@ComponentScan(basePackages = "com.graphql_java_generator")
public class BasicMultiple_Server_SpringConfiguration extends AbstractSpringConfiguration {

	public BasicMultiple_Server_SpringConfiguration() {
		super("*.graphqls", PluginMode.server);
		setSchemaFileSubFolder("basic_multiple");
	}
}
