/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.plugin.CommonConfiguration;
import com.graphql_java_generator.plugin.CustomScalarDefinition;
import com.graphql_java_generator.plugin.GraphQLCodeGenerator;
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

	final GraphqlUtils graphqlUtils = GraphqlUtils.graphqlUtils;

	/**
	 * 
	 * @param name
	 *            The name of the GraphQL type
	 * @param configuration
	 *            The current plugin configuration, which is accessible through an interface that extends
	 *            {@link CommonConfiguration}
	 * @see CustomScalarDefinition
	 */
	public CustomScalarType(CustomScalarDefinition customScalarDefinition, CommonConfiguration configuration) {
		super(customScalarDefinition.getGraphQLTypeName(),
				GraphqlUtils.graphqlUtils.getPackageName(customScalarDefinition.getJavaType()),
				GraphqlUtils.graphqlUtils.getClassSimpleName(customScalarDefinition.getJavaType()), configuration);
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
		if (GraphQLCodeGenerator.FILE_TYPE_JACKSON_DESERIALIZER.equals(fileType)) {
			return "CustomScalarDeserializer" + getName();
		} else {
			throw new RuntimeException("Unknown file type: '" + fileType + "'");
		}

	}
}
