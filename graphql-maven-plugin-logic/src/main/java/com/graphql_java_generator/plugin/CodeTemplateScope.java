package com.graphql_java_generator.plugin;

/**
 * Enumeration that defines the {@link CodeTemplate} scopes avaible
 * @author ggomez
 *
 */
public enum CodeTemplateScope {
	
	/**
	 * Scope for just client code
	 */
	CLIENT,
	
	/**
	 * Scope for server code
	 */
	SERVER,
	
	/**
	 * Scope for both client and server code
	 */
	COMMON;

}
