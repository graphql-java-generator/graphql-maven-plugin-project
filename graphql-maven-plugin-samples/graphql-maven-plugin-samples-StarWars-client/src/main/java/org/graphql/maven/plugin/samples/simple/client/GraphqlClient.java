package org.graphql.maven.plugin.samples.simple.client;

import com.generated.graphql.Character;
import com.generated.graphql.Droid;
import com.generated.graphql.Episode;
import com.generated.graphql.Human;
import com.generated.graphql.QueryType;

import graphql.java.client.response.GraphQLExecutionException;
import graphql.java.client.response.GraphQLRequestPreparationException;

/**
 * Hello world!
 *
 */
public class GraphqlClient {

	QueryType queryType = new QueryType();

	public static void main(String[] args) throws GraphQLExecutionException, GraphQLRequestPreparationException {
		try {
			GraphqlClient client = new GraphqlClient();

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  heroSimple  ----------------------------------------------");
			System.out.println(client.heroSimple());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  heroFriendsFriendsFriends  -------------------------------");
			System.out.println(client.heroFriendsFriendsFriends());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  humanSimple  ----------------------------------------------");
			System.out.println(client.humanSimple());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  humanFriendsFriendsFriends  ------------------------------");
			System.out.println(client.humanFriendsFriendsFriends());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  droidSimple  ----------------------------------------------");
			System.out.println(client.droidSimple());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  droidFriendsFriendsFriends  ------------------------------");
			System.out.println(client.droidSimple());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  droidDoesNotExist  ---------------------------------------");
			System.out.println(client.droidDoesNotExist());

			System.out.println("");
			System.out.println("");
			System.out.println("Sample application finished ... enjoy !    :)");
			System.out.println("");
			System.out.println("(please take a look at the other samples, for other use cases)");
		} catch (javax.ws.rs.ProcessingException e) {
			System.out.println("");
			System.out.println("ERROR");
			System.out.println("");
			System.out.println(
					"Please start the server from the project graphql-maven-plugin-samples-StarWars-server, before executing the client part");
		}
	}

	public Character heroSimple() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.hero("{id appearsIn name}", Episode.NEWHOPE);
	}

	public Character heroFriendsFriendsFriends() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.hero("{id appearsIn friends {name friends {friends{id name appearsIn}}}}", Episode.NEWHOPE);
	}

	public Human humanSimple() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.human("{id appearsIn homePlanet name}", "45");
	}

	public Human humanFriendsFriendsFriends() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.human("{id appearsIn name friends {name friends {friends{id name appearsIn}}}}", "180");
	}

	public Droid droidSimple() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.droid("{id appearsIn primaryFunction name}", "3");
	}

	public Droid droidFriendsFriendsFriends() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType
				.droid("{id appearsIn name friends {name friends {friends{id name appearsIn}}} primaryFunction }", "2");
	}

	public Droid droidDoesNotExist() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.droid("{id appearsIn friends {name friends {friends{id name appearsIn}}} primaryFunction }",
				"doesn't exist");
	}

}
