package com.graphql_java_generator.samples.forum.client.graphql;

import java.util.List;

import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.client.response.GraphQLExecutionException;
import com.graphql_java_generator.client.response.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Board;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.MutationType;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.QueryType;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Topic;
import com.graphql_java_generator.samples.forum.client.Main;
import com.graphql_java_generator.samples.forum.client.Queries;

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

	QueryType queryType = new QueryType(Main.GRAPHQL_ENDPOINT_URL);
	MutationType mutationType = new MutationType(Main.GRAPHQL_ENDPOINT_URL);

	// Below are the ObjectResponses, that are created at initialization time.
	ObjectResponse boardsSimpleResponse;
	ObjectResponse boardsAndTopicsResponse;
	ObjectResponse topicAuthorPostAuthorResponse;
	ObjectResponse createBoardResponse;

	public WithQueries() throws GraphQLRequestPreparationException {
		// No field specified: all known scalar fields of the root type will be queried
		boardsSimpleResponse = queryType.getBoardsResponseBuilder().build();

		boardsAndTopicsResponse = queryType.getBoardsResponseBuilder()
				.withQueryResponseDef("{id name publiclyAvailable topics{id}}").build();

		topicAuthorPostAuthorResponse = queryType.getTopicsResponseBuilder().withQueryResponseDef(
				"{id date author{name email alias id type} nbPosts title content posts{id date author{name email alias} title content}}")
				.build();

		createBoardResponse = mutationType.getCreateBoardResponseBuilder().build();

	}

	@Override
	public List<Board> boardsSimple() throws GraphQLExecutionException {
		return queryType.boards(boardsSimpleResponse);
	}

	@Override
	public List<Board> boardsAndTopics() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		// Used to check that a newly created Board has no topic
		return queryType.boards(boardsAndTopicsResponse);
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
