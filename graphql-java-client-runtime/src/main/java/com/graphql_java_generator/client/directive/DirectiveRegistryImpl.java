/**
 * 
 */
package com.graphql_java_generator.client.directive;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author etienne-sf
 *
 */
@Component
public class DirectiveRegistryImpl implements DirectiveRegistry {

	@Autowired
	ApplicationContext ctx;

	////////////////////////////////////////////////////////////////////////////////
	// Start of static methods

	/**
	 * As we may have or not have Spring at runtime, we manually manage a singleton. This field is private, and should
	 * only be accessed through {@link #getDirectiveRegistry()}.
	 */
	private static Map<String, DirectiveRegistry> directiveRegistries = new HashMap<>();

	/**
	 * Creates and register the {@link DirectiveRegistry} for the given schema, only if it has not already been
	 * registered.
	 * 
	 * @param schema
	 *            value of the <i>springBeanSuffix</i> plugin parameter for the searched schema. When there is only one
	 *            schema, this plugin parameter is usually not set. In this case, its default value ("") is used.
	 * @param directiveRegistryInitializer
	 *            The function that created the {@link DirectiveRegistry}. It will be called only if needed, that is: if
	 *            the {@link DirectiveRegistry} for this schema has not already been registered
	 */
	public static void registerDirectiveRegistry(String schema,
			Consumer<DirectiveRegistry> directiveRegistryInitializer) {
		if (!directiveRegistries.containsKey(schema)) {
			DirectiveRegistry directiveRegistry = new DirectiveRegistryImpl();
			directiveRegistryInitializer.accept(directiveRegistry);
			directiveRegistries.put(schema, directiveRegistry);
		}
	}

	/**
	 * Retrieves the directive of the given name, from the given schema
	 * 
	 * @param schema
	 *            value of the <i>springBeanSuffix</i> plugin parameter for the searched schema. When there is only one
	 *            schema, this plugin parameter is usually not set. In this case, its default value ("") is used.
	 * @param directiveName
	 */
	public static Directive getDirective(String schema, String directiveName) {
		return directiveRegistries.get(schema).getDirective(directiveName);
	}

	// End of static methods
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Map of all registered directives, for one schema.
	 */
	private static Map<String, Directive> directiveTypes = new HashMap<>();

	/**
	 * {@inheritDoc}<BR/>
	 * This implementation works only if this class has been loaded as a Spring Component.
	 */
	@Override
	public void registerAllDirectives() {
		for (Directive type : ctx.getBeansOfType(Directive.class).values()) {
			registerDirective(type);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void registerDirective(Directive type) {
		directiveTypes.put(type.getName(), type);
	}

	/** {@inheritDoc} */
	@Override
	public Directive getDirective(String name) {
		return directiveTypes.get(name);
	}

}
