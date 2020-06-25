/**
 * 
 */
package generate_relay_schema.mavenplugin_notscannedbyspring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * The Spring configuration used for JUnit tests
 * 
 * @author etienne-sf
 */
@Configuration
@ComponentScan(basePackages = "com.graphql_java_generator", excludeFilters = {
		@Filter(type = FilterType.REGEX, pattern = ".*\\.GraphQL.*"),
		@Filter(type = FilterType.REGEX, pattern = ".*CompilationTestHelper"),
		@Filter(type = FilterType.REGEX, pattern = ".*JsonSchemaPersonalization") })
public class Forum_Client_SpringConfiguration extends AbstractSpringConfiguration {

	public Forum_Client_SpringConfiguration() {
		super("forum.graphqls");
	}
}
