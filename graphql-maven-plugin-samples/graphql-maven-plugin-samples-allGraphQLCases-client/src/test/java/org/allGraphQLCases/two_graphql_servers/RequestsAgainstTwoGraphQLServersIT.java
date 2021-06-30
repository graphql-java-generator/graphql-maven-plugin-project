/**
 * 
 */
package org.allGraphQLCases.two_graphql_servers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.allGraphQLCases.client.Character;
import org.allGraphQLCases.client.util.MyQueryTypeExecutor;
import org.forum.client.Board;
import org.forum.client.util.QueryTypeExecutor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * Generates IT tests with requests against the two started GraphQL servers: the allGraphQLCases and the Forum ones
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
public class RequestsAgainstTwoGraphQLServersIT {

	@Autowired
	static MyQueryTypeExecutor queryType;

	@Autowired(required = false)
	static QueryTypeExecutor queryType2;

	@BeforeAll
	static void setup() {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(TwoGraphQLServersSpringConfig.class);

		queryType = ctx.getBean(MyQueryTypeExecutor.class);
		assertNotNull(queryType);

		queryType2 = ctx.getBean(QueryTypeExecutor.class);
		assertNotNull(queryType2);
	}

	@Test
	void test_allGraphQLCasesServer() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		List<Character> list = queryType.withoutParameters("{appearsIn name }");

		assertNotNull(list);
		assertEquals(10, list.size());
		for (Character c : list) {
			checkCharacter(c, "withoutParameters", true, "Random String (", 0, 0);
		}
	}

	@Test
	void test_forumServer() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// return queryType.boards("{id name publiclyAvailable}");
		List<Board> boards = queryType2.boards("");

		// Verification
		assertTrue(boards.size() >= 10, "10 boards at startup, then new ones are created by the tests");

		Board board2 = boards.get(1); // Board names start by 1, not 0 as a lists
		assertEquals("2", board2.getId());
		assertEquals("Board name 2", board2.getName());
		assertEquals(false, board2.getPubliclyAvailable());
		assertEquals(null, board2.getTopics());

		Board board10 = boards.get(9); // Board names start by 1, not 0 as a lists
		assertEquals("10", board10.getId());
		assertEquals("Board name 10", board10.getName());
		assertEquals(true, board10.getPubliclyAvailable());
		assertEquals(null, board10.getTopics());

		fail("not yet implemented");
	}

	@Test
	void test_GraphQLRepository_allGraphQLCases() {
		fail("not yet implemented");
	}

	@Test
	void test_GraphQLRepository_forum() {
		fail("not yet implemented");
	}

	private void checkCharacter(Character c, String testDecription, boolean idShouldBeNull, String nameStartsWith,
			int nbFriends, int nbAppearsIn) {

		if (idShouldBeNull)
			assertNull(c.getId(), testDecription + " (id)");
		else
			assertNotNull(c.getId(), testDecription + " (id)");

		assertTrue(c.getName().startsWith(nameStartsWith),
				testDecription + " (name starts with " + nameStartsWith + ")");

		// nbFriends is the number of friends... before any call to addFriend
		if (nbFriends == 0) {
			// c.getFriends() may be null
			if (c.getFriends() != null) {
				assertTrue(c.getFriends().size() >= 0, testDecription + " (friends)");
			}
		} else {
			assertTrue(c.getFriends().size() >= nbFriends, testDecription + " (friends)");
			for (Character friend : c.getFriends()) {
				// Expected fields: id and name
				assertNotNull(friend.getId());
				assertNotNull(friend.getName());
				assertNull(friend.getAppearsIn());
			}
		}

		// nbEpisodes is the expected number of episodes
		if (nbAppearsIn == 0) {
			// c.getAppearsIn() may be null
			if (c.getAppearsIn() != null) {
				assertTrue(c.getAppearsIn().size() >= 0, testDecription + " (getAppearsIn)");
			}
		} else {
			assertTrue(c.getAppearsIn().size() >= nbAppearsIn, testDecription + " (getAppearsIn)");
		}

	}
}
