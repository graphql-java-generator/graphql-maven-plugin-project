/**
 * 
 */
package com.graphql_java_generator.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marke the type, field or method as a non GraphQL one. Typically, it marked stuff that doesn't come
 * from the GraphQL schema, and is internal to the generated code.
 * 
 * @author etienne-sf
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ TYPE, FIELD, METHOD })
public @interface GraphQLIgnore {

}
