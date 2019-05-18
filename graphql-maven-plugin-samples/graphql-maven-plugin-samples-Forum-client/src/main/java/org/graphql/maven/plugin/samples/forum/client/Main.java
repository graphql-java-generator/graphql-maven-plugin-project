package org.graphql.maven.plugin.samples.forum.client;

import org.graphql.maven.plugin.samples.forum.client.graphql.DirectQueries;
import org.graphql.maven.plugin.samples.forum.client.graphql.WithBuilder;
import org.graphql.maven.plugin.samples.forum.client.graphql.WithQueries;

import graphql.java.client.response.GraphQLExecutionException;
import graphql.java.client.response.GraphQLRequestPreparationException;

/**
 * The main class, which executes the same queries, built by three different methods. See {@link DirectQueries},
 * {@link WithQueries}, {@link WithBuilder}
 * 
 * @author EtienneSF
 */
public class Main {

	public static void main(String[] args) throws GraphQLExecutionException, GraphQLRequestPreparationException {
		System.out.println("============================================================================");
		System.out.println("======= SIMPLEST WAY: DIRECT QUERIES =======================================");
		System.out.println("============================================================================");
		exec(new DirectQueries());

		System.out.println("============================================================================");
		System.out.println("======= MOST SECURE WAY: PREPARED QUERIES ==================================");
		System.out.println("============================================================================");
		exec(new WithQueries());

		System.out.println("============================================================================");
		System.out.println("======= MOST SECURE WAY: PREPARED QUERIES ==================================");
		System.out.println("============================================================================");
		exec(new WithBuilder());

		System.out.println("");
		System.out.println("");
		System.out.println("Sample application finished ... enjoy !    :)");
		System.out.println("");
		System.out.println("(please take a look at the other samples, for other use cases)");
	}

	static void exec(Queries client) throws GraphQLExecutionException, GraphQLRequestPreparationException {
		try {

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  boardsSimple  --------------------------------------------");
			System.out.println(client.boardsSimple());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  topicAuthorPostAuthor  -----------------------------------");
			System.out.println(client.topicAuthorPostAuthor());

		} catch (javax.ws.rs.ProcessingException e) {
			System.out.println("");
			System.out.println("ERROR");
			System.out.println("");
			System.out.println(
					"Please start the server from the project graphql-maven-plugin-samples-StarWars-server, before executing the client part");
		}
	}

}
