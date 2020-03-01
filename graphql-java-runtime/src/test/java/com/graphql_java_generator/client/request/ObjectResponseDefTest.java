package com.graphql_java_generator.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import com.graphql_java_generator.client.QueryExecutor;
import com.graphql_java_generator.client.domain.starwars.Character;
import com.graphql_java_generator.client.domain.starwars.Human;
import com.graphql_java_generator.client.domain.starwars.QueryType;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

class ObjectResponseDefTest {

	@Test
	void testResponseDefinitionImpl() throws GraphQLRequestPreparationException {
		ObjectResponse objectResponse = new ObjectResponse(QueryType.class, "human");

		assertEquals(0, objectResponse.scalarFields.size(), "list fields initialized, and no field before start");
		assertEquals(0, objectResponse.subObjects.size(), "list subObjects initialized, and no field before start");
		assertEquals(QueryExecutor.GRAPHQL_MARKER, objectResponse.marker, "Marker");
		assertEquals(Human.class, objectResponse.getFieldClass(), "Classname");
		assertEquals("human", objectResponse.getFieldName(), "name");
		assertNull(objectResponse.getFieldAlias(), "alias");
	}

	@Test
	void test_setFieldName_OK() throws GraphQLRequestPreparationException {
		// Preparation
		ObjectResponse objectResponse = new ObjectResponse(Human.class, "friends");

		// Verification
		assertEquals("friends", objectResponse.getFieldName(), "getter");
		assertEquals("friends", objectResponse.field.name, "attribute");
		assertEquals(null, objectResponse.field.alias, "alias");
	}

	@Test
	void test_setFieldName_KO() throws GraphQLRequestPreparationException {
		Exception e = assertThrows(NullPointerException.class, () -> new ObjectResponse(Human.class, null));
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new ObjectResponse(Human.class, "doesNotExist"));
		assertTrue(e.getMessage().contains("doesNotExist"), "doesNotExist doesn't exist");

		e = assertThrows(GraphQLRequestPreparationException.class, () -> new ObjectResponse(Human.class, "name"));
		assertTrue(e.getMessage().contains("name"), "name is a scalar (no response def for a scalar)");
	}

	@Test
	void test_setFieldAlias_OK() throws GraphQLRequestPreparationException {
		// Preparation
		ObjectResponse objectResponse = new ObjectResponse(Human.class, "friends", "aValidAlias");

		// Verification
		assertEquals("friends", objectResponse.getFieldName(), "getter");
		assertEquals("friends", objectResponse.field.name, "attribute");
		assertEquals("aValidAlias", objectResponse.getFieldAlias(), "getter");
		assertEquals("aValidAlias", objectResponse.field.alias, "attribute");
	}

	@Test
	void test_setFieldAlias_KO() {
		Exception e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new ObjectResponse(Human.class, "friends", "not valid"));
		assertTrue(e.getMessage().contains("not valid"), "'not valid' is not a valid identifier");
	}

	@Test
	void testAppendResponseQuery_noSubResponseDef()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		ObjectResponse objectResponse = new Builder(QueryType.class, "human").withField("name", "aliasForName")
				.withField("id").withField("homePlanet").build();
		StringBuilder sb = new StringBuilder();

		// Go, go, go
		objectResponse.appendResponseQuery(sb, new HashMap<String, Object>(), false);

		// Verification
		assertEquals("human{aliasForName:name id homePlanet __datatype}", sb.toString());
	}

	@Test
	void testAppendResponseQuery_withSubObjects()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation

		ObjectResponse subFriendsResponseDef = new Builder(Character.class, "friends").withField("id").withField("name")
				.withField("appearsIn").build();

		ObjectResponse friendsResponseDef = new Builder(Human.class, "friends", "aliasForFriends").withField("id")
				.withField("name", "aliasForName").withSubObject(subFriendsResponseDef).build();

		Builder builder = new Builder(QueryType.class, "human");
		builder.withField("id", "aliasForId");
		builder.withSubObject(friendsResponseDef);
		builder.withField("name");
		builder.withField("homePlanet");
		ObjectResponse objectResponse = builder.build();

		StringBuilder sb = new StringBuilder();

		// Go, go, go
		objectResponse.appendResponseQuery(sb, new HashMap<String, Object>(), false);

		// Verification
		assertEquals(4, objectResponse.scalarFields.size(), "Human: scalarFields (with the added __datatype field)");
		assertEquals(1, objectResponse.subObjects.size(), "Human: objects");

		// Friends : first sublevel
		assertEquals(3, objectResponse.subObjects.get(0).scalarFields.size(),
				"friends (first level): scalarFields (with the added __datatype field)");
		assertEquals(1, objectResponse.subObjects.get(0).subObjects.size(), "friends (first level): objects");

		// Friends : second sublevel
		assertEquals(4, objectResponse.subObjects.get(0).subObjects.get(0).scalarFields.size(),
				"friends (second level): scalarFields (with the added __datatype field)");
		assertEquals(0, objectResponse.subObjects.get(0).subObjects.get(0).subObjects.size(),
				"friends (second level): objects");

		assertEquals(
				"human{aliasForId:id name homePlanet __datatype aliasForFriends:friends{id aliasForName:name __datatype friends{id name appearsIn __datatype}}}",
				sb.toString());
	}
}
