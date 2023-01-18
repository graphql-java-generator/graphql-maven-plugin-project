/**
 * 
 */
package com.graphql_java_generator.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author etienne-sf
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ TYPE, FIELD, METHOD, PARAMETER })
public @interface GraphQLDirectives {

	/** The directives that were defined in the GraphQL schema for this item */
	public GraphQLDirective[] value();
}
