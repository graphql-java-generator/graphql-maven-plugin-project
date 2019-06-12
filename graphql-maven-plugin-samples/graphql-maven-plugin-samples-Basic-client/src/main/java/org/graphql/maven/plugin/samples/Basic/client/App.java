package org.graphql.maven.plugin.samples.Basic.client;

import com.generated.graphql.Query;

import graphql.java.client.response.GraphQLExecutionException;
import graphql.java.client.response.GraphQLRequestPreparationException;

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
