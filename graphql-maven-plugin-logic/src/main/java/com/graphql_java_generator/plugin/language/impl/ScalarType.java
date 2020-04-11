/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import com.graphql_java_generator.plugin.PluginConfiguration;
import com.graphql_java_generator.plugin.language.Field;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author etienne-sf
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
	 * @param pluginConfiguration
	 *            The current {@link PluginConfiguration}
	 */
	public ScalarType(String name, String packageName, String classSimpleName,
			PluginConfiguration pluginConfiguration) {
		super(packageName, pluginConfiguration, GraphQlType.SCALAR);
		setName(name);
		this.classSimpleName = classSimpleName;
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
