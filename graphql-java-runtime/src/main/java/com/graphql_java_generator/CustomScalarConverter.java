/**
 * 
 */
package com.graphql_java_generator;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * This abstract class MUST be implemented by the project for each Custom Scalar managed by its GraphQL schema. It's
 * used on both the client or the server, to provide JSON serialization and deserialization capabilities.
 * 
 * @author EtienneSF
 */
public interface CustomScalarConverter<T> {
	/**
	 * Convert the given String, as read in a message coming from or going to the GraphQL server to an instance of this
	 * Custom Scalar
	 * 
	 * @param str
	 *            The string to transform
	 * @return The str value, instanciated with the corresponding java value
	 * @throws GraphQLRequestExecutionException
	 *             When an error occurs during conversion
	 */
	abstract public T convertFromString(String str) throws GraphQLRequestExecutionException;

	/**
	 * Convert a Java value for this CustomScalar, as a String that can be written in a message coming from or going to
	 * the GraphQL server
	 * 
	 * @param t
	 * @return
	 * @throws GraphQLRequestExecutionException
	 *             When an error occurs during conversion
	 */
	abstract public String convertToString(Object o) throws GraphQLRequestExecutionException;

	/**
	 * Retrieves the name for the GraphQL Custom Scalar type, that this converter can manage. This MUST be exactly the
	 * name defined in the GraphQL Schema.
	 */
	public String getTypeName();

	/**
	 * Indicates whether the target type is :
	 * <UL>
	 * <LI>A String value and must be sent in JSON as a String. That is: surrounded with double-quotes. That's the case
	 * for strings, for dates...</LI>
	 * <LI>A non-String value. That is: the value is written in JSON without being surrounded by double-quotes. That's
	 * the case for integer, float, boolean, enumeration...</LI>
	 * </UL>
	 *
	 * @return True if the target type is a String value
	 */
	public boolean isStringValue();

}
