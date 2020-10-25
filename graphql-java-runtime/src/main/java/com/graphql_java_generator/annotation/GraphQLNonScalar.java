package com.graphql_java_generator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks all fields in the generated classes, that are non scalar. That is: this class is either an
 * input type, or a standard GraphQL object.<BR/>
 * The {@link #javaClass()} contains the type for this Scalar. This is useful only when this field is actually a list,
 * as java has the type erasure shit, and on Runtime, you can use java reflection to check the objects allowed in the
 * list.
 * 
 * @author etienne-sf
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface GraphQLNonScalar {

	/**
	 * The name of the field's attribute, as defined in the GraphQL schema. <BR/>
	 * Note on object and input types: It can be different from the attribute's name in the generated class, when the
	 * GraphQL field's name is a java keyword. <BR/>
	 * Note on interface: it's not possible to guess the attribute name from the getter method name, as there may be a
	 * case issue.
	 */
	public String fieldName();

	/**
	 * The name of the Scalar type, as defined in the GraphQL schema, without indication of list or mandatory. This name
	 * is used to retrieve the associated converter, on runtime.
	 */
	public String graphQLTypeSimpleName();

	/** Contains the java data type that is has used to store the data, on both the client and the server side. */
	public Class<?> javaClass();

}
