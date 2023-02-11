/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.conf.CommonConfiguration;
import com.graphql_java_generator.plugin.conf.CustomScalarDefinition;
import com.graphql_java_generator.plugin.generate_code.GenerateCodePluginExecutor;
import com.graphql_java_generator.plugin.language.CustomScalar;
import com.graphql_java_generator.util.GraphqlUtils;

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
	 * @param customScalarDefinition
	 *            The custom scalar implementation, as provided by the plugin's configuration. It may be null in some
	 *            cases (e.g.: when the goal is to generate the schema, as there is no code generation)
	 * @param configuration
	 *            The current plugin configuration, which is accessible through an interface that extends
	 *            {@link CommonConfiguration}
	 * @param documentParser
	 *            The {@link DocumentParser} that has parsed the schema, and so that contains the whole schema
	 *            definition
	 * @see CustomScalarDefinition
	 */
	public CustomScalarType(String name, CustomScalarDefinition customScalarDefinition,
			CommonConfiguration configuration, DocumentParser documentParser) {
		super(name, //
				(customScalarDefinition == null) ? null
						: GraphqlUtils.graphqlUtils.getPackageName(customScalarDefinition.getJavaType()),
				(customScalarDefinition == null) ? null
						: GraphqlUtils.graphqlUtils.getClassSimpleName(customScalarDefinition.getJavaType()),
				configuration, documentParser);

		if (customScalarDefinition != null && !name.equals(customScalarDefinition.getGraphQLTypeName()))
			throw new RuntimeException("The provided custom scalar implementation has a wrong name (expected: '" + name
					+ "', actual: '" + customScalarDefinition.getGraphQLTypeName() + "')");

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
		if (GenerateCodePluginExecutor.FILE_TYPE_JACKSON_DESERIALIZER.equals(fileType)) {
			return "CustomScalarDeserializer" + getName();
		} else {
			throw new RuntimeException("Unknown file type: '" + fileType + "'");
		}

	}

	@Override
	public String getClassFullName() {
		return customScalarDefinition.getJavaType();
	}
}
