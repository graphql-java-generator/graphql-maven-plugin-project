package graphql.java.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import graphql.java.client.QueryExecutor;
import graphql.java.client.domain.Character;
import graphql.java.client.domain.Human;
import graphql.java.client.domain.QueryType;
import graphql.java.client.request.ObjectResponseDef.Builder;
import graphql.java.client.response.GraphQLRequestPreparationException;

class ObjectResponseDefTest {

	@Test
	void testResponseDefinitionImpl() {
		ObjectResponseDef objectResponseDef = new ObjectResponseDef(Human.class);
		objectResponseDef.setOwningClass(QueryType.class);

		assertEquals(0, objectResponseDef.fields.size(), "list fields initialized, and no field before start");
		assertEquals(0, objectResponseDef.subObjects.size(), "list subObjects initialized, and no field before start");
		assertEquals(QueryExecutor.GRAPHQL_MARKER, objectResponseDef.marker, "Marker");
		assertEquals(Human.class, objectResponseDef.fieldClass, "Name");
		assertNull(objectResponseDef.fieldAlias, "alias");
	}

	@Test
	void test_setFieldName_OK() throws GraphQLRequestPreparationException {
		// Preparation
		ObjectResponseDef objectResponseDef = new ObjectResponseDef(Character.class);
		objectResponseDef.setOwningClass(Human.class);

		// Go, go, go
		objectResponseDef.setField("friends");

		// Verification
		assertEquals("friends", objectResponseDef.getFieldName(), "getter");
		assertEquals("friends", objectResponseDef.fieldName, "attribute");
	}

	@Test
	void test_setFieldName_KO() {
		// Preparation
		ObjectResponseDef objectResponseDef = new ObjectResponseDef(Character.class);
		objectResponseDef.setOwningClass(Human.class);
		Exception e;

		// Go, go, go
		e = assertThrows(NullPointerException.class, () -> objectResponseDef.setField(null));
		e = assertThrows(GraphQLRequestPreparationException.class, () -> objectResponseDef.setField("doesNotExist"));
		assertTrue(e.getMessage().contains("doesNotExist"), "doesNotExist doesn't exist");

		e = assertThrows(GraphQLRequestPreparationException.class, () -> objectResponseDef.setField("name"));
		assertTrue(e.getMessage().contains("name"), "name is a scalar (no response def for a scalar)");
	}

	@Test
	void test_setFieldAlias_OK() throws GraphQLRequestPreparationException {
		// Preparation
		ObjectResponseDef objectResponseDef = new ObjectResponseDef(Character.class);
		objectResponseDef.setOwningClass(Human.class);

		// Go, go, go
		objectResponseDef.setField("friends", "aValidAlias");

		// Verification
		assertEquals("friends", objectResponseDef.getFieldName(), "getter");
		assertEquals("friends", objectResponseDef.fieldName, "attribute");
		assertEquals("aValidAlias", objectResponseDef.getFieldAlias(), "getter");
		assertEquals("aValidAlias", objectResponseDef.fieldAlias, "attribute");

		// Go, go, go
		objectResponseDef.setField("friends", null);

		// Verification
		assertEquals(null, objectResponseDef.getFieldAlias(), "getter");
		assertEquals(null, objectResponseDef.fieldAlias, "attribute");
	}

	@Test
	void test_setFieldAlias_KO() {
		// Preparation
		ObjectResponseDef objectResponseDef = new ObjectResponseDef(Character.class);
		objectResponseDef.setOwningClass(Human.class);

		// Go, go, go
		Exception e = assertThrows(GraphQLRequestPreparationException.class,
				() -> objectResponseDef.setField("friends", "not valid"));
		assertTrue(e.getMessage().contains("not valid"), "'not valid' is not a valid identifier");
	}

	@Test
	void testAppendResponseQuery_noSubResponseDef() throws GraphQLRequestPreparationException {
		// Preparation
		ObjectResponseDef objectResponseDef = ObjectResponseDef.newQueryResponseDefBuilder(QueryType.class, "human")
				.withField("name", "aliasForName").withField("id").withField("homePlanet").build();
		StringBuilder sb = new StringBuilder();

		// Go, go, go
		objectResponseDef.appendResponseQuery(sb);

		// Verification
		assertEquals("{ aliasForName: name id homePlanet}", sb.toString());
	}

	@Test
	void testAppendResponseQuery_withSubObjects() throws GraphQLRequestPreparationException {
		// Preparation

		ObjectResponseDef subFriendsResponseDef = ObjectResponseDef.newSubObjectResponseDefBuilder(Character.class)
				.withField("id").withField("name").withField("appearsIn").build();

		ObjectResponseDef friendsResponseDef = ObjectResponseDef.newSubObjectResponseDefBuilder(Character.class)
				.withField("id").withField("name", "aliasForName").withSubObject("friends", subFriendsResponseDef)
				.build();

		Builder builder = ObjectResponseDef.newQueryResponseDefBuilder(QueryType.class, "human");
		builder.withField("id", "aliasForId");
		builder.withSubObject("friends", "aliasForFriends", friendsResponseDef);
		builder.withField("name");
		builder.withField("homePlanet");
		ObjectResponseDef objectResponseDef = builder.build();

		StringBuilder sb = new StringBuilder();

		// Go, go, go
		objectResponseDef.appendResponseQuery(sb);

		// Verification
		assertEquals(3, objectResponseDef.fields.size(), "Human: fields");
		assertEquals(1, objectResponseDef.subObjects.size(), "Human: objects");

		// Friends : first sublevel
		assertEquals(2, objectResponseDef.subObjects.get(0).fields.size(), "friends (first level): fields");
		assertEquals(1, objectResponseDef.subObjects.get(0).subObjects.size(), "friends (first level): objects");

		// Friends : second sublevel
		assertEquals(3, objectResponseDef.subObjects.get(0).subObjects.get(0).fields.size(),
				"friends (second level): fields");
		assertEquals(0, objectResponseDef.subObjects.get(0).subObjects.get(0).subObjects.size(),
				"friends (second level): objects");

		assertEquals(
				"{ aliasForId: id name homePlanet aliasForFriends: friends{ id aliasForName: name friends{ id name appearsIn}}}",
				sb.toString());
	}
}
