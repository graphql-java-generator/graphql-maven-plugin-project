/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import java.util.ArrayList;
import java.util.List;

import com.graphql_java_generator.plugin.PluginMode;
import com.graphql_java_generator.plugin.language.EnumValue;
import com.graphql_java_generator.plugin.language.Field;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class is the image for a graphql Enum
 * 
 * @author etienne-sf
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EnumType extends AbstractType {

	/** The list of values */
	List<EnumValue> values = new ArrayList<>();

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

	/**
	 * An enum has no identifier.
	 * 
	 * @return null
	 */
	@Override
	public Field getIdentifier() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isInputType() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isCustomScalar() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isScalar() {
		return true;
	}

}
