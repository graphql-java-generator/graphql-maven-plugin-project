/**
 * 
 */
package graphql.mavenplugin.language.impl;

import java.util.List;

import graphql.mavenplugin.PluginMode;
import graphql.mavenplugin.language.Field;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author EtienneSF
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScalarType extends AbstractType {

	/** The simple name for this class */
	final String classSimpleName;

	// /**
	// * Indicates whether this type is predefined (as Integer or String), or not. If not, it has a specific class
	// * defining it.
	// */
	// boolean preDefined;

	/**
	 * 
	 * @param name
	 *            The name of the GraphQL type
	 * @param packageName
	 *            The package where the Java type for this class is stored
	 * @param classSimpleName
	 *            The simple name for this class
	 * @param The
	 *            current plugin mode
	 */
	public ScalarType(String name, String packageName, String classSimpleName, PluginMode mode) {
		super(packageName, mode, GraphQlType.SCALAR);
		setName(name);
		this.classSimpleName = classSimpleName;
	}

	/**
	 * An enum has no fields.
	 * 
	 * @return null
	 */
	@Override
	public List<Field> getFields() {
		return null;
	}

}
