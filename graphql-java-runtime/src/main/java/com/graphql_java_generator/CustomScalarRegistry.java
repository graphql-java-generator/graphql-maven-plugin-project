/**
 * 
 */
package com.graphql_java_generator;

import org.springframework.stereotype.Component;

import graphql.schema.GraphQLScalarType;

/**
 * Registry for all {@link GraphQLScalarType} available.
 * 
 * @author EtienneSF
 */
public interface CustomScalarRegistry {

	/**
	 * This method registers all {@link GraphQLScalarType} that are declared as Spring {@link Component}. Another way to
	 * register {@link GraphQLScalarType} is to call the {@link #registerGraphQLScalarType(GraphQLScalarType)}.
	 */
	public void registerAllGraphQLScalarType();

	/**
	 * Manually register one {@link GraphQLScalarType}.
	 * 
	 * @param graphQLScalarType
	 */
	public void registerGraphQLScalarType(GraphQLScalarType graphQLScalarType);

	/**
	 * Retrieves the registered {@link GraphQLScalarType} for this GraphQL CustomScalar.
	 * 
	 * @param graphQLTypeName
	 * @return the {@link GraphQLScalarType}, or null if no converter has been registered for the given name
	 */
	public GraphQLScalarType getGraphQLScalarType(String graphQLTypeName);

}
