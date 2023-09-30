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
 * Indicates that the annotated java class is a deprecated responseType. These class were generated in the first
 * versions of the plugin. By default, since 2.0, they are not generated any more. A plugin parameter allows to still
 * generate them, to maintain compatibility for people that used the 1.x version. <br/>
 * This annotation indicates the the annotated class is such a deprecated responseType. This is useful, for instance
 * when deserializing a response that contains aliases with custom deserializers (custom scalars or list)
 * 
 * @author etienne-sf
 */
public @interface GraphQLDeprecatedResponseForRequestObject {

	/** The full name of the query, mutation or subscription that this generated ResponseType overrides */
	public String value();
}
