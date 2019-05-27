package org.graphql.maven.plugin.samples.forum.client;

import java.util.List;

import org.graphql.maven.plugin.samples.forum.client.graphql.forum.client.Board;
import org.graphql.maven.plugin.samples.forum.client.graphql.forum.client.Topic;

import graphql.java.client.response.GraphQLExecutionException;
import graphql.java.client.response.GraphQLRequestPreparationException;

public interface Queries {

	List<Board> boardsSimple() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	List<Board> boardsAndTopics() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	List<Topic> topicAuthorPostAuthor() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Board createBoard(String name, boolean publiclyAvailable)
			throws GraphQLExecutionException, GraphQLRequestPreparationException;

}