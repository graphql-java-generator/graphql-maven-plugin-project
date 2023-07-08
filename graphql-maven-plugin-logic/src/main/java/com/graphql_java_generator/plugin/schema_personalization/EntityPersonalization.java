/**
 * 
 */
package com.graphql_java_generator.plugin.schema_personalization;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Contains the changes that can be applied to an entity
 * 
 * @author etienne-sf
 */
@Data
public class EntityPersonalization {

	/** The name of the entity to personalize */
	private String name;

	/**
	 * This string will be added to the annotation(s) of this entity. You can put as many annotations as you want here,
	 * provided that they are properly separated. At least by a semi column. For proper format of the generated code,
	 * the best is to have a newline beween each annotation. If this entity already has annotation(s), a newline and
	 * indentation will be added first. Default value is null (no change to the annotations)
	 */
	private String addAnnotation = null;

	/**
	 * This string will be added to the list of interfaces for this entity. You can put as many interfaces as you want
	 * here, provided that they are separated by a comma. For proper format of the generated code, you can add newline
	 * characters (\n) and tabulation (\t) between each interface.
	 */
	private String addInterface = null;

	/** The description of a field that will be added to this entity, in the generated code */
	private List<FieldPersonalization> newFields = new ArrayList<>();

	/** The changes that will be applied to the entity'sfields, before the code generation */
	private List<FieldPersonalization> fieldPersonalizations = new ArrayList<>();

}
