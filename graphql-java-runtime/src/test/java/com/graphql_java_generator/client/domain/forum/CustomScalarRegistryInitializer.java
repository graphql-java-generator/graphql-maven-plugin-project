package com.graphql_java_generator.client.domain.forum;

import com.graphql_java_generator.CustomScalarRegistry;
import com.graphql_java_generator.CustomScalarRegistryImpl;
import com.graphql_java_generator.customscalars.GraphQLScalarTypeDate;

public class CustomScalarRegistryInitializer {

	/**
	 * Initialization of the {@link CustomScalarRegistry} with all known custom scalars, that is with all custom scalars
	 * defined in the project pom
	 */
	public void initCustomScalarRegistry() {
		CustomScalarRegistry customScalarRegistry = new CustomScalarRegistryImpl();
		customScalarRegistry.registerGraphQLScalarType(GraphQLScalarTypeDate.Date);
		CustomScalarRegistryImpl.customScalarRegistry = customScalarRegistry;
	}

}
