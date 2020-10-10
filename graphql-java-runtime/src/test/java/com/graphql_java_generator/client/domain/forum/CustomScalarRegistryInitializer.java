package com.graphql_java_generator.client.domain.forum;

import java.util.Date;

import com.graphql_java_generator.customscalars.CustomScalarRegistry;
import com.graphql_java_generator.customscalars.CustomScalarRegistryImpl;

public class CustomScalarRegistryInitializer {

	/**
	 * Initialization of the {@link CustomScalarRegistry} with all known custom scalars, that is with all custom scalars
	 * defined in the project pom
	 */
	public void initCustomScalarRegistry() {
		CustomScalarRegistry customScalarRegistry = new CustomScalarRegistryImpl();

		customScalarRegistry.registerGraphQLScalarType(
				com.graphql_java_generator.customscalars.GraphQLScalarTypeDate.Date, Date.class);

		CustomScalarRegistryImpl.customScalarRegistry = customScalarRegistry;
	}

}
