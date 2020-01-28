/**
 * 
 */
package com.graphql_java_generator;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import graphql.schema.GraphQLScalarType;

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
	Map<String, GraphQLScalarType> customScalarTypes = new HashMap<>();

	/**
	 * {@inheritDoc}<BR/>
	 * This implementation works only if this class has been loaded as a Spring Component.
	 */
	@Override
	public void registerAllCustomScalarConverters() {
		for (GraphQLScalarType type : ctx.getBeansOfType(GraphQLScalarType.class).values()) {
			registerOneCustomScalarConverter(type);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void registerOneCustomScalarConverter(GraphQLScalarType type) {
		customScalarTypes.put(type.getName(), type);
	}

	/** {@inheritDoc} */
	@Override
	public GraphQLScalarType getCustomScalarConverter(String graphQLTypeName) {
		return customScalarTypes.get(graphQLTypeName);
	}

}
