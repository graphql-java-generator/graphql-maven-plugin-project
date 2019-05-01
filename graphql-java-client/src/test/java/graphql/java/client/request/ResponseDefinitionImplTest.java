package graphql.java.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResponseDefinitionImplTest {

	final Marker GRAPHQL_TEST = MarkerManager.getMarker("junit test for ResponseDefinitionImplTest");
	ResponseDefinition responseDef;

	@BeforeEach
	void setUp() throws Exception {
		responseDef = new ResponseDefinition(GRAPHQL_TEST);
		assertEquals(0, responseDef.fields.size(), "list initialized, and no field before start");
	}

	@Test
	void testResponseDefinitionImpl() {
		assertEquals(GRAPHQL_TEST, responseDef.marker);
	}

	@Test
	void testAddResponseField_Ok() {
		// Preparation
		String fieldName = "aFieldName";

		// Go, go, go
		responseDef.addResponseField(fieldName);

		// Verification
		assertEquals(1, responseDef.fields.size(), "one field in the list");
		assertEquals(fieldName, responseDef.fields.get(0).name, "field name");
		assertNull(responseDef.fields.get(0).responseDef, "responseDef is null");
	}

	@Test
	void testAddResponseField_KO_InvalidIdentifier() {
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

		// Go, go, go
		responseDef.addResponseFieldWithAlias(name, alias);

		// Verification
		assertEquals(name, responseDef.fields.get(0).name, "name");
		assertEquals(alias, responseDef.fields.get(0).alias, "alias");
	}

	@Test
	void testAddResponseFieldWithAlias_KO() {
		// KO on the name
		assertThrows(IllegalArgumentException.class,
				() -> responseDef.addResponseFieldWithAlias("qdqd qdsq", "validAlias"));
		// KO on the alias
		assertThrows(IllegalArgumentException.class, () -> responseDef.addResponseFieldWithAlias("valid", "qdqd qdsq"));
	}

	@Test
	void testAddResponseEntity_OK() {
		// Preparation
		String fieldName = "aGraphqlEntityName";

		// Go, go, go
		responseDef.addResponseEntity(fieldName);

		// Verification
		assertEquals(1, responseDef.fields.size(), "one field in the list");
		assertEquals(fieldName, responseDef.fields.get(0).name, "field name");
		assertNull(responseDef.fields.get(0).alias, "alias is null");
		assertNotNull(responseDef.fields.get(0).responseDef, "responseDef is not null");
	}

	@Test
	void testAddResponseEntity_KO() {
		assertThrows(IllegalArgumentException.class, () -> responseDef.addResponseEntity("qsd "));
	}

	@Test
	void testAddResponseEntityWithAlias_OK() {
		// Preparation
		String fieldName = "aGraphqlEntityName";
		String alias = "theAlias";

		// Go, go, go
		responseDef.addResponseEntityWithAlias(fieldName, alias);

		// Verification
		assertEquals(1, responseDef.fields.size(), "one field in the list");
		assertEquals(fieldName, responseDef.fields.get(0).name, "field name");
		assertEquals(alias, responseDef.fields.get(0).alias, "alias");
		assertNotNull(responseDef.fields.get(0).responseDef, "responseDef is not null");
	}

	@Test
	void testAddResponseEntityWithAlias_KO() {
		assertThrows(IllegalArgumentException.class,
				() -> responseDef.addResponseEntityWithAlias("not valid name", "validAlias"));
		assertThrows(IllegalArgumentException.class,
				() -> responseDef.addResponseEntityWithAlias("validName", "non valid alias"));
	}

	@Test
	void testAppendResponseQuery_noSubEntity() {
		// Preparation
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
		assertEquals("{theAlias: aFieldName anotherFieldName aThirdFieldName}", sb.toString());
	}

	@Test
	void testAppendResponseQuery_withSubEntities() {
		// Preparation
		String fieldName1 = "aGraphqlEntityName";
		String alias1 = "theAlias";
		ResponseDefinition subResponseDef = responseDef.addResponseEntityWithAlias(fieldName1, alias1);
		String fieldName1_1 = "aSubGraphqlField1";
		subResponseDef.addResponseField(fieldName1_1);
		String fieldName1_2 = "aSubGraphqlField2";
		String alias1_2 = "aliasField2";
		subResponseDef.addResponseFieldWithAlias(fieldName1_2, alias1_2);
		//
		String fieldName2 = "anotherGraphqlEntityName";
		ResponseDefinition subResponseDef2 = responseDef.addResponseEntity(fieldName2);
		String fieldName2_1 = "aSubGraphqlField3";
		String alias2_2 = "aliasField3";
		subResponseDef2.addResponseFieldWithAlias(fieldName2_1, alias2_2);
		//
		String fieldName3 = "aThirdFieldName";
		responseDef.addResponseField(fieldName3);
		StringBuilder sb = new StringBuilder();

		// Go, go, go
		responseDef.appendResponseQuery(sb);

		// Verification
		assertEquals(3, responseDef.fields.size(), "Main entity");
		assertEquals(2, ((ResponseDefinition) subResponseDef).fields.size(), "First sub entity");
		assertEquals(1, ((ResponseDefinition) subResponseDef2).fields.size(), "Second sub entity");
		assertEquals(
				"{theAlias: aGraphqlEntityName{aSubGraphqlField1 aliasField2: aSubGraphqlField2} anotherGraphqlEntityName{aliasField3: aSubGraphqlField3} aThirdFieldName}",
				sb.toString());
	}

}
