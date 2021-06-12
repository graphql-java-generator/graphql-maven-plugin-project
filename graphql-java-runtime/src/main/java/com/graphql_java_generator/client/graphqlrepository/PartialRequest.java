/**
 * 
 */
package com.graphql_java_generator.client.graphqlrepository;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.graphql_java_generator.annotation.RequestType;

@Documented
@Retention(RUNTIME)
@Target(METHOD)
/**
 * This annotation marks the method of an interface, as being a partial GraphQL request. See the Project Wiki <A HREF=
 * "https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/client_exec_graphql_requests">request
 * page</A> for more information
 * 
 * @author etienne-sf
 */
public @interface PartialRequest {

	/**
	 * The kind of request that this method will execute. One of QUERY, MUTATION, SUBSCRIPTION. This is not mandatory,
	 * and the default value is QUERY
	 */
	RequestType requestType() default RequestType.query;

	/**
	 * The name of the request, as defined in the GraphQL schema. It's the field name in the Query, Mutation or
	 * Subscription type. <BR/>
	 * This name must be exactly the same as the one defined in the GraphQL schema. For instance, it's case
	 * sensitive.<BR/>
	 * This field is optional: if no requestName value is given, then the method name must match exactly the request
	 * (query/mutation/subscription) field name that is defined in the GraphQL schema.
	 */
	String requestName() default "";

	/**
	 * The GraphQL request. It can be either a Partial Request or a Full Request.
	 * 
	 * @See https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/client_exec_graphql_requests
	 */
	String request();
}
