package com.graphql_java_generator.client.domain.allGraphQLCases;

import com.graphql_java_generator.CustomScalarRegistry;
import com.graphql_java_generator.CustomScalarRegistryImpl;

public class CustomScalarRegistryInitializer {

	/**
	 * Initialization of the {@link CustomScalarRegistry} with all known custom scalars, that is with all custom scalars
	 * defined in the project pom
	 */
	public void initCustomScalarRegistry() {
		CustomScalarRegistry customScalarRegistry = new CustomScalarRegistryImpl();

		customScalarRegistry.registerGraphQLScalarType(new com.graphql_java_generator.customscalars.GraphQLScalarTypeDate());
		customScalarRegistry.registerGraphQLScalarType(new com.graphql_java_generator.customscalars.GraphQLScalarTypeString());
		customScalarRegistry.registerGraphQLScalarType(graphql.Scalars.GraphQLLong);

		CustomScalarRegistryImpl.customScalarRegistry = customScalarRegistry;
	}

}
