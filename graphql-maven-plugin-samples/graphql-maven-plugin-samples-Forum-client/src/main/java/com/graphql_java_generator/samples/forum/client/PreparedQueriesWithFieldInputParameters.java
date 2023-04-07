package com.graphql_java_generator.samples.forum.client;

import java.util.Date;
import java.util.List;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.GraphQLRequest;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.QueryExecutor;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Topic;

/**
 * Check that the server correctly works with the combination for the arguments for the post field: as there are
 * optional argument, multiple queries must be implemented. In order to the sample to be properly coded, all must be
 * tested.
 * 
 * @author etienne-sf
 *
 */
@Component
public class PreparedQueriesWithFieldInputParameters {

	@Autowired
	QueryExecutor queryType;
	GraphQLRequest topicAuthorPostAuthorResponse;

	@PostConstruct
	public void postConstruct() throws GraphQLRequestPreparationException {
		topicAuthorPostAuthorResponse = queryType.getTopicsGraphQLRequest(
				"{id date author{name email alias id type} nbPosts title content posts(memberId:?memberId, memberName:?memberName, since:?since){id date author{name email alias} title content}}");
	}

	/**
	 * Execute the query, with the given parameters. Optional parameters may be null.
	 * 
	 * @return
	 * 
	 * @throws GraphQLRequestExecutionException
	 */
	public List<Topic> boardsWithPostSince(String boardName, String memberId, String memberName, Date since)
			throws GraphQLRequestExecutionException {
		return queryType.topics(topicAuthorPostAuthorResponse, boardName, "memberId", memberId, "memberName",
				memberName, "since", since);
	}

}
