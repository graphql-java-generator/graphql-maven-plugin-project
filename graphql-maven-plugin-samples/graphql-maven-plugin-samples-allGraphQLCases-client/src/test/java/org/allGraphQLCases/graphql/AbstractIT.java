package org.allGraphQLCases.graphql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.allGraphQLCases.Queries;
import org.allGraphQLCases.client.Character;
import org.allGraphQLCases.client.CharacterInput;
import org.allGraphQLCases.client.Episode;
import org.allGraphQLCases.client.MyQueryType;
import org.junit.jupiter.api.Test;

import com.graphql_java_generator.client.response.GraphQLRequestExecutionException;
import com.graphql_java_generator.client.response.GraphQLRequestPreparationException;

/**
 * As it is suffixed by "IT", this is an integration test. Thus, it allows us to start the GraphQL StatWars server, see
 * the pom.xml file for details.
 * 
 * @author EtienneSF
 */
abstract class AbstractIT {

	MyQueryType queryType;
	Queries queries;

	@Test
	void test_withoutParameters() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		List<Character> list = queries.withoutParameters();

		assertNotNull(list);
		assertEquals(10, list.size());
		for (Character c : list) {
			checkCharacter(c, "withoutParameters", null, "Random String (", 4, 0);
		}
	}

	@Test
	void test_withOneOptionalParam() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Without parameter
		Character c = queries.withOneOptionalParam(null);
		checkCharacter(c, "test_withOneOptionalParam(null)", null, "Random String (", 0, 0);

		// With a parameter
		CharacterInput input = new CharacterInput();
		input.setName("A name");
		// Go, go, go
		c = queries.withOneOptionalParam(input);
		// Verification
		assertNotNull(c.getId());
		assertEquals("A name", c.getName());
		assertNull(c.getAppearsIn());
		assertNull(c.getFriends());
	}

	@Test
	void test_withOneMandatoryParam() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// With null parameter
		GraphQLRequestExecutionException e = assertThrows(GraphQLRequestExecutionException.class,
				() -> queries.withOneMandatoryParam(null));
		assertTrue(e.getMessage().contains("character"));

		// With a non null parameter
		CharacterInput input = new CharacterInput();
		input.setName("A name");
		// Go, go, go
		Character c = queries.withOneMandatoryParam(null);
		// Verification
		assertNotNull(c.getId());
		assertEquals("A name", c.getName());
		assertNull(c.getAppearsIn());
		assertNull(c.getFriends());
	}

	@Test
	void test_withEnum() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// With null parameter: NEWHOPE is the default value
		Character c = queries.withEnum(null);
		assertEquals(1, c.getAppearsIn().size());
		assertEquals(Episode.NEWHOPE, c.getAppearsIn().get(0));

		// With a non null parameter
		c = queries.withEnum(Episode.JEDI);
		assertEquals(1, c.getAppearsIn().size());
		assertEquals(Episode.JEDI, c.getAppearsIn().get(0));
	}

	@Test
	void test_withList() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		CharacterInput ci1 = new CharacterInput();
		ci1.setName("A name");
		CharacterInput ci2 = new CharacterInput();
		ci2.setName("Another name");
		//
		List<CharacterInput> list = new ArrayList<CharacterInput>();
		list.add(ci1);
		list.add(ci2);
		//
		String firstName = "A first name";

		// Go, go, go
		List<Character> ret = queries.withList(firstName, list);

		// Verification
		assertEquals(2, ret.size());
		//
		int i = 0;
		assertNotNull(ret.get(i).getId());
		assertEquals(firstName, ret.get(i).getName());
		//
		i += 1;
		assertNotNull(ret.get(i).getId());
		assertEquals("Another name", ret.get(i).getName());

	}

	@Test
	void test_error() {
		GraphQLRequestExecutionException e = assertThrows(GraphQLRequestExecutionException.class,
				() -> queries.error(""));
		assertTrue(e.getMessage().contains("This is an expected error"));
	}

	private void checkCharacter(Character c, String testDecription, String id, String nameStartsWith, int nbFriends,
			int nbAppearsIn) {
		assertEquals(id, c.getId(), testDecription + " (id)");
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
