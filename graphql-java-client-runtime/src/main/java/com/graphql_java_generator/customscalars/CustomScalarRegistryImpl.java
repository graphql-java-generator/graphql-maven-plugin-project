/**
 * 
 */
package com.graphql_java_generator.customscalars;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import graphql.schema.GraphQLScalarType;

/**
 * @author etienne-sf
 *
 */
@Component
public class CustomScalarRegistryImpl implements CustomScalarRegistry {

	@Autowired
	ApplicationContext ctx;

	/**
	 * As we may have or not have Spring at runtime, we manually manage a singleton, that contains the custom scalar
	 * registration for each schema. This field is private, and can only be accessed through
	 * {@link #getCustomScalarRegistry()}.
	 */
	private static Map<String, CustomScalarRegistry> customScalarRegistries = new HashMap<>();

	/**
	 * Map of all registered Custom Scalars. The key is the type name or the Custom Scalar, as defined in the GraphQL
	 * schema.
	 */
	Map<String, CustomScalar> customScalarTypes = new HashMap<>();

	@Override
	public void registerGraphQLScalarType(String typeName, GraphQLScalarType type, Class<?> valueClazz) {
		this.customScalarTypes.put(//
				typeName, //
				new CustomScalar(GraphQLScalarType.newScalar(type).name(typeName).build(), valueClazz));
	}

	@Override
	public GraphQLScalarType getGraphQLCustomScalarType(String graphQLTypeName) {
		CustomScalar scalar = this.customScalarTypes.get(graphQLTypeName);
		return (scalar == null) ? null : scalar.getGraphQLScalarType();
	}

	@Override
	public CustomScalar getCustomScalar(String graphQLTypeName) {
		return this.customScalarTypes.get(graphQLTypeName);
	}

	/**
	 * Indicates whether the {@link CustomScalarRegistry} for the given schema has been initialized.
	 * 
	 * @param schema
	 *            value of the <i>springBeanSuffix</i> plugin parameter for the searched schema. When there is only one
	 *            schema, this plugin parameter is usually not set. In this case, its default value ("") is used.
	 * @return true if this registry is already initialized
	 * @throws IllegalArgumentException
	 *             If no {@link CustomScalarRegistry} has been defined for the given schema
	 */
	static public boolean isCustomScalarRegistryInitialized(String schema) {
		return customScalarRegistries.get(schema) != null;
	}

	/**
	 * Retrieves the {@link CustomScalarRegistry} for the given schema. This registry is initialized in the generated
	 * class <code>CustomScalarRegistryInitializer</code>. <br/>
	 * Note: this method is an internal utility method.
	 * 
	 * @param schema
	 *            value of the <i>springBeanSuffix</i> plugin parameter for the searched schema. When there is only one
	 *            schema, this plugin parameter is usually not set. In this case, its default value ("") is used.
	 * @return
	 * @throws IllegalArgumentException
	 *             If no {@link CustomScalarRegistry} has been defined for the given schema
	 */
	static public CustomScalarRegistry getCustomScalarRegistry(String schema) {
		CustomScalarRegistry ret = customScalarRegistries.get(schema);
		if (ret == null) {
			throw new IllegalArgumentException(
					"Unknown schema: The CustomScalarRegistry for the '" + schema + "' schema has not been defined");
		}
		return ret;
	}

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
	static public void setCustomScalarRegistry(String schema, CustomScalarRegistry customScalarRegistry) {
		customScalarRegistries.put(schema, customScalarRegistry);
	}
}
