package com.graphql_java_generator.samples.forum.client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
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
	final ObjectResponse topicAuthorPostAuthorResponse;

	final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public PreparedQueriesWithFieldInputParameters() throws GraphQLRequestPreparationException {
		queryType = new QueryType(Main.GRAPHQL_ENDPOINT_URL);
		topicAuthorPostAuthorResponse = queryType.getTopicsResponseBuilder().withQueryResponseDef(
				"{id date author{name email alias id type} nbPosts title content posts(memberId:?memberId, memberName:?memberName, since:?since){id date author{name email alias} title content}}")
				.build();
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
