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
	private String javaClassFullName;

	/**
	 * Returns the simple java name for this class (for instance String for java.lang.String)
	 * 
	 * @return
	 */
	public String getJavaClassSimpleName() {
		int i = javaClassFullName.lastIndexOf('.');
		return javaClassFullName.substring(i + 1);
	}

}
