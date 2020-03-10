/**
 * 
 */
package com.graphql_java_generator.directive;

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
public class DirectiveRegistryImpl implements DirectiveRegistry {

	@Autowired
	ApplicationContext ctx;

	/**
	 * As we may have or not have Spring at runtime, we manually manage a singleton. This field is private, and should
	 * only be accessed through {@link #getDirectiveRegistry()}.
	 */
	public static DirectiveRegistry directiveRegistry = new DirectiveRegistryImpl();

	/**
	 * Map of all registered Custom Scalars. The key is the type name or the Custom Scalar, as defined in the GraphQL
	 * schema.
	 */
	static Map<String, Directive> directiveTypes = new HashMap<>();

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
