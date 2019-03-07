/**
 * 
 */
package graphql.mavenplugin_notscannedbyspring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * The Spring configuration used for JUnit tests
 * 
 * @author EtienneSF
 */
@Configuration
@ComponentScan(basePackages = "graphql.mavenplugin")
public class MavenResourceSchemaStringProviderTestSpringConfiguration extends AbstractSpringConfiguration {

	public MavenResourceSchemaStringProviderTestSpringConfiguration() {
		super("src/test/resources/MavenResourceSchemaStringProviderTest/*.graphqls");
	}
}
