package com.graphql_java_generator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks all fields in the generated classes, that are standard GraphQL scalar. The
 * {@link #graphqlType()} contains the type for this Scalar. This is useful only when this field is actually a list, as
 * java has the type erasure shit, and on Runtime, you can use java reflection to check the objects allowed in the list.
 * 
 * @author EtienneSF
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface GraphQLScalar {

	public Class<?> graphqlType();

}
