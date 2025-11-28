/**
 * 
 */
package com.graphql_java_generator.client;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is a registry of the GraphQLTypeMapping classes that have been generated at runtime. These classes
 * contains the mapping that allows to find at runtime the class that must be used for a given GraphQL type, from its
 * name
 */
public class GraphQLTypeMappingRegistry {

	/**
	 * The map between the schema name and the GraphQLTypeMapping instance for this schema
	 */
	private static Map<String, GraphQLTypeMapping> graphQLTypeMappings = new HashMap<>();

	/**
	 * Registers the given GraphQLTypeMapping instance for the given schema
	 * 
	 * @param schemaName
	 *            The name of the schema
	 * @param graphQLTypeMapping
	 *            The GraphQLTypeMapping instance for this schema
	 */
	public static void registerGraphQLTypeMapping(GraphQLTypeMapping graphQLTypeMapping) {
		if (!graphQLTypeMappings.containsKey(graphQLTypeMapping.getSchemaName())) {
			graphQLTypeMappings.put(graphQLTypeMapping.getSchemaName(), graphQLTypeMapping);
		}
	}

	/**
	 * Indicates whether a GraphQLTypeMapping instance has been registered for the given schema
	 * 
	 * @param schemaName
	 * @return
	 */
	public static boolean isGraphQLTypeMappingRegistered(String schemaName) {
		return graphQLTypeMappings.containsKey(schemaName);
	}

	/**
	 * Retrieves the GraphQLTypeMapping instance for the given schema
	 * 
	 * @param schemaName
	 *            The name of the schema
	 * @return
	 */
	public static GraphQLTypeMapping getGraphQLTypeMapping(String schemaName) {
		GraphQLTypeMapping ret = graphQLTypeMappings.get(schemaName);
		if (ret == null) {
			throw new RuntimeException(
					"The GraphQLTypeMapping for the schema '" + schemaName + "' has not been registered");
		}
		return ret;
	}

	/**
	 * Retrieves the Java class that maps to the given GraphQL type name, for the given schema
	 * 
	 * @param schemaName
	 *            The name of the schema
	 * @param typeName
	 *            The name of the GraphQL type, as found in the GraphQL schema
	 * @return
	 */
	public static Class<?> getJavaClass(String schemaName, String typeName) {
		return getGraphQLTypeMapping(schemaName).getJavaClass(typeName);
	}
}
