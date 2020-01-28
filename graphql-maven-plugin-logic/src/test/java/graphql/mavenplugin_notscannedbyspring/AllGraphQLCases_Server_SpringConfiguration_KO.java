/**
 * 
 */
package graphql.mavenplugin_notscannedbyspring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.graphql_java_generator.plugin.PluginMode;

import graphql.schema.GraphQLScalarType;

/**
 * The Spring configuration used for JUnit tests. In this one, no {@link GraphQLScalarType} : used to check that this
 * lack is properly handled.
 * 
 * @author EtienneSF
 */
@Configuration
@ComponentScan(basePackages = "com.graphql_java_generator.plugin")
public class AllGraphQLCases_Server_SpringConfiguration_KO extends AbstractSpringConfiguration {

	public AllGraphQLCases_Server_SpringConfiguration_KO() {
		super("allGraphQLCases.graphqls", PluginMode.server);
	}
}
