/**
 * 
 */
package com.graphql_java_generator;

/**
 * This interface must be implemented by the project, either the client or the server, to provide
 * 
 * @author EtienneSF
 */
public interface CustomScalarConverter<T> {

	/**
	 * This method registers the current converter in the given registry. All converters must be registered, so that
	 * they can be used at runtime.
	 * 
	 * @param registry
	 * @See {@link CustomScalarRegistry}
	 */
	public void register(CustomScalarRegistry registry);

	/**
	 * Returns the type name, managed by this converter, as defined in the GraphQL schema
	 * 
	 * @return
	 */
	public String getTypeName();

	/**
	 * Convert the given String, as read in a message coming from or going to the GraphQL server to an instance of this
	 * Custom Scalar
	 * 
	 * @param str
	 *            The string to transform
	 * @return The str value, instanciated with the corresponding java value
	 */
	public T convertFromString(String str);

	/**
	 * Convert a Java value for this CustomScalar, as a String that can be written in a message coming from or going to
	 * the GraphQL server
	 * 
	 * @param t
	 * @return
	 */
	public String convertToString(T t);

}
