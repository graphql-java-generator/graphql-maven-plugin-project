/**
 * 
 */
package com.graphql_java_generator.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
/**
 * Indicates that the annotated java class represents a GraphQL object type, and give the GraphQL name for this object.
 * The java name may be different from the GraphQL one, if the GraphQL name is a reserved java keyword.
 * 
 * @author etienne-sf
 */
public @interface GraphQLObjectType {

	/** The name of the type that is represented by the annotated java class, as defined in the GraphQL schema. */
	public String value();
}
