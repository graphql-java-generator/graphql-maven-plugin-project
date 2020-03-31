/**
 * 
 */
package graphql.mavenplugin_notscannedbyspring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.graphql_java_generator.plugin.PluginMode;

@Configuration
@ComponentScan(basePackages = "com.graphql_java_generator")
public class BasicMultiple_Client_SpringConfiguration extends AbstractSpringConfiguration {

	public BasicMultiple_Client_SpringConfiguration() {
		super("*.graphqls", PluginMode.client);
		setSchemaFileSubFolder("basic_multiple");
	}
}
