/**
 * 
 */
package graphql.mavenplugin.language;

import lombok.Data;

/**
 * This class describes the type for a {@link Field} type.
 * 
 * @author EtienneSF
 */
@Data
public class FieldType {

	/** The name of the type */
	private String name;

	/** The java class full name for this type (e.g.: com.mycompany.graphql.Class) */
	private String javaClassName;

}
