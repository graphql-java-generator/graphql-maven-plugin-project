package org.graphql.maven.plugin.samples.forum.client;

import java.util.List;

import org.graphql.maven.plugin.samples.forum.client.forum.client.Board;
import org.graphql.maven.plugin.samples.forum.client.forum.client.QueryType;
import org.graphql.maven.plugin.samples.forum.client.forum.client.Topic;

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
			System.out.println("----------------  boardsSimple  --------------------------------------------");
			System.out.println(client.boardsSimple());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  topicAuthorPostAuthor  -----------------------------------");
			System.out.println(client.topicAuthorPostAuthor());

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

	public List<Board> boardsSimple() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.boards("{id name publiclyAvailable}");
	}

	public List<Topic> topicAuthorPostAuthor() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.topics(
				"{id date author{name email alias id type} nbPosts title content posts{id date author{name email alias} title content}}",
				"Board name 2");
	}
}
