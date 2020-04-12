/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.plugin.CodeGenerator;
import com.graphql_java_generator.plugin.CustomScalarDefinition;
import com.graphql_java_generator.plugin.PluginConfiguration;
import com.graphql_java_generator.plugin.language.CustomScalar;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author etienne-sf
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CustomScalarType extends ScalarType implements CustomScalar {

	final CustomScalarDefinition customScalarDefinition;

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
	public CustomScalarType(CustomScalarDefinition customScalarDefinition, PluginConfiguration pluginConfiguration) {
		super(customScalarDefinition.getGraphQLTypeName(),
				GraphqlUtils.graphqlUtils.getPackageName(customScalarDefinition.getJavaType()),
				GraphqlUtils.graphqlUtils.getClassSimpleName(customScalarDefinition.getJavaType()),
				pluginConfiguration);
		this.customScalarDefinition = customScalarDefinition;
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
