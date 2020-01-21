/**
 * 
 */
package com.graphql_java_generator;

import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.graphql_java_generator.client.response.GraphQLRequestExecutionException;

/**
 * This abstract class MUST be implemented by the project for each Custom Scalar managed by its GraphQL schema. It's
 * used on both the client or the server, to provide JSON serialization and deserialization capabilities.
 * 
 * @author EtienneSF
 */
public abstract class AbstractCustomScalarConverter<T> extends StdSerializer<T> implements CustomScalarConverter<T> {

	private static final long serialVersionUID = 1L;

	/**
	 * The name for the GraphQL Custom Scalar type, that this converter can manage. This MUST be exactly the name
	 * defined in the GraphQL Schema.
	 */
	final String typeName;

	/**
	 * Indicates whether this GraphQL Scalar is :
	 * <UL>
	 * <LI>A String value and must be sent in JSON as a String. That is: surrounded with double-quotes. That's the case
	 * for strings, for dates...</LI>
	 * <LI>A non-String value. That is: the value is written in JSON without being surrounded by double-quotes. That's
	 * the case for integer, float, boolean, enumeration...</LI>
	 * </UL>
	 */
	final boolean stringValue;

	protected AbstractCustomScalarConverter(Class<T> t, String typeName, boolean stringValue) {
		super(t);
		this.typeName = typeName;
		this.stringValue = stringValue;
	}

	/**
	 * Convert the given String, as read in a message coming from or going to the GraphQL server to an instance of this
	 * Custom Scalar
	 * 
	 * @param str
	 *            The string to transform
	 * @return The str value, instanciated with the corresponding java value
	 * @throws GraphQLRequestExecutionException
	 */
	@Override
	abstract public T convertFromString(String str) throws GraphQLRequestExecutionException;

	/**
	 * Convert a Java value for this CustomScalar, as a String that can be written in a message coming from or going to
	 * the GraphQL server
	 * 
	 * @param o
	 * @return
	 * @throws GraphQLRequestExecutionException
	 */
	@Override
	abstract public String convertToString(Object o) throws GraphQLRequestExecutionException;

	/**
	 * Retrieves the name for the GraphQL Custom Scalar type, that this converter can manage. This MUST be exactly the
	 * name defined in the GraphQL Schema.
	 */
	@Override
	public String getTypeName() {
		return this.typeName;
	}

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
	@Override
	public boolean isStringValue() {
		return stringValue;
	}

}
