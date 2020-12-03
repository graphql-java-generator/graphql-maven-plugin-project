package com.graphql_java_generator.samples.forum.client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.QueryType;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Topic;

/**
 * Some samples (and tests) with direct queries having input parameters
 * 
 * @author etienne-sf
 *
 */
public class DirectQueriesWithFieldInputParameters {

	public static String GRAPHQL_ENDPOINT_URL = "http://localhost:8180/graphql";

	QueryType queryType;

	final String DATE_FORMAT = "yyyy-MM-dd";
	final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

	public DirectQueriesWithFieldInputParameters() throws GraphQLRequestPreparationException {
		queryType = new QueryType(GRAPHQL_ENDPOINT_URL);
	}

	public List<Topic> topics_since(String boardName, Date since)
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		// We need to format each parameter, in each method that uses it.
		String formatedDate = dateFormat.format(since);

		return queryType.topics("{id date author{name email alias id type} nbPosts title content posts(since:\""
				+ formatedDate + "\"){id date author{name email alias} title content}}", boardName);
	}

	public List<Topic> topics_memberId_since(String boardName, UUID memberId, Date since)
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		// We need to format each parameter, in each method that uses it.
		String formatedDate = dateFormat.format(since);

		//
		return queryType.topics("{id date author{name email alias id type} nbPosts title content posts(since:\""
				+ formatedDate + "\", memberName:\"" + memberId.toString()
				+ "\"){id date author{name email alias} title content}}", boardName);
	}
}
