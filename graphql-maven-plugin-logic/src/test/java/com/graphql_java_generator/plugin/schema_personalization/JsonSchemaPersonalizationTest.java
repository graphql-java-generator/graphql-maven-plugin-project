package com.graphql_java_generator.plugin.schema_personalization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;
import com.graphql_java_generator.plugin.test.helper.MavenTestHelper;

@Execution(ExecutionMode.CONCURRENT)
class JsonSchemaPersonalizationTest {

	GraphQLConfigurationTestHelper pluginConfigurationTestHelper;

	GenerateCodeJsonSchemaPersonalization jsonSchemaPersonalization;
	File userJsonFile;

	@BeforeEach
	void setUp() throws Exception {
		pluginConfigurationTestHelper = new GraphQLConfigurationTestHelper(this);
		pluginConfigurationTestHelper.schemaPersonalizationFile = new File(new MavenTestHelper().getModulePathFile(),
				"src/test/resources/schema_personalization/complete.json");

		jsonSchemaPersonalization = new GenerateCodeJsonSchemaPersonalization();
		jsonSchemaPersonalization.pluginConfiguration = pluginConfigurationTestHelper;
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testGetSchemaPersonalization() throws IOException, URISyntaxException {
		// Preparation
		assertNull(jsonSchemaPersonalization.schemaPersonalization, "Before");

		// Go, go, go
		SchemaPersonalization verif = jsonSchemaPersonalization.getSchemaPersonalization();

		// Verification
		assertNotNull(jsonSchemaPersonalization.schemaPersonalization, "After");
		assertEquals(verif, jsonSchemaPersonalization.schemaPersonalization, "verif");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testLoadGrahQLSchemaPersonalization() throws IOException, URISyntaxException {
		// Preparation
		assertNull(jsonSchemaPersonalization.schemaPersonalization, "Before");

		// Go, go, go
		SchemaPersonalization verif = jsonSchemaPersonalization.loadGraphQLSchemaPersonalization();

		// Verification
		assertEquals(2, verif.getEntityPersonalizations().size(), "nb subObjects");
		int i = -1;
		int j = -1;

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Let's check the first entity
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		checkEntityPersonalization(verif.getEntityPersonalizations().get(++i), i, "entity1", "addAnnotation1",
				"replaceAnnotation1");

		//
		// There are two new fields
		//
		assertEquals(2, verif.getEntityPersonalizations().get(i).getNewFields().size(), "nb new fields (" + i + ")");
		j = -1;
		// checkField(name, type, id, list, mandatory, addAnnotation, replaceAnnotation)
		checkField(verif.getEntityPersonalizations().get(i).getNewFields().get(++j), i, j, "field1.1", "type1.1", true,
				true, true, "addAnnotation1.1", "replaceAnnotation1.1");
		checkField(verif.getEntityPersonalizations().get(i).getNewFields().get(++j), i, j, "field1.2", "type1.2", false,
				false, false, "addAnnotation1.2", "replaceAnnotation1.2");
		//
		// There are two changed fields
		//
		assertEquals(2, verif.getEntityPersonalizations().get(i).getFieldPersonalizations().size(),
				"nb personalized fields (" + i + ")");
		j = -1;
		// checkField(name, type, id, list, mandatory, addAnnotation, replaceAnnotation)
		checkField(verif.getEntityPersonalizations().get(i).getFieldPersonalizations().get(++j), i, j, "field1.3",
				"type1.3", true, true, true, "addAnnotation1.3", "replaceAnnotation1.3");
		checkField(verif.getEntityPersonalizations().get(i).getFieldPersonalizations().get(++j), i, j,
				"Checks that everything was set to null (important to properly identify that there should be no update for these values",
				null, null, null, null, null, null);

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Let's check the second entity
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		checkEntityPersonalization(verif.getEntityPersonalizations().get(++i), i, "entity2", "addAnnotation2",
				"replaceAnnotation2");
		//
		// There are two new fields
		//
		assertEquals(2, verif.getEntityPersonalizations().get(i).getNewFields().size(), "nb new fields (" + i + ")");
		j = -1;
		// checkField(name, type, id, list, mandatory, addAnnotation, replaceAnnotation)
		checkField(verif.getEntityPersonalizations().get(i).getNewFields().get(++j), i, j, "field2.1", "type2.1", true,
				true, true, "addAnnotation2.1", "replaceAnnotation2.1");
		checkField(verif.getEntityPersonalizations().get(i).getNewFields().get(++j), i, j, "field2.2", "type2.2", false,
				false, false, "addAnnotation2.2", "replaceAnnotation2.2");
		//
		// There are two changed fields
		//
		assertEquals(2, verif.getEntityPersonalizations().get(i).getFieldPersonalizations().size(),
				"nb personalized fields (" + i + ")");
		j = -1;
		// checkField(name, type, id, list, mandatory, addAnnotation, replaceAnnotation)
		checkField(verif.getEntityPersonalizations().get(i).getFieldPersonalizations().get(++j), i, j, "field2.3",
				"type2.3", true, true, true, "addAnnotation2.3", "replaceAnnotation2.3");
		checkField(verif.getEntityPersonalizations().get(i).getFieldPersonalizations().get(++j), i, j, "field2.4",
				"type2.4", false, false, false, "addAnnotation2.4", "replaceAnnotation2.4");
	}

	private void checkEntityPersonalization(EntityPersonalization entityPers, int num, String name,
			String addAnnotation, String replaceAnnotation) {
		assertEquals(name, entityPers.getName(), "entity" + num + ": name");
		assertEquals(addAnnotation, entityPers.getAddAnnotation(), "entity" + num + ": addAnnotation");
		assertEquals(replaceAnnotation, entityPers.getReplaceAnnotation(), "entity" + num + ": replaceAnnotation");
	}

	private void checkField(Field field, int entityNum, int fieldNum, String name, String type, Boolean id,
			Boolean list, Boolean mandatory, String addAnnotation, String replaceAnnotation) {
		assertEquals(name, field.getName(), "entity" + entityNum + ", field " + fieldNum + ": name");
		assertEquals(type, field.getType(), "entity" + entityNum + ", field " + fieldNum + ": type");
		assertEquals(id, field.getId(), "entity" + entityNum + ", field " + fieldNum + ": id");
		assertEquals(list, field.getList(), "entity" + entityNum + ", field " + fieldNum + ": list");
		assertEquals(mandatory, field.getMandatory(), "entity" + entityNum + ", field " + fieldNum + ": mandatory");
		assertEquals(addAnnotation, field.getAddAnnotation(),
				"entity" + entityNum + ", field " + fieldNum + ": addAnnotation");
		assertEquals(replaceAnnotation, field.getReplaceAnnotation(),
				"entity" + entityNum + ", field " + fieldNum + ": replaceAnnotation");
	}

}
