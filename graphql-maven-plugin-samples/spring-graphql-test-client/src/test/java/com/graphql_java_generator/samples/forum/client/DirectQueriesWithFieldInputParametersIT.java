package com.graphql_java_generator.samples.forum.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.forum.generated.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.SpringTestConfig;
import com.graphql_java_generator.samples.forum.client.graphql.GraphQLRepositoryPartialRequests;

/**
 * Some samples (and tests) with direct queries having input parameters
 * 
 * @author etienne-sf
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SpringTestConfig.class })
@TestPropertySource("classpath:application.properties")
@Execution(ExecutionMode.CONCURRENT)
public class DirectQueriesWithFieldInputParametersIT {

	@Autowired
	GraphQLRepositoryPartialRequests graphQLRepositoryPartialRequests;

	String boardName;
	Date since;

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
		List<Topic> topics = graphQLRepositoryPartialRequests.topicAuthorPostAuthor(boardName, since);

		// Verifications
		assertTrue(topics.size() >= 5);
		assertEquals(2, topics.get(0).getPosts().size());
	}

	@Test
	public void topics_memberId_since() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		// Go, go, go
		List<Topic> topics = graphQLRepositoryPartialRequests.topics_memberId_since(boardName, (long) 12, since);

		// Verifications
		assertTrue(topics.size() >= 5);
		assertEquals(0, topics.get(0).getPosts().size());
	}

}
