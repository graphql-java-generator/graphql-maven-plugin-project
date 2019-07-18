package com.graphql_java_generator.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.graphql_java_generator.client.QueryExecutor;
import com.graphql_java_generator.client.domain.starwars.Character;
import com.graphql_java_generator.client.domain.starwars.Human;
import com.graphql_java_generator.client.domain.starwars.QueryType;
import com.graphql_java_generator.client.request.Builder;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.client.response.GraphQLRequestPreparationException;

class ObjectResponseDefTest {

	@Test
	void testResponseDefinitionImpl() {
		ObjectResponse objectResponse = new ObjectResponse(Human.class);
		objectResponse.setOwningClass(QueryType.class);

		assertEquals(0, objectResponse.scalarFields.size(), "list fields initialized, and no field before start");
		assertEquals(0, objectResponse.subObjects.size(), "list subObjects initialized, and no field before start");
		assertEquals(QueryExecutor.GRAPHQL_MARKER, objectResponse.marker, "Marker");
		assertEquals(Human.class, objectResponse.fieldClass, "Name");
		assertNull(objectResponse.fieldAlias, "alias");
	}

	@Test
	void test_setFieldName_OK() throws GraphQLRequestPreparationException {
		// Preparation
		ObjectResponse objectResponse = new ObjectResponse(Character.class);
		objectResponse.setOwningClass(Human.class);

		// Go, go, go
		objectResponse.setField("friends");

		// Verification
		assertEquals("friends", objectResponse.getFieldName(), "getter");
		assertEquals("friends", objectResponse.fieldName, "attribute");
	}

	@Test
	void test_setFieldName_KO() {
		// Preparation
		ObjectResponse objectResponse = new ObjectResponse(Character.class);
		objectResponse.setOwningClass(Human.class);
		Exception e;

		// Go, go, go
		e = assertThrows(NullPointerException.class, () -> objectResponse.setField(null));
		e = assertThrows(GraphQLRequestPreparationException.class, () -> objectResponse.setField("doesNotExist"));
		assertTrue(e.getMessage().contains("doesNotExist"), "doesNotExist doesn't exist");

		e = assertThrows(GraphQLRequestPreparationException.class, () -> objectResponse.setField("name"));
		assertTrue(e.getMessage().contains("name"), "name is a scalar (no response def for a scalar)");
	}

	@Test
	void test_setFieldAlias_OK() throws GraphQLRequestPreparationException {
		// Preparation
		ObjectResponse objectResponse = new ObjectResponse(Character.class);
		objectResponse.setOwningClass(Human.class);

		// Go, go, go
		objectResponse.setField("friends", "aValidAlias");

		// Verification
		assertEquals("friends", objectResponse.getFieldName(), "getter");
		assertEquals("friends", objectResponse.fieldName, "attribute");
		assertEquals("aValidAlias", objectResponse.getFieldAlias(), "getter");
		assertEquals("aValidAlias", objectResponse.fieldAlias, "attribute");

		// Go, go, go
		objectResponse.setField("friends", null);

		// Verification
		assertEquals(null, objectResponse.getFieldAlias(), "getter");
		assertEquals(null, objectResponse.fieldAlias, "attribute");
	}

	@Test
	void test_setFieldAlias_KO() {
		// Preparation
		ObjectResponse objectResponse = new ObjectResponse(Character.class);
		objectResponse.setOwningClass(Human.class);

		// Go, go, go
		Exception e = assertThrows(GraphQLRequestPreparationException.class,
				() -> objectResponse.setField("friends", "not valid"));
		assertTrue(e.getMessage().contains("not valid"), "'not valid' is not a valid identifier");
	}

	@Test
	void testAppendResponseQuery_noSubResponseDef() throws GraphQLRequestPreparationException {
		// Preparation
		ObjectResponse objectResponse = ObjectResponse.newQueryResponseDefBuilder(QueryType.class, "human")
				.withField("name", "aliasForName").withField("id").withField("homePlanet").build();
		StringBuilder sb = new StringBuilder();

		// Go, go, go
		objectResponse.appendResponseQuery(sb);

		// Verification
		assertEquals("{ aliasForName: name id homePlanet}", sb.toString());
	}

	@Test
	void testAppendResponseQuery_withSubObjects() throws GraphQLRequestPreparationException {
		// Preparation

		ObjectResponse subFriendsResponseDef = ObjectResponse.newSubObjectBuilder(Character.class)
				.withField("id").withField("name").withField("appearsIn").build();

		ObjectResponse friendsResponseDef = ObjectResponse.newSubObjectBuilder(Character.class)
				.withField("id").withField("name", "aliasForName").withSubObject("friends", subFriendsResponseDef)
				.build();

		Builder builder = ObjectResponse.newQueryResponseDefBuilder(QueryType.class, "human");
		builder.withField("id", "aliasForId");
		builder.withSubObject("friends", "aliasForFriends", friendsResponseDef);
		builder.withField("name");
		builder.withField("homePlanet");
		ObjectResponse objectResponse = builder.build();

		StringBuilder sb = new StringBuilder();

		// Go, go, go
		objectResponse.appendResponseQuery(sb);

		// Verification
		assertEquals(3, objectResponse.scalarFields.size(), "Human: scalarFields");
		assertEquals(1, objectResponse.subObjects.size(), "Human: objects");

		// Friends : first sublevel
		assertEquals(2, objectResponse.subObjects.get(0).scalarFields.size(), "friends (first level): scalarFields");
		assertEquals(1, objectResponse.subObjects.get(0).subObjects.size(), "friends (first level): objects");

		// Friends : second sublevel
		assertEquals(3, objectResponse.subObjects.get(0).subObjects.get(0).scalarFields.size(),
				"friends (second level): scalarFields");
		assertEquals(0, objectResponse.subObjects.get(0).subObjects.get(0).subObjects.size(),
				"friends (second level): objects");

		assertEquals(
				"{ aliasForId: id name homePlanet aliasForFriends: friends{ id aliasForName: name friends{ id name appearsIn}}}",
				sb.toString());
	}
}
