package graphql.java.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import graphql.java.client.domain.Character;
import graphql.java.client.domain.Droid;
import graphql.java.client.domain.Human;
import graphql.java.client.domain.QueryType;
import graphql.java.client.request.ObjectResponseDef.Builder;
import graphql.java.client.response.GraphQLRequestPreparationException;

class ResponseDefBuilderTest {

	Class<?> clazz = Human.class;
	Builder humanResponseDefBuilder;

	@BeforeEach
	void setUp() throws Exception {
		humanResponseDefBuilder = ObjectResponseDef.newQueryResponseDefBuilder(QueryType.class, "human");
		assertEquals(0, humanResponseDefBuilder.objectResponseDef.fields.size(),
				"list fields initialized, and no field before start");
		assertEquals(0, humanResponseDefBuilder.objectResponseDef.subObjects.size(),
				"list subObjects initialized, and no field before start");
	}

	@Test
	void testWithField_Ok() throws GraphQLRequestPreparationException {
		// Go, go, go
		humanResponseDefBuilder.withField("name");

		// Verification
		assertEquals(1, humanResponseDefBuilder.objectResponseDef.fields.size(), "one field in the list");
		assertEquals("name", humanResponseDefBuilder.objectResponseDef.fields.get(0).name, "field name");
	}

	@Test
	void testWithField_KO_InvalidIdentifier() {
		GraphQLRequestPreparationException e;

		// Most important check: only attribute of the clazz class may be added.
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withField("fieldNotInThisClass"));
		assertTrue(e.getMessage().contains("fieldNotInThisClass"));

		// Field added, but it is a subObject
		e = assertThrows(GraphQLRequestPreparationException.class, () -> humanResponseDefBuilder.withField("friends"));
		assertTrue(e.getMessage().contains("friends"));

