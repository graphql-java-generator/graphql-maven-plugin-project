/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import com.graphql_java_generator.plugin.PluginMode;

/**
 * @author EtienneSF
 *
 */
public class CustomScalarType extends ScalarType {

	/** The full class name for this custom scalar converter */
	String customScalarConvertClassName;

	/**
	 * 
	 * @param name
	 *            The name of the GraphQL type
	 * @param packageName
	 *            The package where the Java type for this class is stored
	 * @param classSimpleName
	 *            The simple name for this class
	 * @param mode
	 *            The current plugin mode
	 */
	public CustomScalarType(String name, String packageName, String classSimpleName,
			String customScalarConvertClassName, PluginMode mode) {
		super(name, packageName, classSimpleName, mode);
		this.customScalarConvertClassName = customScalarConvertClassName;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isCustomScalar() {
		return true;
	}

}
