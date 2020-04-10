package com.graphql_java_generator.samples.forum.client;

import java.util.Calendar;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.graphql.PartialDirectRequests;
import com.graphql_java_generator.samples.forum.client.graphql.PartialPreparedRequests;

/**
 * The main class, which executes the same queries, built by three different methods. See {@link PartialDirectRequests},
 * {@link PartialPreparedRequests}, {@link WithBuilder}
 * 
 * @author etienne-sf
 */
public class Main {

	public static String GRAPHQL_ENDPOINT_URL = "http://localhost:8180/graphql";

	public static void main(String[] args) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		System.out.println("");
		System.out.println("============================================================================");
		System.out.println("======= SIMPLEST WAY: DIRECT QUERIES =======================================");
		System.out.println("============================================================================");
		exec(new PartialDirectRequests(), null);

		System.out.println("");
		System.out.println("============================================================================");
		System.out.println("======= MOST SECURE WAY: PREPARED QUERIES ==================================");
		System.out.println("============================================================================");
		exec(new PartialPreparedRequests(), null);

		System.out.println("");
		System.out.println("");
		System.out.println("Sample application finished ... enjoy !    :)");
		System.out.println("");
		System.out.println("(please take a look at the other samples, for other use cases)");
	}

	static void exec(Queries client, String name)
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		try {

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  boardsSimple  --------------------------------------------");
			System.out.println(client.boardsSimple());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  topicAuthorPostAuthor  -----------------------------------");
			Calendar cal = Calendar.getInstance();
			cal.set(2018, 12, 20);
			System.out.println(client.topicAuthorPostAuthor("Board name 2", cal.getTime()));

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
