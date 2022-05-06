package com.graphql_java_generator.samples.forum.client;

import java.util.Date;
import java.util.List;

import org.forum.generated.Topic;
import org.forum.generated.util.GraphQLRequest;
import org.forum.generated.util.QueryExecutor;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * Check that the server correctly works with the combination for the arguments for the post field: as there are
 * optional argument, multiple queries must be implemented. In order to the sample to be properly coded, all must be
 * tested.
 * 
 * @author etienne-sf
 *
 */
public class PreparedQueriesWithFieldInputParameters {

	public static String GRAPHQL_ENDPOINT_URL = "http://localhost:8183/graphql";

	final QueryExecutor queryType;
	final GraphQLRequest topicAuthorPostAuthorResponse;

	public PreparedQueriesWithFieldInputParameters() throws GraphQLRequestPreparationException {
		queryType = new QueryExecutor(GRAPHQL_ENDPOINT_URL);
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
	List<Topic> boardsWithPostSince(String boardName, String memberId, String memberName, Date since)
			throws GraphQLRequestExecutionException {
		return queryType.topics(topicAuthorPostAuthorResponse, boardName, "memberId", memberId, "memberName",
				memberName, "since", since);
	}

}
