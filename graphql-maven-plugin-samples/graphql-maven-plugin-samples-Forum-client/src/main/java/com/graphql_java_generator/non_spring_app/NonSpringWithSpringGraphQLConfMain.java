/**
 * 
 */
package com.graphql_java_generator.non_spring_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.minimal_app.MinimalSpringApp;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.util.MutationExecutor;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.util.QueryExecutor;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.util.SubscriptionExecutor;

/**
 * This class demonstrates how to use the Spring Boot configuration capabilities, with a non spring app. It can be used
 * to use this plugin, and its configuration capabilities (Spring Security, OAuth, https, and all what's permitted by
 * Spring Boot), along with an <B>already existing non spring app</B>.<BR/>
 * The general idea is to have a Spring app, that loads the context and all the GraphQL stuff. Then, call the non spring
 * app. Getters then allow to retrieve the GraphQL components.
 * 
 * @author etienne-sf
 */
@SpringBootApplication(scanBasePackageClasses = { MinimalSpringApp.class, GraphqlClientUtils.class,
		QueryExecutor.class })
public class NonSpringWithSpringGraphQLConfMain implements CommandLineRunner {

	/**
	 * This singleton allows the static getters to retrieve the Spring components that have been autowired from Spring
	 */
	private static NonSpringWithSpringGraphQLConfMain nonSpringWithSpringGraphQLConfApp;

	/**
	 * The executor, that allows to execute GraphQL queries. The class name is the one defined in the GraphQL schema.
	 */
	@Autowired
	QueryExecutor queryExecutor;

	/**
	 * The executor, that allows to execute GraphQL mutations. The class name is the one defined in the GraphQL schema.
	 * It will be null if no mutation has been defined.
	 */
	@Autowired(required = false)
	MutationExecutor mutationExecutor;

	/**
	 * The executor, that allows to execute GraphQL subscriptions. The class name is the one defined in the GraphQL
	 * schema. It will be null if no subscription has been defined.
	 */
	@Autowired(required = false)
	SubscriptionExecutor subscriptionExecutor;

	public static void main(String[] args) {
		SpringApplication.run(MinimalSpringApp.class, args);
	}

	/**
	 * This method is started by Spring, once the Spring context has been loaded. This is run, as this class implements
	 * {@link CommandLineRunner}
	 */
	@Override
	public void run(String... args) throws Exception {
		nonSpringWithSpringGraphQLConfApp = this;
		// The Spring context is now created, including the GraphQL stuff. Let's start the non Spring app
		OldMain.main(args);
	}

	/**
	 * Getter for the {@link QueryExecutor} that has been loaded by Spring
	 * 
	 * @return
	 */
	public static QueryExecutor getQueryExecutor() {
		return nonSpringWithSpringGraphQLConfApp.queryExecutor;
	}

	/**
	 * Getter for the {@link SubscriptionExecutor} that has been loaded by Spring
	 * 
	 * @return
	 */
	public static SubscriptionExecutor getSubscriptionExecutor() {
		return nonSpringWithSpringGraphQLConfApp.subscriptionExecutor;
	}
}
