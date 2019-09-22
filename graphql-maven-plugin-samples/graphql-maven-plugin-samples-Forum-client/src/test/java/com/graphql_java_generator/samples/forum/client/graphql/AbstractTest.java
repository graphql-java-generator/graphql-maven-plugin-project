package com.graphql_java_generator.samples.forum.client.graphql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.graphql_java_generator.client.response.GraphQLExecutionException;
import com.graphql_java_generator.client.response.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.Queries;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Board;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Member;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.MemberType;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Post;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Topic;

/**
 * As it is suffixed by "IT", this is an integration test. Thus, it allows us to start the GraphQL StatWars server, see
 * the pom.xml file for details.
 * 
 * @author EtienneSF
 */
abstract class AbstractTest {

	Queries queries;

	@Test
	void testBoardsSimple() throws GraphQLExecutionException, GraphQLRequestPreparationException {

		// return queryType.boards("{id name publiclyAvailable}");
		List<Board> boards = queries.boardsSimple();

		// Verification
		assertTrue(boards.size() >= 10, "10 boards at startup, then new ones are created by the tests");

		Board board2 = boards.get(1); // Board names start by 1, not 0 as a lists
		assertEquals("00000000-0000-0000-0000-000000000002", board2.getId());
		assertEquals("Board name 2", board2.getName());
		assertEquals(false, board2.getPubliclyAvailable());
		assertEquals(null, board2.getTopics());

		Board board10 = boards.get(9); // Board names start by 1, not 0 as a lists
		assertEquals("00000000-0000-0000-0000-000000000010", board10.getId());
		assertEquals("Board name 10", board10.getName());
		assertEquals(true, board10.getPubliclyAvailable());
		assertEquals(null, board10.getTopics());
	}

	@Test
	void testTopicAuthorPostAuthor() throws GraphQLExecutionException, GraphQLRequestPreparationException {

		// return queryType.topics(
		// "{id date author{name email alias id type} nbPosts title content posts{id date author{name email alias} title
		// content}}",
		// "Board name 2");
		List<Topic> topics = queries.topicAuthorPostAuthor();

		assertEquals(5, topics.size());

		Topic topic12 = topics.get(1);
		//
		assertEquals("The content of the topic <12>", topic12.getContent());
		assertEquals("2018-12-20 00:07:10", topic12.getDate());
		assertEquals(12, (int) topic12.getNbPosts());
		assertEquals("The title of <12>", topic12.getTitle());
		assertEquals("00000000-0000-0000-0000-000000000012", topic12.getId());
		//
		Member author12 = topic12.getAuthor();// All its fields have been loaded
		assertNotNull(author12);
		assertEquals("00000000-0000-0000-0000-000000000012", author12.getId());
		assertEquals("Name 12", author12.getName());
		assertEquals("Alias of Name 12", author12.getAlias());
		assertEquals("name.12@graphql-java.com", author12.getEmail());
		assertEquals(MemberType.STANDARD, author12.getType());

		// Let's check for one post
		List<Post> posts12 = topic12.getPosts();
		assertEquals(8, posts12.size());
		//
		Post post232 = posts12.get(5);
		assertEquals("2018-05-13 13:47:10", post232.getDate());
		assertEquals("00000000-0000-0000-0000-000000000232", post232.getId());
		assertEquals("The content of the post <232>", post232.getContent());
		assertEquals(null, post232.getPubliclyAvailable()); // Not queried
		assertEquals("The title of <232>", post232.getTitle());
		//
		Member author12bis = post232.getAuthor();// This one is partially loaded: author{name email alias}
		assertNotNull(author12bis);
		assertEquals(null, author12bis.getId());
		assertEquals("Name 12", author12bis.getName());
		assertEquals("Alias of Name 12", author12bis.getAlias());
		assertEquals("name.12@graphql-java.com", author12bis.getEmail());
		assertEquals(null, author12bis.getType());
	}

	@Test
	void testCreateBoards() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		// Preparation
		List<Board> before = queries.boardsSimple();
		String name = this.getClass().getSimpleName() + Float.floatToIntBits((float) Math.random() * Integer.MAX_VALUE);
		boolean publiclyAvailable = Math.random() > 0.5;

		// Go, go, go
		Board board = queries.createBoard(name, publiclyAvailable);

		// Verification
		List<Board> after = queries.boardsAndTopics();
		assertEquals(before.size() + 1, after.size());
		assertNull(contains(before, board.getId()));
		Board boardVerif = contains(after, board.getId());
		assertNotNull(boardVerif);
		assertEquals(board.getName(), boardVerif.getName());
		assertEquals(board.getPubliclyAvailable(), boardVerif.getPubliclyAvailable());
		assertEquals(0, boardVerif.getTopics().size());

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
