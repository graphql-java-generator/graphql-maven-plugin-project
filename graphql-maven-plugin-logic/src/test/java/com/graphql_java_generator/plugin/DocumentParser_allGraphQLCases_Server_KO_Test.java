package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import graphql.language.Document;
import graphql.mavenplugin_notscannedbyspring.AllGraphQLCases_Server_SpringConfiguration_KO;
import graphql.schema.GraphQLScalarType;

/**
 * Test of the allGraphQLCases without defining the necessary {@link GraphQLScalarType}
 * 
 * @author EtienneSF
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AllGraphQLCases_Server_SpringConfiguration_KO.class })
class DocumentParser_allGraphQLCases_Server_KO_Test {

	@Resource
	private DocumentParser documentParser;

	@Resource
	private PluginConfiguration pluginConfiguration;

	@Resource
	List<Document> documents;

	@BeforeEach
	void setUp() throws Exception {
		//
	}

	@Test
	@DirtiesContext
	void test_parseOneDocument_allGraphQLCases() {
		// Go, go, go
		Exception e = assertThrows(Exception.class, () -> documentParser.parseDocuments());
		assertTrue(e.getMessage().contains("must provide an implementation for the Custom Scalar 'Date'"));
	}
}
