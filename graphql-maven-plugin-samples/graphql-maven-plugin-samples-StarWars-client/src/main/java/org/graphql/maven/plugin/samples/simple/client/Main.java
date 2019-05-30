package org.graphql.maven.plugin.samples.simple.client;

import org.graphql.maven.plugin.samples.simple.client.graphql.DirectQueries;
import org.graphql.maven.plugin.samples.simple.client.graphql.WithBuilder;
import org.graphql.maven.plugin.samples.simple.client.graphql.WithQueries;

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

		// Execution of three way to user the GraphQL client, to call the GraphQL server

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

	public static void exec(Queries client) throws GraphQLExecutionException, GraphQLRequestPreparationException {
		// A random value, to variabilize mutations
		int i = (int) (Math.random() * Integer.MAX_VALUE);

		try {

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  heroSimple  ----------------------------------------------");
			System.out.println(client.heroPartial());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  heroFriendsFriendsFriends  -------------------------------");
			System.out.println(client.heroFriendsFriendsFriends());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  humanSimple  ----------------------------------------------");
			System.out.println(client.humanPartial());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  humanFriendsFriendsFriends  ------------------------------");
			System.out.println(client.humanFriendsFriendsFriends());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  droidSimple  ----------------------------------------------");
			System.out.println(client.droidPartial());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  droidFriendsFriendsFriends  ------------------------------");
			System.out.println(client.droidPartial());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  droidDoesNotExist  ---------------------------------------");
			System.out.println(client.droidDoesNotExist());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  createHuman  ---------------------------------------------");
			System.out.println(client.createHuman("A new Human (" + i++ + ")", "A random planet"));

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  addFriend  -----------------------------------------------");
			System.out.println(client.droidDoesNotExist());

		} catch (javax.ws.rs.ProcessingException e) {
			System.out.println("");
			System.out.println("ERROR");
			System.out.println("");
			System.out.println(
					"Please start the server from the project graphql-maven-plugin-samples-StarWars-server, before executing the client part");
		}
	}

}
