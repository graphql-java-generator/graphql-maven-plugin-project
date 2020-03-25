package com.graphql_java_generator.client.domain.allGraphQLCases;

import com.graphql_java_generator.customscalars.CustomScalarRegistry;
import com.graphql_java_generator.customscalars.CustomScalarRegistryImpl;

public class CustomScalarRegistryInitializer {

	/**
	 * Initialization of the {@link CustomScalarRegistry} with all known custom scalars, that is with all custom scalars
	 * defined in the project pom
	 */
	public void initCustomScalarRegistry() {
		CustomScalarRegistry customScalarRegistry = new CustomScalarRegistryImpl();

		customScalarRegistry.registerGraphQLScalarType(com.graphql_java_generator.customscalars.GraphQLScalarTypeDate.Date);
		customScalarRegistry.registerGraphQLScalarType(graphql.Scalars.GraphQLLong);
		customScalarRegistry.registerGraphQLScalarType(com.graphql_java_generator.customscalars.GraphQLScalarTypeString.String);

		CustomScalarRegistryImpl.customScalarRegistry = customScalarRegistry;
	}

}
