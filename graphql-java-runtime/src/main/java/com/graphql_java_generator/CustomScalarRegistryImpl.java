/**
 * 
 */
package com.graphql_java_generator;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author EtienneSF
 *
 */
@Component
public class CustomScalarRegistryImpl implements CustomScalarRegistry {

	@Autowired
	ApplicationContext ctx;

	/**
	 * As we may have or not have Spring at runtime, we manually manage a singleton. This field is private, and should
	 * only be accessed through {@link #getCustomScalarRegistry()}.
	 */
	public static CustomScalarRegistry customScalarRegistry = new CustomScalarRegistryImpl();

	/**
	 * Map of all registered Custom Scalars. The key is the type name or the Custom Scalar, as defined in the GraphQL
	 * schema.
	 */
	Map<String, CustomScalarConverter<?>> converters = new HashMap<>();

	/**
	 * {@inheritDoc}<BR/>
	 * This implementation works only if this class has been loaded as a Spring Component.
	 */
	@Override
	public void registerAllCustomScalarConverters() {
		for (CustomScalarConverter<?> converter : ctx.getBeansOfType(CustomScalarConverter.class).values()) {
			registerOneCustomScalarConverter(converter);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void registerOneCustomScalarConverter(CustomScalarConverter<?> converter) {
		converters.put(converter.getTypeName(), converter);
	}

	/** {@inheritDoc} */
	@Override
	public CustomScalarConverter<?> getCustomScalarConverter(String graphQLTypeName) {
		return converters.get(graphQLTypeName);
	}

}
