package org.graphql.maven.plugin.samples.forum.client.graphql;

import java.util.List;

import org.graphql.maven.plugin.samples.forum.client.Queries;
import org.graphql.maven.plugin.samples.forum.client.graphql.forum.client.Board;
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

	@Override
	public List<Board> boardsSimple() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.boards("{id name publiclyAvailable}");
	}

	@Override
	public List<Topic> topicAuthorPostAuthor() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.topics(
				"{id date author{name email alias id type} nbPosts title content posts{id date author{name email alias} title content}}",
				"Board name 2");
	}
}
