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

	Map<String, CustomScalarConverter<?>> converters = new HashMap<>();

	/** {@inheritDoc} */
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

}
