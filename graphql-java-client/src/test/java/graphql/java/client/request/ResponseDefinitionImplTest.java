package graphql.java.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import graphql.java.client.QueryExecutor;
import graphql.java.client.request.ResponseDef.ResponseDefBuilder;

class ResponseDefinitionImplTest {

	String objectName = "AnObjectName";

	@BeforeEach
	void setUp() throws Exception {

	}

	@Test
	void testResponseDefinitionImpl() {
		ResponseDef responseDef = new ResponseDef(objectName);
		assertEquals(0, responseDef.fields.size(), "list fields initialized, and no field before start");
		assertEquals(0, responseDef.subObjects.size(), "list subObjects initialized, and no field before start");
		assertEquals(QueryExecutor.GRAPHQL_MARKER, responseDef.marker, "Marker");
		assertEquals(objectName, responseDef.graphqlObjectName, "Name");
		assertNull(responseDef.fieldAlias, "alias");
	}

	@Test
	void testAddResponseField_Ok() {
		// Preparation
		String fieldName = "aFieldName";
		ResponseDef responseDef = new ResponseDef(objectName);
		assertEquals(0, responseDef.fields.size(), "list fields initialized, and no field before start");
		assertEquals(0, responseDef.subObjects.size(), "list subObjects initialized, and no field before start");

		// Go, go, go
		responseDef.addResponseField(fieldName);

		// Verification
		assertEquals(1, responseDef.fields.size(), "one field in the list");
		assertEquals(fieldName, responseDef.fields.get(0).name, "field name");
	}

	@Test
	void testAddResponseField_KO_InvalidIdentifier() {
		ResponseDef responseDef = new ResponseDef(objectName);
		assertEquals(0, responseDef.fields.size(), "list fields initialized, and no field before start");
		assertEquals(0, responseDef.subObjects.size(), "list subObjects initialized, and no field before start");

		// Various types of checks
		assertThrows(IllegalArgumentException.class, () -> responseDef.addResponseField("qdqd qdsq"));
		assertThrows(IllegalArgumentException.class, () -> responseDef.addResponseField("qdqd.qdsq"));
		assertThrows(IllegalArgumentException.class, () -> responseDef.addResponseField("qdqdqdsq."));
		assertThrows(IllegalArgumentException.class, () -> responseDef.addResponseField(".qdqdqdsq"));
		assertThrows(IllegalArgumentException.class, () -> responseDef.addResponseField("qdqdqdsq*"));
	}

	@Test
	void testAddResponseFieldWithAlias_OK() {
		// Preparation
		String name = "aValidName";
		String alias = "anAlias666";
		ResponseDef responseDef = new ResponseDef(objectName);
		assertEquals(0, responseDef.fields.size(), "list fields initialized, and no field before start");
		assertEquals(0, responseDef.subObjects.size(), "list subObjects initialized, and no field before start");

		// Go, go, go
		responseDef.addResponseFieldWithAlias(name, alias);

		// Verification
		assertEquals(name, responseDef.fields.get(0).name, "name");
		assertEquals(alias, responseDef.fields.get(0).alias, "alias");
	}

	@Test
	void testAddResponseFieldWithAlias_KO() {
		ResponseDef responseDef = new ResponseDef(objectName);
		assertEquals(0, responseDef.fields.size(), "list fields initialized, and no field before start");
		assertEquals(0, responseDef.subObjects.size(), "list subObjects initialized, and no field before start");
		// KO on the name
		assertThrows(IllegalArgumentException.class,
				() -> responseDef.addResponseFieldWithAlias("qdqd qdsq", "validAlias"));
		// KO on the alias
		assertThrows(IllegalArgumentException.class, () -> responseDef.addResponseFieldWithAlias("valid", "qdqd qdsq"));
	}

	@Test
	void testAddResponseDef_OK() {
		// Preparation
		String fieldName = "aGraphqlResponseDefName";
		String graphqlObjectName = "AGraphQLObjectName";
		ResponseDef responseDef = new ResponseDef(objectName);
		assertEquals(0, responseDef.fields.size(), "list fields initialized, and no field before start");
		assertEquals(0, responseDef.subObjects.size(), "list subObjects initialized, and no field before start");

		// Go, go, go
		responseDef.addSubObjectResponseDef(graphqlObjectName, fieldName);

		// Verification
		assertEquals(1, responseDef.subObjects.size(), "one object in the list");
		assertEquals(graphqlObjectName, responseDef.subObjects.get(0).graphqlObjectName, "field graphqlObjectName");
		assertEquals(fieldName, responseDef.subObjects.get(0).getFieldName(), "field name");
		assertNull(responseDef.subObjects.get(0).fieldAlias, "alias is null");
	}

