/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import com.graphql_java_generator.plugin.CodeGenerator;
import com.graphql_java_generator.plugin.CustomScalarDefinition;
import com.graphql_java_generator.plugin.PluginConfiguration;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author etienne-sf
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CustomScalarType extends ScalarType {

	/**
	 * The full class name for this GraphQLScalarType. Optional.
	 * 
	 * @see CustomScalarDefinition
	 */
	String graphQLScalarTypeClass;

	/**
	 * The full path for the static field name that contains this GraphQLScalarType. Optional.
	 * 
	 * @see CustomScalarDefinition
	 */
	String graphQLScalarTypeStaticField;

	/**
	 * The full path for the static method name that returns this GraphQLScalarType. Optional.
	 * 
	 * @see CustomScalarDefinition
	 */
	String graphQLScalarTypeGetter;

	/**
	 * 
	 * @param name
	 *            The name of the GraphQL type
	 * @param packageName
	 *            The package where the Java type for this class is stored
	 * @param classSimpleName
	 *            The simple name for this class
	 * @param graphQLScalarTypeClass
	 *            The full class name for this GraphQLScalarType. Optional.
	 * @param graphQLScalarTypeStaticField
	 *            The full path for the static field name that contains this GraphQLScalarType. Optional.
	 * @param graphQLScalarTypeGetter
	 *            The full path for the static method name that returns this GraphQLScalarType. Optional.
	 * @param pluginConfiguration
	 *            The current {@link PluginConfiguration}
	 * @see CustomScalarDefinition
	 */
	public CustomScalarType(String name, String packageName, String classSimpleName, String graphQLScalarTypeClass,
			String graphQLScalarTypeStaticField, String graphQLScalarTypeGetter,
			PluginConfiguration pluginConfiguration) {
		super(name, packageName, classSimpleName, pluginConfiguration);
		this.graphQLScalarTypeClass = graphQLScalarTypeClass;
		this.graphQLScalarTypeStaticField = graphQLScalarTypeStaticField;
		this.graphQLScalarTypeGetter = graphQLScalarTypeGetter;
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
			return "CustomScalarDeserializer" + getName();
		} else {
			throw new RuntimeException("Unknown file type: '" + fileType + "'");
		}

	}
}
