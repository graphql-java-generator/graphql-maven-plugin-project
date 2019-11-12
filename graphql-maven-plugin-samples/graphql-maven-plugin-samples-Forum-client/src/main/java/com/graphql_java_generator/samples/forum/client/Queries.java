package com.graphql_java_generator.samples.forum.client;

import java.util.List;

import com.graphql_java_generator.client.response.GraphQLExecutionException;
import com.graphql_java_generator.client.response.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Board;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Topic;

/**
 * This interface contains the queries that will be defined in this sample. The queries are defined in the package
 * com.graphql_java_generator.samples.forum.client.graphql, by three classes. These three classes show the three ways to
 * build and execute GraphQL Request with graphql-java-generator
 * 
 * @author EtienneSF
 *
 */
public interface Queries {

	List<Board> boardsSimple() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	List<Board> boardsAndTopics() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	List<Topic> topicAuthorPostAuthor() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	List<Topic> findTopics(String boardName, List<String> keyword)
			throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Board createBoard(String name, boolean publiclyAvailable)
			throws GraphQLExecutionException, GraphQLRequestPreparationException;

}