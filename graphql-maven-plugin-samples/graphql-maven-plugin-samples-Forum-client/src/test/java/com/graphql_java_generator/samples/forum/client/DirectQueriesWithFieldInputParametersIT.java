package com.graphql_java_generator.samples.forum.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Topic;

/**
 * Some samples (and tests) with direct queries having input parameters
 * 
 * @author etienne-sf
 *
 */
@Execution(ExecutionMode.CONCURRENT)
public class DirectQueriesWithFieldInputParametersIT {

	static DirectQueriesWithFieldInputParameters directQueriesWithFieldInputParameters;

	String boardName;
	Date since;

	@BeforeAll
	static void beforeAll() throws GraphQLRequestPreparationException {
		directQueriesWithFieldInputParameters = new DirectQueriesWithFieldInputParameters();
	}

	@BeforeEach
	void beforeEach() {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, 9, 20);// Month is 0-based, so this date is 2018, october the 20th
		since = cal.getTime();

		boardName = "Board name 2";
	}

	@Test
	public void topics_since() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		// Go, go, go
		List<Topic> topics = directQueriesWithFieldInputParameters.topics_since(boardName, since);

		// Verifications
		assertTrue(topics.size() >= 5);
		assertEquals(2, topics.get(0).getPosts().size());
	}

	@Test
	public void topics_memberId_since() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		// Preparation
		UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000012");

		// Go, go, go
		List<Topic> topics = directQueriesWithFieldInputParameters.topics_memberId_since(boardName, uuid, since);

		// Verifications
		assertTrue(topics.size() >= 5);
		assertEquals(0, topics.get(0).getPosts().size());
	}

}
