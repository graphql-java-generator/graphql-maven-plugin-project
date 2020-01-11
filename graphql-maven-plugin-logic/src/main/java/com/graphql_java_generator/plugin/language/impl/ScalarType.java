/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import java.util.List;

import com.graphql_java_generator.plugin.PluginMode;
import com.graphql_java_generator.plugin.language.Field;

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
	 * A scalar has no fields.
	 * 
	 * @return null
	 */
	@Override
	public List<Field> getFields() {
		return null;
	}

	/**
	 * A scalar has no identifier.
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
}
