/**
 * 
 */
package com.graphql_java_generator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class indicates that this field has at least one input parameter. It can be associated with field (for concrete
 * classes) or setter methods (for interfaces).<BR/>
 * 
 * @author etienne-sf
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface GraphQLInputParameters {

	/**
	 * Contains the list of the names for each of the input parameters of this field. It must contain at least one item
	 * (as this annotation indicates that this field has input parameter(s), and must have the same number of items as
	 * {@link #types()}. The n<I>th</I> name this list, must be linked to the n<I>th</I> type in the {@link #types()}
	 * list.
	 * 
	 * @return
	 */
	public String[] names();

	/**
	 * Contains the list of the types for each of the input parameters of this field. It must contain at least one item
	 * (as this annotation indicates that this field has input parameter(s), and must have the same number of items as
	 * {@link #names()}. The n<I>th</I> type this list, must be linked to the n<I>th</I> name in the {@link #names()}
	 * list.
	 * 
	 * @return
	 */
	public String[] types();

}
