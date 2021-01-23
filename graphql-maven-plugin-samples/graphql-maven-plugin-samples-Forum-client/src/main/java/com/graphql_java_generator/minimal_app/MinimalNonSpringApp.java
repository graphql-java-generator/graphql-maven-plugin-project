package com.graphql_java_generator.minimal_app;

import java.util.Date;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.QueryTypeExecutor;

public class MinimalNonSpringApp {

	public static void main(String[] args) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// The executor, that allows to execute GraphQL queries. The class name is the one defined in the GraphQL
		// schema.
		// You can instanciate in the same way the mutation and the subscription executors.
		QueryTypeExecutor queryExecutor = new QueryTypeExecutor("http://localhost:8180/graphql");

		// A basic demo of input parameters
		Date date = new Date(2019 - 1900, 12 - 1, 20);

		// For this simple sample, we execute a direct query. But prepared queries are recommended.
		// Please note that input parameters are mandatory for list or input types.
		System.out.println(
				"Executing query: '{id name publiclyAvailable topics(since: &param){id}}', with input parameter param of value '"
						+ date + "'");
		System.out
				.println(queryExecutor.boards("{id name publiclyAvailable topics(since: &param){id}}", "param", date));
	}
}
