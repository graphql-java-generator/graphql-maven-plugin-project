package com.graphql_java_generator.plugin;

/**
 * Enumeration that defines the {@link CodeTemplate} available scopes
 * 
 * @author ggomez
 *
 */
public enum CodeTemplateScope {

	/**
	 * Scope for just client code, in the graphql goal
	 */
	CLIENT,

	/**
	 * Scope for server code, in the graphql goal
	 */
	SERVER,

	/**
	 * Scope for both client and server code, in the graphql goal
	 */
	COMMON,

	/**
	 * Scope for the generate-relay-schema goal
	 */
	GENERATE_RELAY_SCHEMA;

}
