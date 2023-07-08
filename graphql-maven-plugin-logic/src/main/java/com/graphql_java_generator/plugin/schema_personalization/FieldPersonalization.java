/**
 * 
 */
package com.graphql_java_generator.plugin.schema_personalization;

import lombok.Data;

/**
 * The various attributes of the field, so that an existing GraphQL field can be personalized, or a new field can be
 * created.
 * 
 * @author etienne-sf
 */
@Data
public class FieldPersonalization {

	/** The name of the new field */
	String name;

	/**
	 * The name of the type of this new field. This field must be provided for new fields. It's optional when
	 * personalizing fields
	 */
	String type;

	/** true if this field is the id of the entity (or part of). Default value is: false */
	Boolean id = null;
	/**
	 * true if this field is a list. In this case, the type indicates the type of the items in the list. Default value
	 * is: false
	 */
	Boolean list = null;

	/** true if this field is mandatory. Default value is: false */
	Boolean mandatory = null;

	/**
	 * This string will be added to the annotations of this field. You can put as many annotations as you want here,
	 * provided that they are properly separated. At least by a semi column. For proper format of the generated code,
	 * the best is to have a newline then a tabulation beween each annotation. If this field already has annotation, a
	 * newline and indentation will be added first. Default value is: null (no change)
	 */
	String addAnnotation = null;

}
