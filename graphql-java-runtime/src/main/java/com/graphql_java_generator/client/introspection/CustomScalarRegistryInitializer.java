package com.graphql_java_generator.client.introspection;

import com.graphql_java_generator.CustomScalarRegistry;
import com.graphql_java_generator.CustomScalarRegistryImpl;

public class CustomScalarRegistryInitializer {

	/**
	 * Initialization of the {@link CustomScalarRegistry} with all known custom scalars, that is with all custom scalars
	 * defined in the project pom
	 */
	public void initCustomScalarRegistry() {
		CustomScalarRegistry customScalarRegistry = new CustomScalarRegistryImpl();


		CustomScalarRegistryImpl.customScalarRegistry = customScalarRegistry;
	}

}
