package graphql.java.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import graphql.java.client.domain.starwars.Character;
import graphql.java.client.domain.starwars.CharacterImpl;
import graphql.java.client.domain.starwars.Human;
import graphql.java.client.domain.starwars.QueryType;
import graphql.java.client.domain.starwars.ScalarTest;
import graphql.java.client.response.GraphQLRequestPreparationException;

class GraphqlUtilsTest {

	GraphqlUtils graphqlUtils;

	@BeforeEach
	void setUp() throws Exception {
		graphqlUtils = new GraphqlUtils();
	}

	@Test
	void testCheckName() throws GraphQLRequestPreparationException {
		// Some valid name: we call check, and no exception should be thrown
		graphqlUtils.checkName("avalidname");
		graphqlUtils.checkName("aValidName");
		graphqlUtils.checkName("_aValidName");
		graphqlUtils.checkName("ValidName");

		// Various types of checks KO
		assertThrows(NullPointerException.class, () -> graphqlUtils.checkName(null));
		assertThrows(GraphQLRequestPreparationException.class, () -> graphqlUtils.checkName("qdqd qdsq"));
		assertThrows(GraphQLRequestPreparationException.class, () -> graphqlUtils.checkName("qdqd.qdsq"));
		assertThrows(GraphQLRequestPreparationException.class, () -> graphqlUtils.checkName("qdqdqdsq."));
		assertThrows(GraphQLRequestPreparationException.class, () -> graphqlUtils.checkName(".qdqdqdsq"));
		assertThrows(GraphQLRequestPreparationException.class, () -> graphqlUtils.checkName("qdqdqdsq*"));
		assertThrows(GraphQLRequestPreparationException.class, () -> graphqlUtils.checkName("qdqdqdsèq"));
	}

	@Test
	void test_getPascalCase() {
		assertEquals("PascalCase", graphqlUtils.getPascalCase("pascalCase"));
		assertEquals("PascalCase", graphqlUtils.getPascalCase("PascalCase"));
	}

	@Test
	void test_checkIsScalar_field() throws NoSuchFieldException, SecurityException, GraphQLRequestPreparationException {

		assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkIsScalar(ScalarTest.class.getDeclaredField("episode"), false));
		graphqlUtils.checkIsScalar(ScalarTest.class.getDeclaredField("episode"), true);

		assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkIsScalar(Human.class.getDeclaredField("id"), false));
		graphqlUtils.checkIsScalar(Human.class.getDeclaredField("id"), true);

		assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkIsScalar(Human.class.getDeclaredField("name"), false));
		graphqlUtils.checkIsScalar(Human.class.getDeclaredField("name"), true);

		assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkIsScalar(Human.class.getDeclaredField("appearsIn"), false));
		graphqlUtils.checkIsScalar(Human.class.getDeclaredField("appearsIn"), true);

	}

	@Test
	void test_checkIsScalar_method()
			throws NoSuchMethodException, SecurityException, GraphQLRequestPreparationException {
		GraphQLRequestPreparationException e;

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkIsScalar("id", Character.class.getMethod("getId"), false));
		assertTrue(e.getMessage().contains("getId"), "getId");
		assertTrue(e.getMessage().contains("id"), "id");
		graphqlUtils.checkIsScalar("id", Character.class.getMethod("getId"), true);

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkIsScalar("name", Character.class.getMethod("getName"), false));
		assertTrue(e.getMessage().contains("getName"), "getName");
		assertTrue(e.getMessage().contains("name"), "name");
		graphqlUtils.checkIsScalar("name", Character.class.getMethod("getName"), true);

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkIsScalar("friends", Character.class.getMethod("getFriends"), true));
		assertTrue(e.getMessage().contains("getFriends"), "getFriends");
		assertTrue(e.getMessage().contains("friends"), "friends");
		graphqlUtils.checkIsScalar("friends", Character.class.getMethod("getFriends"), false);
	}

	@Test
	void checkFieldOfGraphQLType_Objets() throws GraphQLRequestPreparationException {
		GraphQLRequestPreparationException e;

		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Wrong field names, for interface and classes (shouldBeScalar : null)
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkFieldOfGraphQLType("wrong", null, Character.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkFieldOfGraphQLType("wrong", null, CharacterImpl.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkFieldOfGraphQLType("wrong", null, Human.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Wrong field names, for interface and classes (shouldBeScalar : false)
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkFieldOfGraphQLType("wrong", false, Character.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkFieldOfGraphQLType("wrong", false, CharacterImpl.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkFieldOfGraphQLType("wrong", false, Human.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Wrong field names, for interface and classes (shouldBeScalar : true)
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkFieldOfGraphQLType("wrong", true, Character.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkFieldOfGraphQLType("wrong", true, CharacterImpl.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkFieldOfGraphQLType("wrong", true, Human.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Correct field names (check with wrong scalar type, then correct scalar type). Variation for shouldBeScalar
		// (null or not)
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkFieldOfGraphQLType("id", false, Character.class));
		assertTrue(e.getMessage().contains("id"), "id");
		//
		assertEquals(String.class, graphqlUtils.checkFieldOfGraphQLType("id", null, Character.class), "id : scalar OK");
		assertEquals(String.class, graphqlUtils.checkFieldOfGraphQLType("id", true, Character.class), "id : scalar OK");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkFieldOfGraphQLType("id", false, CharacterImpl.class));
		assertTrue(e.getMessage().contains("id"), "id");
		//
		assertEquals(String.class, graphqlUtils.checkFieldOfGraphQLType("id", null, CharacterImpl.class),
				"id : scalar OK");
		assertEquals(String.class, graphqlUtils.checkFieldOfGraphQLType("id", true, CharacterImpl.class),
				"id : scalar OK");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkFieldOfGraphQLType("id", false, Human.class));
		assertTrue(e.getMessage().contains("id"), "id");
		//
		assertEquals(String.class, graphqlUtils.checkFieldOfGraphQLType("id", null, Human.class), "id : scalar OK");
		assertEquals(String.class, graphqlUtils.checkFieldOfGraphQLType("id", true, Human.class), "id : scalar OK");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkFieldOfGraphQLType("friends", true, Character.class));
		assertTrue(e.getMessage().contains("friends"), "friends");
		//
		assertEquals(String.class, graphqlUtils.checkFieldOfGraphQLType("id", null, Character.class), "id : scalar OK");
		assertEquals(String.class, graphqlUtils.checkFieldOfGraphQLType("id", true, Character.class), "id : scalar OK");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkFieldOfGraphQLType("friends", true, CharacterImpl.class));
		assertTrue(e.getMessage().contains("friends"), "friends");
		//
		assertEquals(String.class, graphqlUtils.checkFieldOfGraphQLType("id", null, CharacterImpl.class),
				"id : scalar OK");
		assertEquals(String.class, graphqlUtils.checkFieldOfGraphQLType("id", true, CharacterImpl.class),
				"id : scalar OK");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkFieldOfGraphQLType("friends", true, Human.class));
		assertTrue(e.getMessage().contains("friends"), "friends");
		//
		assertEquals(String.class, graphqlUtils.checkFieldOfGraphQLType("id", null, Human.class), "id : scalar OK");
		assertEquals(String.class, graphqlUtils.checkFieldOfGraphQLType("id", true, Human.class), "id : scalar OK");

	}

	@Test
	void checkFieldOfGraphQLType_Query() throws GraphQLRequestPreparationException {
		GraphQLRequestPreparationException e;

		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Wrong field name, for interface and classes (shouldBeScalar : null, true, false)
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkFieldOfGraphQLType("wrong", null, QueryType.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkFieldOfGraphQLType("wrong", true, QueryType.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkFieldOfGraphQLType("wrong", false, QueryType.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Correct field name, for interface and classes (shouldBeScalar : null, true, false)
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlUtils.checkFieldOfGraphQLType("hero", true, QueryType.class));
		assertTrue(e.getMessage().contains("hero"), "hero");

		assertEquals(Character.class, graphqlUtils.checkFieldOfGraphQLType("hero", null, QueryType.class),
				"hero : scalar OK");
		assertEquals(Character.class, graphqlUtils.checkFieldOfGraphQLType("hero", false, QueryType.class),
				"hero : scalar OK");
	}
}
