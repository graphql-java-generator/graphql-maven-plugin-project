package com.graphql_java_generator.samples.forum.client;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.GraphQLRequest;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.QueryType;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Topic;

/**
 * Check that the server correctly works with the combination for the arguments for the post field: as there are
 * optional argument, multiple queries must be implemented. In order to the sample to be properly coded, all must be
 * tested.
 * 
 * @author etienne-sf
 *
 */
public class PreparedQueriesWithFieldInputParameters {

	final QueryType queryType;
	final GraphQLRequest topicAuthorPostAuthorResponse;

	public PreparedQueriesWithFieldInputParameters() throws GraphQLRequestPreparationException {
		queryType = new QueryType(Main.GRAPHQL_ENDPOINT_URL);
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
	List<Topic> boardsWithPostSince(String boardName, UUID memberId, String memberName, Date since)
			throws GraphQLRequestExecutionException {
		return queryType.topics(topicAuthorPostAuthorResponse, boardName, "memberId", memberId, "memberName",
				memberName, "since", since);
	}

}
