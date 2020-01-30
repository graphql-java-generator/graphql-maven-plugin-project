/**
 * 
 */
package com.graphql_java_generator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks all fields in the generated classes, that are GraphQL Custom Scalar. <BR/>
 * The {@link #graphQLTypeName()} contains the type name for this Scalar, as defined in the GraphQL schema.<BR/>
 * The {@link #javaClass()} contains the java data type that is used to store the data, on both the client and the
 * server side.
 * 
 * @author EtienneSF
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface GraphQLCustomScalar {

	/**
	 * The name of the Custom Scalar type, as defined in the GraphQL schema. This name is used to retrieve the
	 * associated converter, on runtime
	 */
	public String graphQLTypeName();

	/** Contains the java data type that is has used to store the data, on both the client and the server side. */
	public Class<?> javaClass();

}
