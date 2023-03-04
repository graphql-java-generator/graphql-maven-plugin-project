package com.graphql_java_generator.samples.forum.client.graphql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.Queries;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Board;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Member;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.MemberInput;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.MemberType;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Post;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.PostInput;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Topic;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.TopicInput;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.TopicPostInput;

/**
 * As it is suffixed by "IT", this is an integration test. Thus, it allows us to start the GraphQL Forum server, see the
 * pom.xml file for details.
 * 
 * @author etienne-sf
 */
abstract class AbstractIT {

	Queries queries;

	@Test
	void test_boardsSimple() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// return queryType.boards("{id name publiclyAvailable}");
		List<Board> boards = queries.boardsSimple();

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
	void test_boardsAndTopicsWithFieldParameter()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, 11, 20);// Month is 0-based, so this date is 2018, December the 20th

		List<Board> boards = queries.boardsAndTopicsWithFieldParameter(cal.getTime());

		// Verification
		assertTrue(boards.size() >= 10, "10 boards at startup, then new ones are created by the tests");

		Board board2 = boards.get(1); // Board names start by 1, not 0 as a lists
		assertEquals("2", board2.getId());
		assertEquals("Board name 2", board2.getName());
		assertEquals(false, board2.getPubliclyAvailable());
		assertEquals(2, board2.getTopics().size());

		Board board10 = boards.get(9); // Board names start by 1, not 0 as a lists
		assertEquals("10", board10.getId());
		assertEquals("Board name 10", board10.getName());
		assertEquals(true, board10.getPubliclyAvailable());
		assertEquals(1, board10.getTopics().size());
	}

	@Test
	void testTopicAuthorPostAuthor() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// return queryType.topics(
		// "{id date author{name email alias id type} nbPosts title content posts{id date author{name email alias} title
		// content}}",
		// "Board name 2");
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2009, 11, 20);// Month is 0-based, so this date is 2009, December the 20th
		List<Topic> topics = queries.topicAuthorPostAuthor("Board name 2", cal.getTime());

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

	@Test
	void testFindTopics() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		String boardName = "Board name 3";
		List<String> keyword = new ArrayList<>(Arrays.asList("3", "content"));

		// Go, go, go
		List<Topic> topics = queries.findTopics(boardName, keyword);

		// Verification
		assertEquals(5, topics.size());
		Topic topic = topics.get(3);
		assertEquals("The title of <33>", topic.getTitle());
		assertEquals("The content of the topic <33>", topic.getContent());
	}

	@Test
	void testCreateBoard() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		List<Board> before = queries.boardsSimple();
		String name = this.getClass().getSimpleName() + Float.floatToIntBits((float) Math.random() * Integer.MAX_VALUE);
		boolean publiclyAvailable = Math.random() > 0.5;

		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2009, 11, 20);// Month is 0-based, so this date is 2009, December the 20th

		// Go, go, go
		Board board = queries.createBoard(name, publiclyAvailable);

		// Verification
		assertEquals(name + " (Overriden DataFetcher)", board.getName(),
				"The DataFetcher for this mutation should have been overriden. See the CustomGraphQLDataFetchers class in the Forum server sample.");
		//
		List<Board> after = queries.boardsAndTopicsWithFieldParameter(cal.getTime());
		assertEquals(before.size() + 1, after.size());
		assertNull(contains(before, board.getId()));
		Board boardVerif = contains(after, board.getId());
		assertNotNull(boardVerif);
		assertEquals(board.getName(), boardVerif.getName());
		assertEquals(board.getPubliclyAvailable(), boardVerif.getPubliclyAvailable());
		assertEquals(0, boardVerif.getTopics().size());

	}

	@Test
	void testCreateTopicThenPostThenPosts()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2009, 11, 20);// Month is 0-based, so this date is 2009, December the 20th
		List<Topic> before = queries.topicAuthorPostAuthor("Board name 3", cal.getTime());

		TopicInput topicInput = new TopicInput();
		topicInput.setBoardId(before.get(0).getId());
		topicInput.setInput(getTopicPostInput(before.get(0).getAuthor(), "Some content",
				new GregorianCalendar(2009, 11 - 1, 20).getTime(), true, "The good title"));

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////// CHECK OF TOPIC CREATION
		Topic topic = queries.createTopic(topicInput);

		// Verification
		assertNotNull(topic.getId());
		assertEquals("Some content", topic.getContent());
		assertEquals(new GregorianCalendar(2009, 11 - 1, 20).getTime(), topic.getDate());
		assertEquals(true, topic.getPubliclyAvailable());
		assertEquals("The good title", topic.getTitle());

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////// CHECK OF POST CREATION
		// Preparation
		PostInput postInput = new PostInput();
		postInput.setTopicId(topic.getId());
		postInput.setInput(getTopicPostInput(before.get(0).getAuthor(), "Some other content",
				new GregorianCalendar(2009, 11 - 1, 21).getTime(), false, "The good title for a post"));

		// Go, go, go
		Post post = queries.createPost(postInput);

		// Verification
		assertNotNull(post.getId());
		assertEquals("Some other content", post.getContent());
		assertEquals(new GregorianCalendar(2009, 11 - 1, 21).getTime(), post.getDate());
		assertEquals(false, post.getPubliclyAvailable());
		assertEquals("The good title for a post", post.getTitle());

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////// CHECK OF POSTS (check the plural!) CREATION
		// Preparation
		List<PostInput> list = new ArrayList<>();
		list.add(postInput);

		// Go, go, go (the server always response with an exception: we'll check that this is the expected one)
		GraphQLRequestExecutionException e = assertThrows(GraphQLRequestExecutionException.class,
				() -> queries.createPosts(list));

		// Verification
		assertTrue(e.getMessage().contains("Spamming is forbidden"));
	}

	@Test
	void test_createMember() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		MemberInput input = new MemberInput();
		input.setAlias("an alias");
		input.setEmail("an.email@my.domain.com");
		input.setName("a member name");
		input.setType(MemberType.MODERATOR);

		// Go, go, go
		Member member = queries.createMember(input);

		// Verification
		assertNotNull(member.getId());
		assertEquals("an alias", member.getAlias());
		assertEquals("an.email@my.domain.com", member.getEmail());
		assertEquals("a member name", member.getName());
		assertEquals(MemberType.MODERATOR, member.getType());
	}

	@Test
	void test_createPost() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		Member author = new Member();
		author.setId("12");
		PostInput postInput = new PostInput();
		postInput.setTopicId("22");
		postInput.setInput(getTopicPostInput(author, "Some other content",
				new GregorianCalendar(1900, 11 - 1, 21).getTime(), false, "The good title for a post"));

		// Go, go, go
		Post post = queries.createPost(postInput);

		// Verification
		assertNotNull(post.getId());
		assertEquals("Some other content", post.getContent());
		assertEquals(new GregorianCalendar(1900, 11 - 1, 21).getTime(), post.getDate());
		assertEquals(false, post.getPubliclyAvailable());
		assertEquals("The good title for a post", post.getTitle());
	}

	@Test
	void test_createPosts() {
		// Preparation
		Member author = new Member();
		author.setId("12");
		PostInput postInput = new PostInput();
		postInput.setTopicId("22");
		postInput.setInput(getTopicPostInput(author, "Some other content",
				new GregorianCalendar(1900, 11 - 1, 21).getTime(), false, "The good title for a post"));

		List<PostInput> list = new ArrayList<>();
		list.add(postInput);

		// Go, go, go (the server always response with an exception: we'll check that this is the expected one)
		GraphQLRequestExecutionException e = assertThrows(GraphQLRequestExecutionException.class,
				() -> queries.createPosts(list));

		// Verification
		assertTrue(e.getMessage().contains("Spamming is forbidden"));
	}

	private TopicPostInput getTopicPostInput(Member author, String content, Date date, boolean publiclyAvailable,
			String title) {
		TopicPostInput input = new TopicPostInput();
		input.setAuthorId(author.getId());
		input.setContent(content);
		input.setDate(date);
		input.setPubliclyAvailable(publiclyAvailable);
		input.setTitle(title);

		return input;
	}

	public Board contains(List<Board> boards, String id) {
		for (Board b : boards) {
			if (b.getId().equals(id)) {
				return b;
			}
		}
		return null;
	}
}
