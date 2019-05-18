package org.graphql.maven.plugin.samples.forum.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.graphql.maven.plugin.samples.forum.client.forum.client.Board;
import org.graphql.maven.plugin.samples.forum.client.forum.client.Member;
import org.graphql.maven.plugin.samples.forum.client.forum.client.MemberType;
import org.graphql.maven.plugin.samples.forum.client.forum.client.Post;
import org.graphql.maven.plugin.samples.forum.client.forum.client.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import graphql.java.client.response.GraphQLExecutionException;
import graphql.java.client.response.GraphQLRequestPreparationException;

class GraphqlClientIT {

	GraphqlClient graphqlClient;

	@BeforeEach
	void setUp() throws Exception {
		graphqlClient = new GraphqlClient();
	}

	@Test
	void testBoardsSimple() throws GraphQLExecutionException, GraphQLRequestPreparationException {

		// return queryType.boards("{id name publiclyAvailable}");
		List<Board> boards = graphqlClient.boardsSimple();

		// Verification
		assertEquals(10, boards.size());

		Board board2 = boards.get(1); // Board names start by 1, not 0 as a lists
		assertEquals("Board2", board2.getId());
		assertEquals("Board name 2", board2.getName());
		assertEquals(false, board2.getPubliclyAvailable());
		assertEquals(null, board2.getTopics());

		Board board10 = boards.get(9); // Board names start by 1, not 0 as a lists
		assertEquals("Board10", board10.getId());
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
		List<Topic> topics = graphqlClient.topicAuthorPostAuthor();

		assertEquals(5, topics.size());

		Topic topic12 = topics.get(1);
		//
		assertEquals("The content of the topic <Topic12>", topic12.getContent());
		assertEquals("2019-05-03 09:12:47", topic12.getDate());
		assertEquals(12, (int) topic12.getNbPosts());
		assertEquals("The title of <Topic12>", topic12.getTitle());
		assertEquals("Topic12", topic12.getId());
		//
		Member author12 = topic12.getAuthor();// All its fields have been loaded
		assertNotNull(author12);
		assertEquals("Member12", author12.getId());
		assertEquals("Name 12", author12.getName());
		assertEquals("Alias of Name 12", author12.getAlias());
		assertEquals("name.12@graphql-java.com", author12.getEmail());
		assertEquals(MemberType.STANDARD, author12.getType());

		// Let's check for one post
		List<Post> posts12 = topic12.getPosts();
		assertEquals(8, posts12.size());
		//
		Post post232 = posts12.get(5);
		assertEquals("2018-09-24 22:52:47", post232.getDate());
		assertEquals("Post232", post232.getId());
		assertEquals("The content of the post <Post232>", post232.getContent());
		assertEquals(null, post232.getPubliclyAvailable()); // Not queried
		assertEquals("The title of <Post232>", post232.getTitle());
		//
		Member author12bis = post232.getAuthor();// This one is partially loaded: author{name email alias}
		assertNotNull(author12bis);
		assertEquals(null, author12bis.getId());
		assertEquals("Name 12", author12bis.getName());
		assertEquals("Alias of Name 12", author12bis.getAlias());
		assertEquals("name.12@graphql-java.com", author12bis.getEmail());
		assertEquals(null, author12bis.getType());
	}

}
