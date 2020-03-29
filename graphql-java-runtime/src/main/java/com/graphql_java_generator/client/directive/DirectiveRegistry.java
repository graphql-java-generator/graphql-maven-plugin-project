/**
 * 
 */
package com.graphql_java_generator.client.directive;

import org.springframework.stereotype.Component;

import graphql.schema.GraphQLScalarType;

/**
 * Registry for all GraphQL directives that are available, that is: the GraphQL standard directives and the ones that
 * are defined in the GraphQL schema.
 * 
 * @author EtienneSF
 */
public interface DirectiveRegistry {

	/**
	 * This method registers all {@link GraphQLScalarType} that are declared as Spring {@link Component}. Another way to
	 * register {@link GraphQLScalarType} is to call the {@link #registerGraphQLScalarType(GraphQLScalarType)}.
	 */
	public void registerAllDirectives();

	/**
	 * Manually register one GraphQL directive.
	 * 
	 * @param directive
	 */
	public void registerDirective(Directive directive);

	/**
	 * Retrieves the registered {@link GraphQLScalarType} for this GraphQL CustomScalar.
	 * 
	 * @param name
	 * @return the {@link Directive}, or null if no directive has been registered for the given name
	 */
	public Directive getDirective(String name);

}
