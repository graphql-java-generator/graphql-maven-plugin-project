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
		@Filter(type = FilterType.REGEX, pattern = ".*CompilationTestHelper") })
public class GeneratedAllGraphQLCases_Client_SpringConfiguration extends AbstractSpringConfiguration {

	public GeneratedAllGraphQLCases_Client_SpringConfiguration() {
		super(ROOT_UNIT_TEST_FOLDER + "allGraphQLCases", "allGraphQLCases.graphqls", "allGraphQLCases.graphqls",
				"allGraphQLCases/regenerate");
	}
}
