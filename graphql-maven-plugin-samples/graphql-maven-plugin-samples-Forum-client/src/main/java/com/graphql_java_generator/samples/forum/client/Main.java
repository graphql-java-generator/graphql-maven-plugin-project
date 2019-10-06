package com.graphql_java_generator.samples.forum.client;

import com.graphql_java_generator.client.response.GraphQLExecutionException;
import com.graphql_java_generator.client.response.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.graphql.DirectQueries;
import com.graphql_java_generator.samples.forum.client.graphql.WithBuilder;
import com.graphql_java_generator.samples.forum.client.graphql.WithQueries;

/**
 * The main class, which executes the same queries, built by three different methods. See {@link DirectQueries},
 * {@link WithQueries}, {@link WithBuilder}
 * 
 * @author EtienneSF
 */
public class Main {

	public static String GRAPHQL_ENDPOINT_URL = "http://localhost:8180/graphql";

	public static void main(String[] args) throws GraphQLExecutionException, GraphQLRequestPreparationException {

		System.out.println("");
		System.out.println("============================================================================");
		System.out.println("======= SIMPLEST WAY: DIRECT QUERIES =======================================");
		System.out.println("============================================================================");
		exec(new DirectQueries(), null);

		System.out.println("");
		System.out.println("============================================================================");
		System.out.println("======= MOST SECURE WAY: PREPARED QUERIES ==================================");
		System.out.println("============================================================================");
		exec(new WithQueries(), null);

		System.out.println("");
		System.out.println("============================================================================");
		System.out.println("======= MOST SECURE WAY: PREPARED QUERIES ==================================");
		System.out.println("============================================================================");
		exec(new WithBuilder(), null);

		System.out.println("");
		System.out.println("");
		System.out.println("Sample application finished ... enjoy !    :)");
		System.out.println("");
		System.out.println("(please take a look at the other samples, for other use cases)");
	}

	static void exec(Queries client, String name) throws GraphQLExecutionException, GraphQLRequestPreparationException {
		try {

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  boardsSimple  --------------------------------------------");
			System.out.println(client.boardsSimple());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  topicAuthorPostAuthor  -----------------------------------");
			System.out.println(client.topicAuthorPostAuthor());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  createBoard  ---------------------------------------------");
			// We need a unique name. Let's use a random name for that, if none was provided.
			name = (name != null) ? name : "Name " + Float.floatToIntBits((float) Math.random() * Integer.MAX_VALUE);
			System.out.println(client.createBoard(name, true));

		} catch (javax.ws.rs.ProcessingException e) {
			System.out.println("");
			System.out.println("ERROR");
			System.out.println("");
			System.out.println(
					"Please start the server from the project graphql-maven-plugin-samples-StarWars-server, before executing the client part");
		}
	}

}