	@Test
	void testAddResponseResponseDef_KO() {
		ResponseDef responseDef = new ResponseDef(objectName);
		assertEquals(0, responseDef.fields.size(), "list fields initialized, and no field before start");
		assertEquals(0, responseDef.subObjects.size(), "list subObjects initialized, and no field before start");

		assertThrows(IllegalArgumentException.class, () -> responseDef.addSubObjectResponseDef("mml", "qsd "));
	}

	@Test
	void testAddResponseResponseDefWithAlias_OK() {
		// Preparation
		String graphqlObjectName = "AName";
		String fieldName = "aGraphqlResponseDefName";
		String alias = "theAlias";
		ResponseDef responseDef = new ResponseDef(objectName);
		assertEquals(0, responseDef.fields.size(), "list fields initialized, and no field before start");
		assertEquals(0, responseDef.subObjects.size(), "list subObjects initialized, and no field before start");

		// Go, go, go
		responseDef.addSubObjectResponseDefWithAlias(graphqlObjectName, fieldName, alias);

		// Verification
		assertEquals(1, responseDef.subObjects.size(), "one field in the list");
		assertEquals(graphqlObjectName, responseDef.subObjects.get(0).graphqlObjectName, "field graphqlObjectName");
		assertEquals(fieldName, responseDef.subObjects.get(0).getFieldName(), "field name");
		assertEquals(alias, responseDef.subObjects.get(0).fieldAlias, "alias");
	}

	@Test
	void testAddResponseResponseDefWithAlias_KO() {
		ResponseDef responseDef = new ResponseDef(objectName);
		assertEquals(0, responseDef.fields.size(), "list fields initialized, and no field before start");
		assertEquals(0, responseDef.subObjects.size(), "list subObjects initialized, and no field before start");

		String graphqlObjectName = "AName";
		assertThrows(IllegalArgumentException.class,
				() -> responseDef.addSubObjectResponseDefWithAlias(graphqlObjectName, "not valid name", "validAlias"));
		assertThrows(IllegalArgumentException.class,
				() -> responseDef.addSubObjectResponseDefWithAlias(graphqlObjectName, "validName", "non valid alias"));
	}

	@Test
	void testAppendResponseQuery_noSubResponseDef() {
		// Preparation
		ResponseDef responseDef = new ResponseDef(objectName);
		assertEquals(0, responseDef.fields.size(), "list fields initialized, and no field before start");
		assertEquals(0, responseDef.subObjects.size(), "list subObjects initialized, and no field before start");

		String fieldName1 = "aFieldName";
		String alias1 = "theAlias";
		responseDef.addResponseFieldWithAlias(fieldName1, alias1);
		String fieldName2 = "anotherFieldName";
		responseDef.addResponseField(fieldName2);
		String fieldName3 = "aThirdFieldName";
		responseDef.addResponseField(fieldName3);
		StringBuilder sb = new StringBuilder();

		// Go, go, go
		responseDef.appendResponseQuery(sb);

		// Verification
		assertEquals("{ theAlias: aFieldName anotherFieldName aThirdFieldName}", sb.toString());
	}

	@Test
	void testAppendResponseQuery_withSubObjects() {
		// Preparation

		ResponseDef subFriendsResponseDef = ResponseDef.newResponseDeBuilder("Character").withField("id")
				.withField("name").withField("appearsIn").build();

		ResponseDef friendsResponseDef = ResponseDef.newResponseDeBuilder("Character").withField("id")
				.withField("name", "aliasForName").withSubObject("friends", subFriendsResponseDef).build();

		ResponseDefBuilder responseDefBuilder = ResponseDef.newResponseDeBuilder("Human");
		responseDefBuilder.withField("id", "aliasForId");
		responseDefBuilder.withSubObject("friends", "aliasForFriends", friendsResponseDef);
		responseDefBuilder.withField("name");
		responseDefBuilder.withField("homePlanet");
		ResponseDef responseDef = responseDefBuilder.build();

		StringBuilder sb = new StringBuilder();

		// Go, go, go
		responseDef.appendResponseQuery(sb);

		// Verification
		assertEquals(3, responseDef.fields.size(), "Human: fields");
		assertEquals(1, responseDef.subObjects.size(), "Human: objects");

		// Friends : first sublevel
		assertEquals(2, responseDef.subObjects.get(0).fields.size(), "friends (first level): fields");
		assertEquals(1, responseDef.subObjects.get(0).subObjects.size(), "friends (first level): objects");

		// Friends : second sublevel
		assertEquals(3, responseDef.subObjects.get(0).subObjects.get(0).fields.size(),
				"friends (second level): fields");
		assertEquals(0, responseDef.subObjects.get(0).subObjects.get(0).subObjects.size(),
				"friends (second level): objects");

		assertEquals(
				"{ aliasForId: id name homePlanet aliasForFriends: friends{ id aliasForName: name friends{ id name appearsIn}}}",
				sb.toString());
	}

}
