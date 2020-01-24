/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import com.graphql_java_generator.plugin.CodeGenerator;
import com.graphql_java_generator.plugin.PluginMode;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author EtienneSF
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CustomScalarType extends ScalarType {

	/** The full class name for this custom scalar converter */
	String customScalarConverterClassName;

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
			String customScalarConverterClassName, PluginMode mode) {
		super(name, packageName, classSimpleName, mode);
		this.customScalarConverterClassName = customScalarConverterClassName;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isCustomScalar() {
		return true;
	}

	/** Get the filename where this type must be created. Default is to return the name for the Type */
	@Override
	public String getTargetFileName(String fileType) {
		if (CodeGenerator.FILE_TYPE_JACKSON_DESERIALIZER.equals(fileType)) {
			return "JacksonDeserializer" + getName();
		} else {
			throw new RuntimeException("Unknown file type: '" + fileType + "'");
		}

	}
}
