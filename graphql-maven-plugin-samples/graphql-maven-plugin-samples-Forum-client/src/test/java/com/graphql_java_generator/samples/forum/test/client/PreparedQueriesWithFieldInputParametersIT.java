package com.graphql_java_generator.samples.forum.test.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import com.graphql_java_generator.samples.forum.client.PreparedQueriesWithFieldInputParameters;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Topic;
import com.graphql_java_generator.samples.forum.test.SpringTestConfig;

/**
 * Check that the server correctly works with the combination for the arguments for the post field: as there are
 * optional argument, multiple queries must be implemented. In order to the sample to be properly coded, all must be
 * tested.
 * 
 * @author etienne-sf
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SpringTestConfig.class })
@TestPropertySource("classpath:application.properties")
@Execution(ExecutionMode.CONCURRENT)
public class PreparedQueriesWithFieldInputParametersIT {

	@Autowired
	PreparedQueriesWithFieldInputParameters preparedQueriesWithFieldInputParameters;

	String boardName;
	Date since;

	@BeforeEach
	void beforeEach() {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, 9, 20);// Month is 0-based, so this date is 2018, October the 20th
		since = cal.getTime();

		boardName = "Board name 2";
	}

	/**
	 * Only the since parameter
	 * 
	 * @throws GraphQLRequestExecutionException
	 */
	@Test
	void test_boardsWithPostSince() throws GraphQLRequestExecutionException {
		// Go, go, go
		List<Topic> topics = preparedQueriesWithFieldInputParameters.boardsWithPostSince(boardName, null, null, since);

		// Verification
		assertTrue(topics.size() >= 5);
		assertEquals(2, topics.get(0).getPosts().size(), "First topic has two posts since 2018-10-20");
		assertEquals(2, topics.get(1).getPosts().size(), "Second topic has two posts since 2018-10-20");
	}

	/**
	 * With the memberId and since parameters
	 * 
	 * @throws GraphQLRequestExecutionException
	 */
	@Test
	void test_memberId_since() throws GraphQLRequestExecutionException {

		// Go, go, go
		List<Topic> topics = preparedQueriesWithFieldInputParameters.boardsWithPostSince(boardName, "2", null, since);

		// Verification
		assertTrue(topics.size() >= 5);
		assertEquals(2, topics.get(0).getPosts().size(), "First topic has two posts since 2018-10-20 for member '2'");
		assertEquals(0, topics.get(1).getPosts().size(), "Second topic has no posts since 2018-10-20 for member '2'");
	}

	/**
	 * With the memberName and since parameters
	 * 
	 * @throws GraphQLRequestExecutionException
	 */
	@Test
	void test_memberName_since() throws GraphQLRequestExecutionException {

		// Go, go, go
		List<Topic> topics = preparedQueriesWithFieldInputParameters.boardsWithPostSince(boardName, null, "Name 12",
				since);

		// Verification
		assertTrue(topics.size() >= 5);
		assertEquals(0, topics.get(0).getPosts().size(),
				"First topic has no posts since 2018-10-20 for member 'Name 12'");
		assertEquals(2, topics.get(1).getPosts().size(),
				"Second topic has two posts since 2018-10-20 for member 'Name 12'");
	}

	/**
	 * With the memberId, memberName and since
	 * 
	 * @throws GraphQLRequestExecutionException
	 */
	@Test
	void test_memberId_memberName_since_OK() throws GraphQLRequestExecutionException {

		// Go, go, go
		List<Topic> topics = preparedQueriesWithFieldInputParameters.boardsWithPostSince(boardName, "2", "Name 2",
				since);

		// Verification
		assertTrue(topics.size() >= 5);
		assertEquals(2, topics.get(0).getPosts().size(), "First topic has two posts since 2018-10-20 for member '2'");
		assertEquals(0, topics.get(1).getPosts().size(), "Second topic has no posts since 2018-10-20 for member '2'");
	}

	/**
	 * With the memberId, memberName and since
	 * 
	 * @throws GraphQLRequestExecutionException
	 */
	@Test
	void test_memberId_memberName_since_KO() throws GraphQLRequestExecutionException {

		List<Topic> topics = preparedQueriesWithFieldInputParameters.boardsWithPostSince(boardName, "2", "Bad Name",
				since);

		// Verification
		assertTrue(topics.size() >= 5);
		assertEquals(0, topics.get(0).getPosts().size(),
				"First topic has two posts since 2018-10-20 for member '00000000-0000-0000-0000-00000000002'");
		assertEquals(0, topics.get(1).getPosts().size(),
				"Second topic has no posts since 2018-10-20 for member '00000000-0000-0000-0000-00000000002'");
	}
}
