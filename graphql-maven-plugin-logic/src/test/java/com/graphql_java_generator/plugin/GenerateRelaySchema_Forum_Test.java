package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.graphql_java_generator.plugin.test.helper.GenerateRelaySchemaConfigurationTestHelper;
import com.graphql_java_generator.plugin.test.helper.MavenTestHelper;

import generate_relay_schema.mavenplugin_notscannedbyspring.AbstractSpringConfiguration;
import generate_relay_schema.mavenplugin_notscannedbyspring.Forum_Client_SpringConfiguration;
import graphql.language.Definition;
import graphql.language.Document;
import graphql.language.ObjectTypeDefinition;
import graphql.parser.Parser;

/**
 * 
 * @author etienne-sf
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { Forum_Client_SpringConfiguration.class })
class GenerateRelaySchema_Forum_Test {

	@Autowired
	GraphQLDocumentParser documentParser;

	@Autowired
	GenerateRelaySchema generateRelaySchema;

	@Autowired
	GenerateRelaySchemaConfigurationTestHelper configuration;

	MavenTestHelper mavenTestHelper = new MavenTestHelper();
	Document generatedDocument;

	/**
	 * This is not a unit test. It's more a full integration test, that checks that the GraphQL generated from the forum
	 * schema is complete.<BR/>
	 * As we won't generate the schema for each check, this method is the unique test. And it throws all other checks on
	 * the generated schema.
	 * 
	 * @throws IOException
	 */
	@Test
	@DirtiesContext
	void testGenerateRelaySchema() throws IOException {

		// Go, go, go
		generateRelaySchema.generateRelaySchema();

		// Verification
		File generatedSchemaFile = new File(getTargetFolder(), getSchemaFileName());
		StringWriter writer = new StringWriter();
		InputStream inputStream = new FileInputStream(generatedSchemaFile);
		IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
		generatedDocument = new Parser().parseDocument(writer.toString());

		for (Document doc : documentParser.documents) {
			for (Definition<?> node : doc.getDefinitions()) {
				if (node instanceof ObjectTypeDefinition) {
					checkObject((ObjectTypeDefinition) node);
				} else {
					fail("non managed node type: " + node.getClass().getSimpleName());
				}
			}
		}

		fail("Not finished");
	}

	/**
	 * Checks that the given object exists in the generated schema
	 * 
	 * @param objectDef
	 */
	private void checkObject(ObjectTypeDefinition objectDef) {
		// Each object in the source schema must be present in the generated schema
		for (ObjectTypeDefinition node : generatedDocument.getDefinitionsOfType(ObjectTypeDefinition.class)) {
			if (node.getName().contentEquals(objectDef.getName())) {
				fail("field not tested");
			}
		}

		fail("The object definition '" + objectDef.getName() + "' doesn't exist in the generated schema");
	}

	/** The folder where the generated GraphQL schema will be written */
	File getTargetFolder() {
		return new File(mavenTestHelper.getModulePathFile(), AbstractSpringConfiguration.ROOT_UNIT_TEST_FOLDER
				+ Forum_Client_SpringConfiguration.class.getSimpleName());
	}

	/** The file name for the generated GraphQL schema */
	String getSchemaFileName() {
		return "forum.graphqls";
	}
}
