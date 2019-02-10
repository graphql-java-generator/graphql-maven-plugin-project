/**
 * 
 */
package graphql.mavenplugin.generation;

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

	/** The java class for this type */
	private Class<?> javaClass;

}
