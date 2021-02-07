/**
 * 
 */
package com.graphql_java_generator.plugin.generate_schema;

import org.springframework.stereotype.Component;

import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.conf.CustomScalarDefinition;
import com.graphql_java_generator.plugin.language.impl.CustomScalarType;

/**
 * @author etienne-sf
 */
@Component
public class GenerateGraphQLSchemaDocumentParser extends DocumentParser {

	/**
	 * This class doesn't need an implementation for the Custom Scalars. So a dummy one is returned. {@inheritDoc}
	 */
	@Override
	protected CustomScalarType getCustomScalarType(String name) {

		// If this custom scalar has already been added to the list, let's return it
		for (CustomScalarType customScalarType : customScalars) {
			if (customScalarType.getName().equals(name)) {
				return customScalarType;
			}
		}

		// The custom scalar has not been added to the list yet, let's add it first.
		CustomScalarDefinition customScalarDefinition = new CustomScalarDefinition(name, "java.lang.String",
				"com.graphql_java_generator.customscalars.GraphQLScalarTypeString", null, null);
		CustomScalarType customScalarType = new CustomScalarType(customScalarDefinition, configuration);
		customScalars.add(customScalarType);
		types.put(customScalarType.getName(), customScalarType);
		return customScalarType;
	}

}
