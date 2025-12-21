/**
 * 
 */
package com.graphql_java_generator.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.customscalars.CustomScalar;

import graphql.schema.GraphQLScalarType;

/**
 * @author etienne-sf
 *
 */
@Component
public class CustomScalarRegistryImpl implements CustomScalarRegistry {

	@Autowired
	ApplicationContext ctx;

	////////////////////////////////////////////////////////////////////////////////
	// Start of static methods

	/**
	 * As we may have or not have Spring at runtime, we manually manage a singleton, that contains the custom scalar
	 * registration for each schema. This field is private, and can only be accessed through
	 * {@link #getCustomScalarRegistry()}.
	 */
	private static Map<String, CustomScalarRegistry> customScalarRegistries = new HashMap<>();

	/**
	 * Sets the {@link CustomScalarRegistry} for the given schema. This method should only be called from the generated
	 * class <code>CustomScalarRegistryInitializer</code>. <br/>
	 * Note: this method is an internal utility method.
	 * 
	 * @param schema
	 *            value of the <i>springBeanSuffix</i> plugin parameter for the searched schema. When there is only one
	 *            schema, this plugin parameter is usually not set. In this case, its default value ("") is used.
	 * @param customScalarRegistry
	 *            The {@link CustomScalarRegistry} associated with this schema
	 */
	public static void registerCustomScalarRegistry(String schema,
			Consumer<CustomScalarRegistry> customScalarRegistryInitializer) {
		if (customScalarRegistries.containsKey(schema)) {
			throw new IllegalArgumentException(
					"The CustomScalarRegistry for the '" + schema + "' schema has already been defined");
		}
		CustomScalarRegistry customScalarRegistry = new CustomScalarRegistryImpl();
		customScalarRegistryInitializer.accept(customScalarRegistry);
		customScalarRegistries.put(schema, customScalarRegistry);
	}

	/**
	 * Retrieves the registered {@link GraphQLScalarType} for this GraphQL CustomScalar.
	 * 
	 * @param graphQLTypeName
	 * @return the {@link GraphQLScalarType}, or null if no converter has been registered for the given name
	 */
	public static GraphQLScalarType getGraphQLCustomScalarType(String schema, String graphQLTypeName) {
		return customScalarRegistries.get(schema).getGraphQLCustomScalarType(graphQLTypeName);
	}

	/**
	 * Retrieves the registered {@link GraphQLScalarType} for this GraphQL CustomScalar.
	 * 
	 * @param graphQLTypeName
	 * @return the {@link GraphQLScalarType}, or null if no converter has been registered for the given name
	 */
	public static CustomScalar getCustomScalar(String schema, String graphQLTypeName) {
		return customScalarRegistries.get(schema).getCustomScalar(graphQLTypeName);
	}
	// End of static methods
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Map of all registered Custom Scalars. The key is the type name or the Custom Scalar, as defined in the GraphQL
	 * schema.
	 */
	Map<String, CustomScalar> customScalarTypes = new HashMap<>();

	@Override
	public void registerGraphQLScalarType(String typeName, GraphQLScalarType type, Class<?> valueClazz) {
		customScalarTypes.put(//
				typeName, //
				new CustomScalar(GraphQLScalarType.newScalar(type).name(typeName).build(), valueClazz));
	}

	@Override
	public GraphQLScalarType getGraphQLCustomScalarType(String graphQLTypeName) {
		CustomScalar scalar = customScalarTypes.get(graphQLTypeName);
		return (scalar == null) ? null : scalar.getGraphQLScalarType();
	}

	@Override
	public CustomScalar getCustomScalar(String graphQLTypeName) {
		return customScalarTypes.get(graphQLTypeName);
	}

}
