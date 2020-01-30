package com.graphql_java_generator.client.domain.forum;

import com.graphql_java_generator.CustomScalarRegistryImpl;
import com.graphql_java_generator.customcalars.GraphQLScalarTypeDate;

public class CustomScalarRegistryInitializer {

	public void initCustomScalarRegistry() {
		CustomScalarRegistryImpl.customScalarRegistry.registerGraphQLScalarType(new GraphQLScalarTypeDate());
	}

}
