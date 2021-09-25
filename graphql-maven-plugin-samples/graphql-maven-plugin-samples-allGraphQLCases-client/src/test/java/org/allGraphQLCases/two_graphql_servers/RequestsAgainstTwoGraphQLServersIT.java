/**
 * 
 */
package org.allGraphQLCases.two_graphql_servers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.allGraphQLCases.SpringTestConfig;
import org.allGraphQLCases.client.Character;
import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.subscription.SubscriptionCallbackListIntegerForTest;
import org.forum.client.Board;
import org.forum.client.Member;
import org.forum.client.MemberType;
import org.forum.client.Post;
import org.forum.client.Topic;
import org.forum.client.util.QueryExecutorForum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * Generates IT tests with requests against the two started GraphQL servers: the allGraphQLCases and the Forum ones
 * 
 * @author etienne-sf
 */
// Adding "webEnvironment = SpringBootTest.WebEnvironment.NONE" avoid this error:
// "No qualifying bean of type 'ReactiveClientRegistrationRepository' available"
// More details here: https://stackoverflow.com/questions/62558552/error-when-using-enablewebfluxsecurity-in-springboot
@SpringBootTest(classes = SpringTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Execution(ExecutionMode.CONCURRENT)
public class RequestsAgainstTwoGraphQLServersIT {

	@Autowired
	MyQueryTypeExecutorAllGraphQLCases queryTypeAllGraphQLCases;
	@Autowired
	GraphQLRepoAllGraphQLCases graphQLRepoAllGraphQLCases;

	@Autowired
	QueryExecutorForum queryTypeForum;
	@Autowired
	GraphQLRepoForum graphQLRepoForum;

	@BeforeEach
	void setup() {
		assertNotNull(queryTypeAllGraphQLCases);
		assertNotNull(queryTypeForum);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_allGraphQLCasesServer() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		List<Character> list = queryTypeAllGraphQLCases.withoutParameters("{appearsIn name }");

		assertNotNull(list);
		assertEquals(10, list.size());
		for (Character c : list) {
			checkCharacter(c, "withoutParameters", true, "Random String (", 0, 0);
		}
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_forumServer() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// return queryType.boards("{id name publiclyAvailable}");
		List<Board> boards = queryTypeForum.boards("");

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
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_GraphQLRepository_allGraphQLCases() throws GraphQLRequestExecutionException {
		// Preparation
		SubscriptionCallbackListIntegerForTest callback = new SubscriptionCallbackListIntegerForTest(
				"FullRequestSubscriptionIT.test_SubscribeToAList");

		// Go, go, go
		SubscriptionClient sub = graphQLRepoAllGraphQLCases.subscribeToAList(callback);

		// Verification
		try {
			Thread.sleep(500); // Wait 0.5 second, so that other thread is ready
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		// Let's wait a max of 10 second, until we receive some notifications
		try {
			for (int i = 1; i < 100; i += 1) {
				if (callback.lastReceivedMessage != null)
					break;
				Thread.sleep(100); // Wait 0.1 second
			} // for
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		// Let's disconnect from the subscription
		sub.unsubscribe();

		// We should have received a notification from the subscription
		assertNotNull(callback.lastReceivedMessage);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_GraphQLRepository_forum() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2009, 11, 20);// Month is 0-based, so this date is 2009, December the 20th
		List<Topic> topics = graphQLRepoForum.topicAuthorPostAuthor("Board name 2", cal.getTime());

		assertTrue(topics.size() >= 5);

		Topic topic12 = topics.get(1);
		//
		assertEquals("The content of the topic <12>", topic12.getContent());
		assertEquals(new GregorianCalendar(2018, 12 - 1, 20).getTime(), topic12.getDate());
		assertEquals(12, (int) topic12.getNbPosts());
		assertEquals("The title of <12>", topic12.getTitle());
		assertEquals("12", topic12.getId());
		//
		Member author12 = topic12.getAuthor();// All its fields have been loaded
		assertNotNull(author12);
		assertEquals("12", author12.getId());
		assertEquals("[BL] Name 12", author12.getName());
		assertEquals("Alias of Name 12", author12.getAlias());
		assertEquals("name.12@graphql-java.com", author12.getEmail());
		assertEquals(MemberType.STANDARD, author12.getType());

		// Let's check for one post
		List<Post> posts12 = topic12.getPosts();
		assertEquals(8, posts12.size());
		//
		Post post232 = posts12.get(5);
		assertEquals(new GregorianCalendar(2018, 05 - 1, 13).getTime(), post232.getDate());
		assertEquals("232", post232.getId());
		assertEquals("The content of the post <232>", post232.getContent());
		assertEquals(null, post232.getPubliclyAvailable()); // Not queried
		assertEquals("The title of <232>", post232.getTitle());
		//
		Member author12bis = post232.getAuthor();// This one is partially loaded: author{name email alias}
		assertNotNull(author12bis);
		assertEquals(null, author12bis.getId());
		assertEquals("[BL] Name 12", author12bis.getName());
		assertEquals("Alias of Name 12", author12bis.getAlias());
		assertEquals("name.12@graphql-java.com", author12bis.getEmail());
		assertEquals(null, author12bis.getType());
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
