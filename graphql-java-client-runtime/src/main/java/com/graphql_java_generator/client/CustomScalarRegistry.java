/**
 * 
 */
package com.graphql_java_generator.client;

import com.graphql_java_generator.customscalars.CustomScalar;

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
	 * @param typeName
	 *            The name of the custom scalar type, as defined in the provided GaphQL schema. It may be different from
	 *            the provided <code>graphQLScalarType</code>
	 * @param graphQLScalarType
	 *            The GraphQL custom scalar class, that contain the coercing rules to serialize and unserialize this
	 *            custom scalar
	 * @param valueClazz
	 *            The java The java type that will contain values for this custom scalar. This is needed to properly
	 *            create the data from the value read in a string, especially when reading a GraphQL request, when in
	 *            client mode
	 */
	public void registerGraphQLScalarType(String typeName, GraphQLScalarType graphQLScalarType, Class<?> valueClazz);

	/**
	 * Retrieves the registered {@link GraphQLScalarType} for this GraphQL CustomScalar.
	 * 
	 * @param graphQLTypeName
	 * @return the {@link GraphQLScalarType}, or null if no converter has been registered for the given name
	 */
	public GraphQLScalarType getGraphQLCustomScalarType(String graphQLTypeName);

	/**
	 * Retrieves the registered {@link GraphQLScalarType} for this GraphQL CustomScalar.
	 * 
	 * @param graphQLTypeName
	 * @return the {@link GraphQLScalarType}, or null if no converter has been registered for the given name
	 */
	public CustomScalar getCustomScalar(String graphQLTypeName);

}
