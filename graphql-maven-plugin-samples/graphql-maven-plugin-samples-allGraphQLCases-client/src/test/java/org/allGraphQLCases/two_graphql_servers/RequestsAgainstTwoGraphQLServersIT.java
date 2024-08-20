/**
 * 
 */
package org.allGraphQLCases.two_graphql_servers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.allGraphQLCases.SpringTestConfig;
import org.allGraphQLCases.client.CIP_Character_CIS;
import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.subscription.SubscriptionCallbackGeneric;
import org.forum.client.Board;
import org.forum.client.Member;
import org.forum.client.MemberType;
import org.forum.client.Post;
import org.forum.client.QueryExecutorForum;
import org.forum.client.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(RequestsAgainstTwoGraphQLServersIT.class);

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
		assertNotNull(this.queryTypeAllGraphQLCases);
		assertNotNull(this.queryTypeForum);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_allGraphQLCasesServer() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		logger.debug("Starting test_allGraphQLCasesServer");

		List<CIP_Character_CIS> list = this.queryTypeAllGraphQLCases.withoutParameters("{appearsIn name }");

		assertNotNull(list);
		assertEquals(10, list.size());
		for (CIP_Character_CIS c : list) {
			checkCharacter(c, "withoutParameters", true, "Random String (", 0, 0);
		}
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_forumServer() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		logger.debug("Starting test_forumServer");

		// return query.boards("{id name publiclyAvailable}");
		List<Board> boards = this.queryTypeForum.boards("");

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
	void test_GraphQLRepository_allGraphQLCases() throws GraphQLRequestExecutionException, InterruptedException {
		logger.debug("Starting test_GraphQLRepository_allGraphQLCases");

		// Preparation
		SubscriptionCallbackGeneric<List<Integer>> callback = new SubscriptionCallbackGeneric<>(
				"RequestsAgainstTwoGraphQLServersIT.test_GraphQLRepository_allGraphQLCases");

		// Go, go, go
		SubscriptionClient sub = this.graphQLRepoAllGraphQLCases.subscribeToAList(callback);

		// Verification
		// Let's wait a max of 80 second, until we receive some notifications (this allows some debugging check in the
		// server)
		callback.latchForMessageReception.await(80, TimeUnit.SECONDS);

		// Let's disconnect from the subscription
		sub.unsubscribe();

		// We should have received a notification from the subscription
		assertNotNull(callback.lastReceivedMessage);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_GraphQLRepository_forum() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		logger.debug("Starting test_GraphQLRepository_forum");

		Calendar cal = new Calendar.Builder().set(0, 0).build();
		cal.clear();
		cal.set(2009, 12 - 1, 20);// Month is 0-based, so this date is 2009, December the 20th
		List<Topic> topics = this.graphQLRepoForum.topicAuthorPostAuthor("Board name 2", cal.getTime());

		// The returned list must contain these topics:

		Map<String, Topic> verif = new HashMap<>();
		verif.put("2", Topic.builder().withId("2")
				.withDate(new Calendar.Builder().setDate(2018, 12 - 1, 29).setTimeOfDay(13, 57, 10).build().getTime())
				.withTitle("The title of <2>").withContent("The content of the topic <2>").withPubliclyAvailable(true)
				.build());
		verif.put("12", Topic.builder().withId("12")
				.withDate(new Calendar.Builder().setDate(2018, 12 - 1, 29).setTimeOfDay(13, 57, 10).build().getTime())
				.withTitle("The title of <12>").withContent("The content of the topic <12>").withPubliclyAvailable(true)
				.build());
		verif.put("22", Topic.builder().withId("22")
				.withDate(new Calendar.Builder().setDate(2018, 12 - 1, 29).setTimeOfDay(13, 57, 10).build().getTime())
				.withTitle("The title of <22>").withContent("The content of the topic <22>").withPubliclyAvailable(true)
				.build());
		verif.put("32", Topic.builder().withId("32")
				.withDate(new Calendar.Builder().setDate(2018, 12 - 1, 29).setTimeOfDay(13, 57, 10).build().getTime())
				.withTitle("The title of <32>").withContent("The content of the topic <32>").withPubliclyAvailable(true)
				.build());
		verif.put("42", Topic.builder().withId("42")
				.withDate(new Calendar.Builder().setDate(2018, 12 - 1, 29).setTimeOfDay(13, 57, 10).build().getTime())
				.withTitle("The title of <42>").withContent("The content of the topic <42>").withPubliclyAvailable(true)
				.build());

		//
		Topic topic12 = null;
		assertTrue(topics.size() >= verif.size());
		for (Topic t : topics) {
			// The returned topics may contain unexpected items, as some Integration Tests create data in the server.
			// We managed here only the expected items
			if (verif.containsKey(t.getId())) {
				// We store the topic 12 for further tests
				if (t.getId().equals("12")) {
					topic12 = t;
				}
				// We remove this item from the map: if the map becomes empty, we'll have found every expected items
				verif.remove(t.getId());
			}
		}
		assertEquals(0, verif.size(), "The verif map should be empty (all of its items must have been found");

		//
		assertEquals("12", topic12.getId());
		assertEquals("The content of the topic <12>", topic12.getContent());
		assertEquals(new GregorianCalendar(2018, 12 - 1, 20).getTime(), topic12.getDate());
		assertEquals(12, (int) topic12.getNbPosts()); // This number is a field unrelated to the real number of posts
		assertEquals("The title of <12>", topic12.getTitle());
		//
		Member author12 = topic12.getAuthor();// All its fields have been loaded
		assertNotNull(author12);
		assertEquals("12", author12.getId());
		assertEquals("[BM] Name 12", author12.getName(),
				"name should be prefixed by [BM] as this is loaded by the @BatchMapper data fetcher delegate");
		assertEquals("Alias of Name 12", author12.getAlias());
		assertEquals("name.12@graphql-java.com", author12.getEmail());
		assertEquals(MemberType.STANDARD, author12.getType());

		// Let's check for one post
		List<Post> posts12 = topic12.getPosts();
		Post post232 = null;
		//
		assertEquals(8, posts12.size());
		List<String> ids = Arrays.asList("12", "56", "100", "144", "188", "232", "276", "320");
		for (Post p : posts12) {
			if (p.getId().equals("232")) {
				post232 = p;
			}
			if (!ids.contains(p.getId())) {
				fail("posts12 contains an expected post (id=" + p.getId() + ")");
			}
		}
		//

		assertNotNull(post232, "We should have found the post of id 232");
		assertEquals("232", post232.getId());
		assertEquals(new GregorianCalendar(2018, 05 - 1, 13).getTime(), post232.getDate());
		assertEquals("The content of the post <232>", post232.getContent());
		assertEquals(null, post232.getPubliclyAvailable()); // Not queried
		assertEquals("The title of <232>", post232.getTitle());
		//
		Member author12bis = post232.getAuthor();// This one is partially loaded: author{name email alias}
		assertNotNull(author12bis);
		assertEquals(null, author12bis.getId());
		assertEquals("[BM] Name 12", author12bis.getName());
		assertEquals("Alias of Name 12", author12bis.getAlias());
		assertEquals("name.12@graphql-java.com", author12bis.getEmail());
		assertEquals(null, author12bis.getType());
	}

	private void checkCharacter(CIP_Character_CIS c, String testDecription, boolean idShouldBeNull,
			String nameStartsWith, int nbFriends, int nbAppearsIn) {

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
			for (CIP_Character_CIS friend : c.getFriends()) {
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
