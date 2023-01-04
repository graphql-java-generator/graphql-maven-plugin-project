package org.allGraphQLCases.server.config;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

@Configuration
public class CustomBeans {

	// @Bean
	// @Primary
	public GraphQL customGraphQL(GraphQLSchema graphQLSchema) throws IOException {
		// The MyInstrumentation class add a Human instance into the extensions response map, associated to the key
		// "aValueToTestTheExtensionsField"
		return GraphQL.newGraphQL(graphQLSchema).instrumentation(new MyInstrumentation()).build();
	}
}
