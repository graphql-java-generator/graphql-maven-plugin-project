package com.graphql_java_generator.client;

/**
 * Interfaces that marks the generated classes of the same name. These classes contain various implementation, specific
 * to the generated code, that are used by the client runtime.
 */
public interface RegistriesInitializer {

	/**
	 * The package name, where the GraphQL generated classes are. It's used to load the class definition, and get the
	 * GraphQL metadata coming from the GraphQL schema.
	 */
	public String getPackageName();

	/**
	 * Retrive the value of the <i>springBeanSuffix</i> plugin parameter for the searched schema. When there is only one
	 * schema, this plugin parameter is usually not set. In this case, its default value ("") is used
	 */
	public String getSchema();

	/**
	 * The class that will contain the responses for GraphQL queries.
	 * 
	 * @return the class to map the json return to
	 */
	public Class<?> getQueryRootResponseClass();

	/**
	 * The class that will contain the responses for GraphQL mutations.
	 * 
	 * @return the class to map the json return to
	 */
	public Class<?> getMutationRootResponseClass();

	/**
	 * The class that will contain the responses for GraphQL subscriptions.
	 * 
	 * @return the class to map the json return to
	 */
	public Class<?> getSubscriptionRootResponseClass();
}
