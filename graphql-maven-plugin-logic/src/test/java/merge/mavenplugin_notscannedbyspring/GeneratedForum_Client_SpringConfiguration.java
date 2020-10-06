/**
 * 
 */
package merge.mavenplugin_notscannedbyspring;

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
		@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateRelaySchema.*"),
		@Filter(type = FilterType.REGEX, pattern = ".*\\.GraphQL.*"),
		@Filter(type = FilterType.REGEX, pattern = ".*CompilationTestHelper") })
public class GeneratedForum_Client_SpringConfiguration extends AbstractSpringConfiguration {

	public GeneratedForum_Client_SpringConfiguration() {
		super(ROOT_UNIT_TEST_FOLDER + "forum", "forum.graphqls", "forum.graphqls", "forum/regenerate", false);
	}
}
