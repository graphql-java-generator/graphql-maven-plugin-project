/**
 * 
 */
package com.graphql_java_generator.samples.forum.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.GregorianCalendar;
import java.util.List;

import org.forum.generated.Board;
import org.forum.generated.Query;
import org.forum.generated.util.GraphQLRequest;
import org.forum.generated.util.QueryExecutor;
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

/**
 * This class is both samples and integration tests for Full GraphQL request, that contains GraphQL fragments.
 * 
 * @author etienne-sf
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SpringTestConfig.class })
@TestPropertySource("classpath:application.properties")
@Execution(ExecutionMode.CONCURRENT)
public class FullRequestWithFragmentIT {

	@Autowired
	QueryExecutor queryExecutor;

	GraphQLRequest boardsRequestWithGlobalFragments;
	GraphQLRequest boardsRequestWithInlineFragments;

	@BeforeEach
	void setupAll() throws GraphQLRequestPreparationException {

		// Let's build once the request, and use it for each further execution
		boardsRequestWithGlobalFragments = queryExecutor.getGraphQLRequest(""//
				+ "fragment member on Member {name alias} "
				+ "fragment post on Post {date content author{...member   id}}\n"
				+ "fragment topic on Topic {title posts(since: &sinceParam){id ...post} author{...member}}\r"
				+ "query{boards{id name topics {id ...topic}}}");

		// The same request, with inline fragments
		boardsRequestWithInlineFragments = queryExecutor.getGraphQLRequest(""//
				+ "query{boards{" //
				+ "  id name topics {"//
				+ "     id ... on Topic {" //
				+ "         title " //
				+ "         posts(since: &sinceParam){id ... on Post {date content author{... on Member {name alias}   id}} } "//
				+ "         author{... on Member {name alias}}"//
				+ "     } "//
				+ "  }"//
				+ "}}");
	}

	@Test
	void test_GlobalFragments_NoRecentPost() throws GraphQLRequestExecutionException {
		// There is no post since 2020, march the 3rd
		Query response = boardsRequestWithGlobalFragments.execQuery("sinceParam",
				new GregorianCalendar(2020, 3 - 1, 31).getTime());
		List<Board> boards = response.getBoards();

		// Verification
		assertTrue(boards.size() >= 10);
		// no posts, as this date is too early
		assertEquals(0, boards.get(0).getTopics().get(0).getPosts().size());
	}

	@Test
	void test_GlobalFragments_WithOldPosts() throws GraphQLRequestExecutionException {
		// There is no post since 2020, march the 3rd
		Query response = boardsRequestWithGlobalFragments.execQuery("sinceParam",
				new GregorianCalendar(2000, 3 - 1, 31).getTime());
		List<Board> boards = response.getBoards();

		// Verification
		assertTrue(boards.size() >= 10);
		// no posts, as this date is too early
		assertTrue(boards.get(0).getTopics().get(0).getPosts().size() > 0);
	}

	@Test
	void test_InlineFragments_NoRecentPost() throws GraphQLRequestExecutionException {
		// There is no post since 2020, march the 3rd
		Query response = boardsRequestWithInlineFragments.execQuery("sinceParam",
				new GregorianCalendar(2020, 3 - 1, 31).getTime());
		List<Board> boards = response.getBoards();

		// Verification
		assertTrue(boards.size() >= 10);
		// no posts, as this date is too early
		assertEquals(0, boards.get(0).getTopics().get(0).getPosts().size());
	}

	@Test
	void test_InlineFragments_WithOldPosts() throws GraphQLRequestExecutionException {
		// There is no post since 2020, march the 3rd
		Query response = boardsRequestWithInlineFragments.execQuery("sinceParam",
				new GregorianCalendar(2000, 3 - 1, 31).getTime());
		List<Board> boards = response.getBoards();

		// Verification
		assertTrue(boards.size() >= 10);
		// no posts, as this date is too early
		assertTrue(boards.get(0).getTopics().get(0).getPosts().size() > 0);
	}

}
