package com.graphql_java_generator.minimal_app;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.MutationExecutor;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.QueryExecutor;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.SubscriptionExecutor;

@SpringBootApplication(scanBasePackageClasses = { MinimalSpringApp.class, GraphqlClientUtils.class,
		QueryExecutor.class })
public class MinimalSpringApp implements CommandLineRunner {

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
		// A basic demo of input parameters
		@SuppressWarnings("deprecation")
		Date date = new Date(2019 - 1900, 12 - 1, 20);

		// For this simple sample, we execute a direct query. But prepared queries are recommended.
		// Please note that input parameters are mandatory for list or input types.
		System.out.println(
				"Executing query: '{id name publiclyAvailable topics(since: &param){id}}', with input parameter param of value '"
						+ date + "'");
		System.out
				.println(queryExecutor.boards("{id name publiclyAvailable topics(since: &param){id}}", "param", date));
		System.out.println("");
		System.out.println("Normal end of the application");
	}
}
