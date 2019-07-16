/**
 * 
 */
package graphql.mavenplugin.schema_personalization;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Contains the changes that can be applied to an entity
 * 
 * @author EtienneSF
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
	 * This string will replace the annotations of this entity. If empty, the entity will no more be annotated. It's the
	 * way to remove the @Entity annotation for an object,if you wish. You can put as many annotations as you want here,
	 * provided that they are properly separated. At least by a semi column. For proper format of the generated code,
	 * the best is to have a newline then a tabulation beween each annotation. Default value is null (no change to the
	 * annotations)
	 */
	private String replaceAnnotation = null;

	/** The description of a field that will be added to this entity, in the generated code */
	private List<Field> newFields = new ArrayList<>();

	/** The changes that will be applied to the entity'sfields, before the code generation */
	private List<Field> fieldPersonalizations = new ArrayList<>();

}
