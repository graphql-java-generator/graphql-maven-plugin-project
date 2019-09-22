package com.graphql_java_generator.samples.basic.client;

import com.generated.graphql.Query;
import com.graphql_java_generator.client.response.GraphQLExecutionException;
import com.graphql_java_generator.client.response.GraphQLRequestPreparationException;

/**
 * Hello world!
 *
 */
public class App {

	public static void main(String[] args) throws GraphQLExecutionException, GraphQLRequestPreparationException {
		Query query = new Query("http://localhost:8180/graphql");
		System.out.println(query.hello("", "world"));
	}
}
