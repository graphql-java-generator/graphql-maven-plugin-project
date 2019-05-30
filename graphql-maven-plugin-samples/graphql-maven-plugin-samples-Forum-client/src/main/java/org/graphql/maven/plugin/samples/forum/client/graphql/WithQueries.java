package org.graphql.maven.plugin.samples.forum.client.graphql;

import java.util.List;

import org.graphql.maven.plugin.samples.forum.client.Main;
import org.graphql.maven.plugin.samples.forum.client.Queries;
import org.graphql.maven.plugin.samples.forum.client.graphql.forum.client.Board;
import org.graphql.maven.plugin.samples.forum.client.graphql.forum.client.MutationType;
import org.graphql.maven.plugin.samples.forum.client.graphql.forum.client.QueryType;
import org.graphql.maven.plugin.samples.forum.client.graphql.forum.client.Topic;

import graphql.java.client.request.ObjectResponse;
import graphql.java.client.response.GraphQLExecutionException;
import graphql.java.client.response.GraphQLRequestPreparationException;

/**
 * This class implements the away to call GraphQl queries, where all queries are prepared before execution.<BR/>
 * The advantages are:
 * <UL>
 * <LI>Performance: this avoid to build an {@link ObjectResponse} for each response. This {@link ObjectResponse} is
 * useful, to help control at runtime if a field has been queried or not. It allows to throw an exception when your code
 * tries to use a field that was not queried</LI>
 * <LI>Security: as all request have been prepared at startup, this make sure at startup that your queries are
 * valid.</LI>
 * </UL>
 * 
 * @author EtienneSF
 */
public class WithQueries implements Queries {

	QueryType queryType = new QueryType(Main.graphqlEndpoint);
	MutationType mutationType = new MutationType(Main.graphqlEndpoint);
	ObjectResponse boardsSimpleResponse;
	ObjectResponse boardsAndTopicsResponse;
	ObjectResponse topicAuthorPostAuthorResponse;
	ObjectResponse createBoardResponse;

	public WithQueries() throws GraphQLRequestPreparationException {
		// No field specified: all known scalar fields of the root type will be queried
		boardsSimpleResponse = queryType.getBoardsResponseBuilder().build();

		topicAuthorPostAuthorResponse = queryType.getTopicsResponseBuilder().withQueryResponseDef(
				"{id date author{name email alias id type} nbPosts title content posts{id date author{name email alias} title content}}")
				.build();

		createBoardResponse = mutationType.getCreateBoardResponseBuilder().build();
	}

	ObjectResponse getBoardsAndTopics() throws GraphQLRequestPreparationException {
		if (boardsAndTopicsResponse == null) {
			boardsAndTopicsResponse = queryType.getBoardsResponseBuilder()
					.withQueryResponseDef("{id name publiclyAvailable topics{id}}").build();
		}
		return boardsAndTopicsResponse;
	}

	@Override
	public List<Board> boardsSimple() throws GraphQLExecutionException {
		return queryType.boards(boardsSimpleResponse);
	}

	@Override
	public List<Board> boardsAndTopics() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		// Used to check that a newly created Board has no topic
		return queryType.boards(getBoardsAndTopics());
	}

	@Override
	public List<Topic> topicAuthorPostAuthor() throws GraphQLExecutionException {
		return queryType.topics(topicAuthorPostAuthorResponse, "Board name 2");
	}

	@Override
	public Board createBoard(String name, boolean publiclyAvailable)
			throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return mutationType.createBoard(createBoardResponse, name, publiclyAvailable);
	}
}
