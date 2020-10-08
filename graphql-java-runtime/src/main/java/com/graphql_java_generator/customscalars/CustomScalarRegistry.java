/**
 * 
 */
package com.graphql_java_generator.customscalars;

import graphql.schema.GraphQLScalarType;

/**
 * Registry for all {@link GraphQLScalarType} available.
 * 
 * @author etienne-sf
 */
public interface CustomScalarRegistry {

	/**
	 * Manually register one {@link GraphQLScalarType}.
	 * 
	 * @param graphQLScalarType
	 * @param valueClazz
	 *            The java The java type that will contain values for this custom scalar. This is needed to properly
	 *            create the data from the value read in a string, especially when reading a GraphQL request, when in
	 *            client mode
	 */
	public void registerGraphQLScalarType(GraphQLScalarType graphQLScalarType, Class<?> valueClazz);

	/**
	 * Retrieves the registered {@link GraphQLScalarType} for this GraphQL CustomScalar.
	 * 
	 * @param graphQLTypeName
	 * @return the {@link GraphQLScalarType}, or null if no converter has been registered for the given name
	 */
	public GraphQLScalarType getGraphQLScalarType(String graphQLTypeName);

	/**
	 * Retrieves the registered {@link GraphQLScalarType} for this GraphQL CustomScalar.
	 * 
	 * @param graphQLTypeName
	 * @return the {@link GraphQLScalarType}, or null if no converter has been registered for the given name
	 */
	public CustomScalar getCustomScalar(String graphQLTypeName);

}
