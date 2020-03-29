package com.graphql_java_generator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks all generated classes that are queries/mutations/subscription, as defined in the GraphQL
 * schema.
 * 
 * @author EtienneSF
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface GraphQLQuery {

	/**
	 * The name of the query/mutation/subscription, that is represented by the annotated java class, as defined in the
	 * GraphQL schema.
	 */
	public String name();

	/**
	 * The type : query, mutation or subscription, as defined in the GraphQL schema.
	 */
	public RequestType type();
}
