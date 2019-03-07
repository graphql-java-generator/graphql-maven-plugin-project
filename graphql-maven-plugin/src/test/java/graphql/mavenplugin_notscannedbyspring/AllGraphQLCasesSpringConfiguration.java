/**
 * 
 */
package graphql.mavenplugin_notscannedbyspring;

import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.oembedler.moon.graphql.boot.GraphQLJavaToolsAutoConfiguration;

/**
 * The Spring configuration used for JUnit tests
 * 
 * @author EtienneSF
 */
@Configuration
@Import({ JacksonAutoConfiguration.class, GraphQLJavaToolsAutoConfiguration.class })
@ComponentScan(basePackages = "graphql.mavenplugin")
public class AllGraphQLCasesSpringConfiguration extends AbstractSpringConfiguration {

	public AllGraphQLCasesSpringConfiguration() {
		// super("allGraphQLCases.graphqls");
		super("src/test/resources/allGraphQLCases.graphqls");
	}
}
