/**
 * 
 */
package graphql.mavenplugin.language.impl;

import java.util.ArrayList;
import java.util.List;

import graphql.mavenplugin.PluginMode;
import graphql.mavenplugin.language.Field;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class is the image for a graphql Enum
 * 
 * @author EtienneSF
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EnumType extends AbstractType {

	/** The list of values */
	List<String> values = new ArrayList<String>();

	/**
	 * 
	 * @param name
	 *            The name of this enum type
	 * @param packageName
	 *            the package name where it must be created
	 * @param mode
	 *            The current {@link PluginMode}
	 */
	public EnumType(String name, String packageName, PluginMode mode) {
		super(packageName, mode, GraphQlType.ENUM);
		setName(name);
	}

	/**
	 * @param packageName
	 *            the package name where it must be created
	 * @param mode
	 *            The current {@link PluginMode}
	 */
	public EnumType(String packageName, PluginMode mode) {
		super(packageName, mode, GraphQlType.ENUM);
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
