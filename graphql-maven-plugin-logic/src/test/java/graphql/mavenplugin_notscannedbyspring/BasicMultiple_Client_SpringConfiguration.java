/**
 * 
 */
package graphql.mavenplugin_notscannedbyspring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import com.graphql_java_generator.plugin.PluginMode;

@Configuration
@ComponentScan(basePackages = "com.graphql_java_generator", excludeFilters = {
		@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateRelaySchema.*"),
		@Filter(type = FilterType.REGEX, pattern = ".*\\.Merge.*") })
public class BasicMultiple_Client_SpringConfiguration extends AbstractSpringConfiguration {

	public BasicMultiple_Client_SpringConfiguration() {
		super("*.graphqls", PluginMode.client);
		setSchemaFileSubFolder("basic_multiple");
	}
}