		// Various types of checks, for invalid identifiers
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withField("qdqd qdsq"));
		assertTrue(e.getMessage().contains("qdqd qdsq"));
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withField("qdqd.qdsq"));
		assertTrue(e.getMessage().contains("qdqd.qdsq"));
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withField("qdqdqdsq."));
		assertTrue(e.getMessage().contains("qdqdqdsq."));
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withField(".qdqdqdsq"));
		assertTrue(e.getMessage().contains(".qdqdqdsq"));
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withField("qdqdqdsq*"));
		assertTrue(e.getMessage().contains("qdqdqdsq*"));
	}

	@Test
	void testWithFieldWithAlias_OK() throws GraphQLRequestPreparationException {
		// Go, go, go
		humanResponseDefBuilder.withField("id", "idAlias");

		// Verification
		assertEquals("id", humanResponseDefBuilder.objectResponseDef.fields.get(0).name, "name");
		assertEquals("idAlias", humanResponseDefBuilder.objectResponseDef.fields.get(0).alias, "alias");
	}

	@Test
	void testWithFieldWithAlias_KO() {
		// KO on the name
		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withField("notAnExistingAttribute", "validAlias"));
		assertTrue(e.getMessage().contains("notAnExistingAttribute"));

		// Field added, but it is a subObject
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withField("friends", "anAlias"));
		assertTrue(e.getMessage().contains("friends"));

		// KO on the alias
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withField("id", "qdqd qdsq"));
		assertTrue(e.getMessage().contains("qdqd qdsq"));
	}

	@Test
	void testWithSubObject_OK() throws GraphQLRequestPreparationException {
		// Go, go, go
		humanResponseDefBuilder.withSubObject("friends", ObjectResponseDef
				.newSubObjectResponseDefBuilder(Character.class).withField("id").withField("name").build());

		// Verification
		assertEquals(1, humanResponseDefBuilder.objectResponseDef.subObjects.size(), "one object in the list");
		//
		ObjectResponseDef subObject = humanResponseDefBuilder.objectResponseDef.subObjects.get(0);
		assertEquals("friends", subObject.fieldName, "subobject fieldName");
		assertEquals(null, subObject.fieldAlias, "subobject fieldAlias");
		assertEquals(Character.class, subObject.fieldClass, "subobject clazz");
		//
		assertEquals(2, subObject.fields.size(), "subobject: nb fields");
		assertEquals(0, subObject.subObjects.size(), "subobject: nb subObjects");
		assertEquals("id", subObject.fields.get(0).name, "subobject: field 0");
		assertEquals("name", subObject.fields.get(1).name, "subobject: field 1");
	}

	@Test
	void testWithSubObject_KO() {
		GraphQLRequestPreparationException e;

		// Bad class for the ReponseDef of the subObject : different of the type of the given field name
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withSubObject("friends", ObjectResponseDef
						.newSubObjectResponseDefBuilder(Droid.class).withField("id").withField("name").build()));
		assertTrue(e.getMessage().contains("friends"));
		assertTrue(e.getMessage().contains("Droid"));

		// Non existant field
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withSubObject("mml", ObjectResponseDef
						.newSubObjectResponseDefBuilder(Character.class).withField("id").withField("name").build()));
		assertTrue(e.getMessage().contains("mml"));

		// subObject added, whereas it is a Field
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withSubObject("appearsIn", ObjectResponseDef
						.newSubObjectResponseDefBuilder(Character.class).withField("id").withField("name").build()));
		assertTrue(e.getMessage().contains("appearsIn"));
	}

	@Test
	void testWithSubObject_withAlias_OK() throws GraphQLRequestPreparationException {
		// Go, go, go
		humanResponseDefBuilder.withSubObject("friends", "aValidAlias", ObjectResponseDef
				.newSubObjectResponseDefBuilder(Character.class).withField("id").withField("name").build());

		// Verification
		assertEquals(1, humanResponseDefBuilder.objectResponseDef.subObjects.size(), "one field in the list");
		//
		ObjectResponseDef subObject = humanResponseDefBuilder.objectResponseDef.subObjects.get(0);
		assertEquals("friends", subObject.fieldName, "subobject fieldName");
		assertEquals("aValidAlias", subObject.fieldAlias, "subobject fieldAlias");
		assertEquals(Character.class, subObject.fieldClass, "subobject clazz");
		//
		assertEquals(2, subObject.fields.size(), "subobject: nb fields");
		assertEquals(0, subObject.subObjects.size(), "subobject: nb subObjects");
		assertEquals("id", subObject.fields.get(0).name, "subobject: field 0");
		assertEquals("name", subObject.fields.get(1).name, "subobject: field 1");
	}

	@Test
	void testWithSubObject_withAlias_KO() {
		GraphQLRequestPreparationException e;

		// Bad class for the ReponseDef of the subObject : different of the type of the given field name
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withSubObject("friends", "anAlias", ObjectResponseDef
						.newSubObjectResponseDefBuilder(Droid.class).withField("id").withField("name").build()));
		assertTrue(e.getMessage().contains("friends"));
		assertTrue(e.getMessage().contains("Droid"));

		// Non existant field
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withSubObject("mml", "anAlias", ObjectResponseDef
						.newSubObjectResponseDefBuilder(Character.class).withField("id").withField("name").build()));
		assertTrue(e.getMessage().contains("mml"));

		// subObject added, whereas it is a Field
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withSubObject("appearsIn", "anAlias", ObjectResponseDef
						.newSubObjectResponseDefBuilder(Character.class).withField("id").withField("name").build()));
		assertTrue(e.getMessage().contains("appearsIn"));

		// Bad identifiers
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withSubObject("not valid name", "validAlias", ObjectResponseDef
						.newSubObjectResponseDefBuilder(Character.class).withField("id").withField("name").build()));
		assertTrue(e.getMessage().contains("not valid name"));

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withSubObject("friends", "non valid alias", ObjectResponseDef
						.newSubObjectResponseDefBuilder(Character.class).withField("id").withField("name").build()));
		assertTrue(e.getMessage().contains("non valid alias"));
	}

}
