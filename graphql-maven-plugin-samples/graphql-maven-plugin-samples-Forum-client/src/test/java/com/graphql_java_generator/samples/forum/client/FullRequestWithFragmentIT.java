/**
 * 
 */
package com.graphql_java_generator.samples.forum.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.graphql_java_generator.client.GraphQLConfiguration;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Board;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.GraphQLRequest;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.QueryTypeResponse;

/**
 * This class is both samples and integration tests for Full GraphQL request, that contains GraphQL fragments.
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
public class FullRequestWithFragmentIT {

	static GraphQLRequest boardsRequestWithGlobalFragments;
	static GraphQLRequest boardsRequestWithInlineFragments;

	@BeforeAll
	static void setupAll() throws GraphQLRequestPreparationException {
		// We have one GraphQL endpoint. So we use the static configuration.
		GraphQLRequest.setStaticConfiguration(new GraphQLConfiguration(Main.GRAPHQL_ENDPOINT_URL));

		// Let's build once the request, and use it for each further execution
		boardsRequestWithGlobalFragments = new GraphQLRequest(""//
				+ "fragment member on Member {name alias} "
				+ "fragment post on Post {date content author{...member   id}}\n"
				+ "fragment topic on Topic {title posts(since: &sinceParam){id ...post} author{...member}}\r"
				+ "query{boards{id name topics {id ...topic}}}");

		// The same request, with inline fragments
		boardsRequestWithInlineFragments = new GraphQLRequest(""//
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
		QueryTypeResponse response = boardsRequestWithGlobalFragments.execQuery("sinceParam",
				new GregorianCalendar(2020, 3 - 1, 31).getTime());
		List<Board> boards = response.getBoards();

		// Verification
		assertEquals(10, boards.size());
		// no posts, as this date is too early
		assertEquals(0, boards.get(0).getTopics().get(0).getPosts().size());
	}

	@Test
	void test_GlobalFragments_WithOldPosts() throws GraphQLRequestExecutionException {
		// There is no post since 2020, march the 3rd
		QueryTypeResponse response = boardsRequestWithGlobalFragments.execQuery("sinceParam",
				new GregorianCalendar(2000, 3 - 1, 31).getTime());
		List<Board> boards = response.getBoards();

		// Verification
		assertEquals(10, boards.size());
		// no posts, as this date is too early
		assertTrue(boards.get(0).getTopics().get(0).getPosts().size() > 0);
	}

	@Test
	void test_InlineFragments_NoRecentPost() throws GraphQLRequestExecutionException {
		// There is no post since 2020, march the 3rd
		QueryTypeResponse response = boardsRequestWithInlineFragments.execQuery("sinceParam",
				new GregorianCalendar(2020, 3 - 1, 31).getTime());
		List<Board> boards = response.getBoards();

		// Verification
		assertEquals(10, boards.size());
		// no posts, as this date is too early
		assertEquals(0, boards.get(0).getTopics().get(0).getPosts().size());
	}

	@Test
	void test_InlineFragments_WithOldPosts() throws GraphQLRequestExecutionException {
		// There is no post since 2020, march the 3rd
		QueryTypeResponse response = boardsRequestWithInlineFragments.execQuery("sinceParam",
				new GregorianCalendar(2000, 3 - 1, 31).getTime());
		List<Board> boards = response.getBoards();

		// Verification
		assertEquals(10, boards.size());
		// no posts, as this date is too early
		assertTrue(boards.get(0).getTopics().get(0).getPosts().size() > 0);
	}

}
