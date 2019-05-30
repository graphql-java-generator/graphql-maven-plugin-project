package graphql.java.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import graphql.java.client.domain.starwars.Character;
import graphql.java.client.domain.starwars.Droid;
import graphql.java.client.domain.starwars.Human;
import graphql.java.client.domain.starwars.QueryType;
import graphql.java.client.response.GraphQLRequestPreparationException;

class BuilderTest {

	Class<?> clazz = Human.class;
	Builder humanResponseDefBuilder;

	@BeforeEach
	void setUp() throws Exception {

		humanResponseDefBuilder = ObjectResponse.newQueryResponseDefBuilder(QueryType.class, "human");

		assertEquals(0, humanResponseDefBuilder.objectResponse.scalarFields.size(),
				"list scalarFields initialized, and no field before start");
		assertEquals(0, humanResponseDefBuilder.objectResponse.subObjects.size(),
				"list subObjects initialized, and no field before start");
	}

	@Test
	void testWithField_Ok() throws GraphQLRequestPreparationException {
		// Go, go, go
		humanResponseDefBuilder.withField("name");

		// Verification
		assertEquals(1, humanResponseDefBuilder.objectResponse.scalarFields.size(), "one field in the list");
		assertEquals("name", humanResponseDefBuilder.objectResponse.scalarFields.get(0).name, "field name");
	}

