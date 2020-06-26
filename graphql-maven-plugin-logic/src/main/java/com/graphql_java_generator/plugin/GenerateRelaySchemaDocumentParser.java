/**
 * 
 */
package com.graphql_java_generator.plugin;

import org.springframework.stereotype.Component;

import com.graphql_java_generator.plugin.language.impl.CustomScalarType;

/**
 * @author etienne-sf
 */
@Component
public class GenerateRelaySchemaDocumentParser extends DocumentParser {

	/**
	 * This class doesn't need an implementation for the Custom Scalars. So a dummy one is returned. {@inheritDoc}
	 */
	@Override
	CustomScalarType getCustomScalarType(String name) {
		CustomScalarDefinition customScalarDefinition = new CustomScalarDefinition(name, "java.lang.String",
				"com.graphql_java_generator.customscalars.GraphQLScalarTypeString", null, null);
		return new CustomScalarType(customScalarDefinition);
	}

}
