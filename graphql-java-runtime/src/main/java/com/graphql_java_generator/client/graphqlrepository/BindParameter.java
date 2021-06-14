/**
 * 
 */
package com.graphql_java_generator.client.graphqlrepository;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target({ METHOD, PARAMETER })
/**
 * This annotation marks a parameter as being the value for either a Bind Parameter or a GraphQL variable. It should be
 * used only on parameters of method of interfaces, marked by a {@link GraphQLRepository} annotation.
 * 
 * @author etienne-sf
 */
public @interface BindParameter {

	/**
	 * The Bind Parameter or GraphQL variable's name, as defined in the GraphQL Request by either &name, ?name or $
	 * name. For more information, have a look at the <A HREF=
	 * "https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/client_exec_graphql_requests">Client
	 * wiki</A>
	 */
	String name();

}