	@Test
	void testWithField_KO_FieldPresentTwoTimes() throws GraphQLRequestPreparationException {
		// Preparation
		GraphQLRequestPreparationException e;
		humanResponseDefBuilder.withField("name");

		// Go, go go
		e = assertThrows(GraphQLRequestPreparationException.class, () -> humanResponseDefBuilder.withField("name"));
		assertTrue(e.getMessage().contains("<name>"));

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withField("name", "validAlias"));
		assertTrue(e.getMessage().contains("<name>"));
	}

	@Test
	void testWithField_KO_InvalidIdentifier() throws GraphQLRequestPreparationException {
		GraphQLRequestPreparationException e;

		// Most important check: only attribute of the clazz class may be added.
		humanResponseDefBuilder = ObjectResponse.newQueryResponseDefBuilder(QueryType.class, "human");
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withField("fieldNotInThisClass"));
		assertTrue(e.getMessage().contains("fieldNotInThisClass"));

		// Field added, but it is a subObject
		humanResponseDefBuilder = ObjectResponse.newQueryResponseDefBuilder(QueryType.class, "human");
		e = assertThrows(GraphQLRequestPreparationException.class, () -> humanResponseDefBuilder.withField("friends"));
		assertTrue(e.getMessage().contains("friends"));

		// Various types of checks, for invalid identifiers
		humanResponseDefBuilder = ObjectResponse.newQueryResponseDefBuilder(QueryType.class, "human");
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withField("qdqd qdsq"));
		assertTrue(e.getMessage().contains("qdqd qdsq"));

		humanResponseDefBuilder = ObjectResponse.newQueryResponseDefBuilder(QueryType.class, "human");
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withField("qdqd.qdsq"));
		assertTrue(e.getMessage().contains("qdqd.qdsq"));

		humanResponseDefBuilder = ObjectResponse.newQueryResponseDefBuilder(QueryType.class, "human");
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withField("qdqdqdsq."));
		assertTrue(e.getMessage().contains("qdqdqdsq."));

		humanResponseDefBuilder = ObjectResponse.newQueryResponseDefBuilder(QueryType.class, "human");
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
		assertEquals("id", humanResponseDefBuilder.objectResponse.scalarFields.get(0).name, "name");
		assertEquals("idAlias", humanResponseDefBuilder.objectResponse.scalarFields.get(0).alias, "alias");
	}

	@Test
	void testWithFieldWithAlias_KO() throws GraphQLRequestPreparationException {
		// KO on the name
		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withField("notAnExistingAttribute", "validAlias"));
		assertTrue(e.getMessage().contains("notAnExistingAttribute"));

		// Field added, but it is a subObject
		humanResponseDefBuilder = ObjectResponse.newQueryResponseDefBuilder(QueryType.class, "human");
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withField("friends", "anAlias"));
		assertTrue(e.getMessage().contains("friends"));

		// KO on the alias
		humanResponseDefBuilder = ObjectResponse.newQueryResponseDefBuilder(QueryType.class, "human");
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withField("id", "qdqd qdsq"));
		assertTrue(e.getMessage().contains("qdqd qdsq"));
	}

	@Test
	void testWithSubObject_OK() throws GraphQLRequestPreparationException {
		// Go, go, go
		humanResponseDefBuilder.withSubObject("friends",
				ObjectResponse.newSubObjectBuilder(Character.class).withField("id").withField("name").build());

		// Verification
		assertEquals(1, humanResponseDefBuilder.objectResponse.subObjects.size(), "one object in the list");
		//
		ObjectResponse subObject = humanResponseDefBuilder.objectResponse.subObjects.get(0);
		assertEquals("friends", subObject.fieldName, "subobject fieldName");
		assertEquals(null, subObject.fieldAlias, "subobject fieldAlias");
		assertEquals(Character.class, subObject.fieldClass, "subobject clazz");
		//
		assertEquals(2, subObject.scalarFields.size(), "subobject: nb scalarFields");
		assertEquals(0, subObject.subObjects.size(), "subobject: nb subObjects");
		assertEquals("id", subObject.scalarFields.get(0).name, "subobject: field 0");
		assertEquals("name", subObject.scalarFields.get(1).name, "subobject: field 1");
	}

	@Test
	void testWithSubObject_KO_fieldPresentTwoTimes() throws GraphQLRequestPreparationException {
		// Preparation
		GraphQLRequestPreparationException e;
		humanResponseDefBuilder.withSubObject("friends",
				ObjectResponse.newSubObjectBuilder(Character.class).withField("id").withField("name").build());

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withSubObject("friends",
						ObjectResponse.newSubObjectBuilder(Character.class).withField("id").withField("name").build()));
		assertTrue(e.getMessage().contains("<friends>"));
	}

	@Test
	void testWithSubObject_KO() {
		GraphQLRequestPreparationException e;

		// Bad class for the ReponseDef of the subObject : different of the type of the given field name
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withSubObject("friends",
						ObjectResponse.newSubObjectBuilder(Droid.class).withField("id").withField("name").build()));
		assertTrue(e.getMessage().contains("friends"));
		assertTrue(e.getMessage().contains("Droid"));

		// Non existant field
		e = assertThrows(GraphQLRequestPreparationException.class, () -> humanResponseDefBuilder.withSubObject("mml",
				ObjectResponse.newSubObjectBuilder(Character.class).withField("id").withField("name").build()));
		assertTrue(e.getMessage().contains("mml"));

		// subObject added, whereas it is a Field
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withSubObject("appearsIn",
						ObjectResponse.newSubObjectBuilder(Character.class).withField("id").withField("name").build()));
		assertTrue(e.getMessage().contains("appearsIn"));
	}

	@Test
	void testWithSubObject_withAlias_OK() throws GraphQLRequestPreparationException {
		// Go, go, go
		humanResponseDefBuilder.withSubObject("friends", "aValidAlias",
				ObjectResponse.newSubObjectBuilder(Character.class).withField("id").withField("name").build());

		// Verification
		assertEquals(1, humanResponseDefBuilder.objectResponse.subObjects.size(), "one field in the list");
		//
		ObjectResponse subObject = humanResponseDefBuilder.objectResponse.subObjects.get(0);
		assertEquals("friends", subObject.fieldName, "subobject fieldName");
		assertEquals("aValidAlias", subObject.fieldAlias, "subobject fieldAlias");
		assertEquals(Character.class, subObject.fieldClass, "subobject clazz");
		//
		assertEquals(2, subObject.scalarFields.size(), "subobject: nb scalarFields");
		assertEquals(0, subObject.subObjects.size(), "subobject: nb subObjects");
		assertEquals("id", subObject.scalarFields.get(0).name, "subobject: field 0");
		assertEquals("name", subObject.scalarFields.get(1).name, "subobject: field 1");
	}

	@Test
	void testWithSubObject_withAlias_KO() {
		GraphQLRequestPreparationException e;

		// Bad class for the ReponseDef of the subObject : different of the type of the given field name
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withSubObject("friends", "anAlias",
						ObjectResponse.newSubObjectBuilder(Droid.class).withField("id").withField("name").build()));
		assertTrue(e.getMessage().contains("friends"));
		assertTrue(e.getMessage().contains("Droid"));

		// Non existant field
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withSubObject("mml", "anAlias",
						ObjectResponse.newSubObjectBuilder(Character.class).withField("id").withField("name").build()));
		assertTrue(e.getMessage().contains("mml"));

		// subObject added, whereas it is a Field
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withSubObject("appearsIn", "anAlias",
						ObjectResponse.newSubObjectBuilder(Character.class).withField("id").withField("name").build()));
		assertTrue(e.getMessage().contains("appearsIn"));

		// Bad identifiers
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withSubObject("not valid name", "validAlias",
						ObjectResponse.newSubObjectBuilder(Character.class).withField("id").withField("name").build()));
		assertTrue(e.getMessage().contains("not valid name"));

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withSubObject("friends", "non valid alias",
						ObjectResponse.newSubObjectBuilder(Character.class).withField("id").withField("name").build()));
		assertTrue(e.getMessage().contains("non valid alias"));
	}

	@Test
	public void test_withQueryResponseDef_emptyQuery() throws GraphQLRequestPreparationException {

		// Go, go, go
		humanResponseDefBuilder.withQueryResponseDef("");

		// Verification
		// Verification
		assertEquals(4, humanResponseDefBuilder.objectResponse.scalarFields.size(), "all scalar fields");
		//
		// field name check
		int i = 0;
		assertEquals("id", humanResponseDefBuilder.objectResponse.scalarFields.get(i++).name);
		assertEquals("name", humanResponseDefBuilder.objectResponse.scalarFields.get(i++).name);
		assertEquals("appearsIn", humanResponseDefBuilder.objectResponse.scalarFields.get(i++).name);
		assertEquals("homePlanet", humanResponseDefBuilder.objectResponse.scalarFields.get(i++).name);

		// No non scalar
		assertEquals(0, humanResponseDefBuilder.objectResponse.subObjects.size(), "no non scalar fields");
	}

	@Test
	public void test_withQueryResponseDef_nullQuery() throws GraphQLRequestPreparationException {

		// Go, go, go
		humanResponseDefBuilder.withQueryResponseDef(null);

		// Verification
		// Verification
		assertEquals(4, humanResponseDefBuilder.objectResponse.scalarFields.size(), "all scalar fields");
		//
		// field name check
		int i = 0;
		assertEquals("id", humanResponseDefBuilder.objectResponse.scalarFields.get(i++).name);
		assertEquals("name", humanResponseDefBuilder.objectResponse.scalarFields.get(i++).name);
		assertEquals("appearsIn", humanResponseDefBuilder.objectResponse.scalarFields.get(i++).name);
		assertEquals("homePlanet", humanResponseDefBuilder.objectResponse.scalarFields.get(i++).name);

		// No non scalar
		assertEquals(0, humanResponseDefBuilder.objectResponse.subObjects.size(), "no non scalar fields");
	}

	@Test
	public void test_withQueryResponseDef() throws GraphQLRequestPreparationException {

		// Go, go, go
		humanResponseDefBuilder.withQueryResponseDef(
				"{id friends{ id      nameAlias:name                       amis     :    friends{id name} appearsIn} name  }  ");

		// Verification
		ObjectResponse respDef = humanResponseDefBuilder.build();
		assertEquals("human", respDef.fieldName, "field name");
		//
		assertEquals(2, respDef.scalarFields.size(), "nb scalar fields");
		assertEquals("id", respDef.scalarFields.get(0).name, "name scalarFields 0");
		assertNull(respDef.scalarFields.get(0).alias, "alias scalarFields 0");
		assertEquals("name", respDef.scalarFields.get(1).name, "name scalarFields 1");
		assertNull(respDef.scalarFields.get(1).alias, "alias scalarFields 1");
		//
		assertEquals(1, respDef.subObjects.size(), "nb subobjects");

		ObjectResponse friends1 = respDef.subObjects.get(0);
		assertEquals("friends", friends1.fieldName);
		assertEquals(null, friends1.fieldAlias);
		//
		assertEquals(3, friends1.scalarFields.size(), "friends1: nb scalar fields");
		assertEquals("id", friends1.scalarFields.get(0).name, "friends1: name scalarFields 0");
		assertNull(friends1.scalarFields.get(0).alias, "friends1: alias scalarFields 0");
		assertEquals("name", friends1.scalarFields.get(1).name, "friends1: name scalarFields 1");
		assertEquals("nameAlias", friends1.scalarFields.get(1).alias, "friends1: alias scalarFields 1");
		assertEquals("appearsIn", friends1.scalarFields.get(2).name, "friends1: name scalarFields 2");
		assertNull(friends1.scalarFields.get(2).alias, "friends1: alias scalarFields 2");
		//
		assertEquals(1, friends1.subObjects.size(), "friends1: nb subobjects");

		ObjectResponse friends2 = friends1.subObjects.get(0);
		assertEquals("friends", friends2.fieldName);
		assertEquals("amis", friends2.fieldAlias);
		//
		assertEquals(2, friends2.scalarFields.size());
		assertEquals("id", friends2.scalarFields.get(0).name);
		assertNull(friends2.scalarFields.get(0).alias);
		assertEquals("name", friends2.scalarFields.get(1).name);
		assertNull(friends2.scalarFields.get(1).alias);
		//
		assertEquals(0, friends2.subObjects.size());
	}

	@Test
	public void test_withQueryResponseDef_Forum() throws GraphQLRequestPreparationException {
		// Go, go, go
		String queryResponseDef = "{id name publiclyAvailable topics{id date author{id name email type} nbPosts posts{date author{name email type}}}}";
		new graphql.java.client.domain.forum.QueryType("http://localhost:8180/graphql").getBoardsResponseBuilder()
				.withQueryResponseDef(queryResponseDef).build();
	}

	@Test
	public void test_withQueryResponseDef_KO() throws GraphQLRequestPreparationException {
		GraphQLRequestPreparationException e;

		// Errors with { or }
		humanResponseDefBuilder = ObjectResponse.newQueryResponseDefBuilder(QueryType.class, "human");
		assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withQueryResponseDef(
						"id friends{ id      nameAlias:name amis  :     friends{id name} appearsIn} name    "),
				"missing a '{'");

		humanResponseDefBuilder = ObjectResponse.newQueryResponseDefBuilder(QueryType.class, "human");
		assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withQueryResponseDef(
						"{id friends{ id      nameAlias:name amis  :     friends{id name} appearsIn} name    "),
				"missing a '}'");

		humanResponseDefBuilder = ObjectResponse.newQueryResponseDefBuilder(QueryType.class, "human");
		assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withQueryResponseDef(
						"{id friends{ id      nameAlias:name amis  :     friends{id name}{id} appearsIn} name    "),
				"a { without leading field name");

		humanResponseDefBuilder = ObjectResponse.newQueryResponseDefBuilder(QueryType.class, "human");
		assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withQueryResponseDef("{id friends name    "),
				"No field definition for friends");

		humanResponseDefBuilder = ObjectResponse.newQueryResponseDefBuilder(QueryType.class, "human");
		assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withQueryResponseDef("{id friends{} name    "),
				"Empty field definition for friends");

		// Field present two times
		humanResponseDefBuilder = ObjectResponse.newQueryResponseDefBuilder(QueryType.class, "human");
		e = assertThrows(GraphQLRequestPreparationException.class, () -> humanResponseDefBuilder.withQueryResponseDef(
				"{id friends{ id      nameAlias:name amis  :     friends{id name} appearsIn} name  id } "));
		assertTrue(e.getMessage().contains("<id>"), e.getMessage());

		// Wrong field name
		humanResponseDefBuilder = ObjectResponse.newQueryResponseDefBuilder(QueryType.class, "human");
		e = assertThrows(GraphQLRequestPreparationException.class, () -> humanResponseDefBuilder.withQueryResponseDef(
				"{id friends{ id      nameAlias:name amis  :     friends{id notAFieldName} appearsIn} name   } "));
		assertTrue(e.getMessage().contains("<notAFieldName>"), e.getMessage());

		// Bad alias name
		humanResponseDefBuilder = ObjectResponse.newQueryResponseDefBuilder(QueryType.class, "human");
		e = assertThrows(GraphQLRequestPreparationException.class, () -> humanResponseDefBuilder.withQueryResponseDef(
				"{id friends{ id      name*Alias:name amis  :     friends{id notAFieldName} appearsIn} name   } "));
		assertTrue(e.getMessage().contains("<name*Alias>"), e.getMessage());

		// We're not ready yet for field parameters
		humanResponseDefBuilder = ObjectResponse.newQueryResponseDefBuilder(QueryType.class, "human");
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> humanResponseDefBuilder.withQueryResponseDef(
						"{id friends{ id(since)      nameAlias:name amis  :     friends{id name} appearsIn} name    "),
				"missing a '}'");
		assertTrue(e.getMessage().contains("("), e.getMessage());
	}

	@Test
	void testBuild_NoFields() throws GraphQLRequestPreparationException {
		// Go, go, go
		ObjectResponse resp = humanResponseDefBuilder.build();

		// Verification
		assertEquals(4, resp.scalarFields.size(), "all scalar fields");
		//
		// field name check
		int i = 0;
		assertEquals("id", resp.scalarFields.get(i++).name);
		assertEquals("name", resp.scalarFields.get(i++).name);
		assertEquals("appearsIn", resp.scalarFields.get(i++).name);
		assertEquals("homePlanet", resp.scalarFields.get(i++).name);

		// No non scalar
		assertEquals(0, resp.subObjects.size(), "no non scalar fields");
	}
}
