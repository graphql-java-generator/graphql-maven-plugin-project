package com.graphql_java_generator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks all generated classes that are generated to match a GraphQL input type, as defined in the
 * GraphQL schema.
 * 
 * @author EtienneSF
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface GraphQLInputType {

	/** The name of the type that is represented by the annotated java class, as defined in the GraphQL schema. */
	public String value();

}
