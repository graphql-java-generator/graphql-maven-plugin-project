/**
 * 
 */
package com.graphql_java_generator;

import org.springframework.stereotype.Component;

/**
 * Registry for all {@link CustomScalarConverter} available.
 * 
 * @author EtienneSF
 */
public interface CustomScalarRegistry {

	/**
	 * This method registers all {@link CustomScalarConverter} that are declared as Spring {@link Component}. Another
	 * way to register {@link CustomScalarConverter} is to call the
	 * {@link #registerOneCustomScalarConverter(CustomScalarConverter)}.
	 */
	public void registerAllCustomScalarConverters();

	/**
	 * Manually register one {@link CustomScalarConverter}.
	 * 
	 * @param converter
	 */
	public void registerOneCustomScalarConverter(CustomScalarConverter<?> converter);

	/**
	 * Retrieves the registered {@link CustomScalarConverter} for this GraphQL CustomScalar.
	 * 
	 * @param graphQLTypeName
	 * @return the Custom Scalar Converter, or null if no converter has been registered for the given name
	 */
	public CustomScalarConverter<?> getCustomScalarConverter(String graphQLTypeName);

}
