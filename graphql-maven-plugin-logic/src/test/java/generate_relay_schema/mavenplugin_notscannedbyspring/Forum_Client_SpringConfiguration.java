/**
 * 
 */
package generate_relay_schema.mavenplugin_notscannedbyspring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * The Spring configuration used for JUnit tests
 * 
 * @author etienne-sf
 */
@Configuration
@ComponentScan(basePackages = "com.graphql_java_generator")
public class Forum_Client_SpringConfiguration extends AbstractSpringConfiguration {

	public Forum_Client_SpringConfiguration() {
		super("forum.graphqls");
	}
}
