package com.graphql_java_generator.client;

/**
 * The classes that implement this interface provide the mapping between GraphQL types and Java classes. There are
 * generated along with other classes by the GraphQL plugin.
 */
public interface GraphQLTypeMapping {

	/**
	 * Retrieves the name of the schema for which this GraphQLTypeMapping applies
	 * 
	 * @return
	 */
	public String getSchemaName();

	/**
	 * Retrieves the Java class that maps to the given GraphQL type name
	 * 
	 * @param typeName
	 *            The name of the GraphQL type, as found in the GraphQL schema
	 * @return
	 */
	public Class<?> getJavaClass(String typeName);
}
