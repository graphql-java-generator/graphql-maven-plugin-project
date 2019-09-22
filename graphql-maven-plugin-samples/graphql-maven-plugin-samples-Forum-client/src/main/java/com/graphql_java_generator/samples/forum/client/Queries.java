package com.graphql_java_generator.samples.forum.client;

import java.util.List;

import com.graphql_java_generator.client.response.GraphQLExecutionException;
import com.graphql_java_generator.client.response.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Board;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Topic;

public interface Queries {

	List<Board> boardsSimple() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	List<Board> boardsAndTopics() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	List<Topic> topicAuthorPostAuthor() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Board createBoard(String name, boolean publiclyAvailable)
			throws GraphQLExecutionException, GraphQLRequestPreparationException;

}