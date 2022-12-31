package com.graphql_java_generator.samples.forum.client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.QueryExecutor;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Topic;

/**
 * Some samples (and tests) with direct queries having input parameters
 * 
 * @author etienne-sf
 *
 */
@Component
public class DirectQueriesWithFieldInputParameters {

	@Autowired
	QueryExecutor query;

	final String DATE_FORMAT = "yyyy-MM-dd";
	final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

	public List<Topic> topics_since(String boardName, Date since)
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		// We need to format each parameter, in each method that uses it.
		String formatedDate = dateFormat.format(since);

		return query.topics("{id date author{name email alias id type} nbPosts title content posts(since:\""
				+ formatedDate + "\"){id date author{name email alias} title content}}", boardName);
	}

	public List<Topic> topics_memberId_since(String boardName, UUID memberId, Date since)
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		// We need to format each parameter, in each method that uses it.
		String formatedDate = dateFormat.format(since);

		//
		return query.topics("{id date author{name email alias id type} nbPosts title content posts(since:\""
				+ formatedDate + "\", memberName:\"" + memberId.toString()
				+ "\"){id date author{name email alias} title content}}", boardName);
	}
}
