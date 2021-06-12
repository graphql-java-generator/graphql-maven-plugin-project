/**
 * 
 */
package com.graphql_java_generator.client.graphqlrepository;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(ElementType.TYPE)
/**
 * This annotation marks an Interface as being a GraphQL Repository. That its: its methods are all marked with either
 * the {@link FullRequest} or the {@link PartialRequest} annotation. A Java runtime proxy is created at runtime, that
 * will execute the relevant GraphQL request, when one of its methods is executed.
 * 
 * @author etienne-sf
 */
public @interface GraphQLRepository {

	/** The name of the Spring bean to build. Default is to use the interface name as the bean name */
	String value() default "";

}
