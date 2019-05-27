package org.graphql.maven.plugin.samples.forum.client.graphql;

import java.util.List;

import org.graphql.maven.plugin.samples.forum.client.Queries;
import org.graphql.maven.plugin.samples.forum.client.graphql.forum.client.Board;
import org.graphql.maven.plugin.samples.forum.client.graphql.forum.client.MutationType;
import org.graphql.maven.plugin.samples.forum.client.graphql.forum.client.QueryType;
import org.graphql.maven.plugin.samples.forum.client.graphql.forum.client.Topic;

import graphql.java.client.response.GraphQLExecutionException;
import graphql.java.client.response.GraphQLRequestPreparationException;

/**
 * This class implements the simplest way to call GraphQl queries, with the GraphQL Java Generator
 * 
 * @author EtienneSF
 */
public class DirectQueries implements Queries {

	QueryType queryType = new QueryType();
	MutationType mutationType = new MutationType();

	@Override
	public List<Board> boardsSimple() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		// No field specified: all known scalar fields of the root type will be queried
		return queryType.boards("");
	}

	@Override
	public List<Board> boardsAndTopics() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		// Used to check that a newly created Board has no topic
		return queryType.boards("{id name publiclyAvailable topics{id}}");
	}

	@Override
	public List<Topic> topicAuthorPostAuthor() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.topics(
				"{id date author{name email alias id type} nbPosts title content posts{id date author{name email alias} title content}}",
				"Board name 2");
	}

	@Override
	public Board createBoard(String name, boolean publiclyAvailable)
			throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return mutationType.createBoard("{id name publiclyAvailable}", name, publiclyAvailable);
	}
}
