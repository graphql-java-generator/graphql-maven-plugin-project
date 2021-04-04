package com.graphql_java_generator.plugin.generate_code;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.graphql_java_generator.plugin.Documents;
import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;

import graphql.mavenplugin_notscannedbyspring.AllGraphQLCases_Client_SpringConfiguration;

/**
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
class DocumentParser_allGraphQLCases_Client_Test {

	AbstractApplicationContext ctx = null;
	GenerateCodeDocumentParser generateCodeDocumentParser;
	GraphQLConfiguration pluginConfiguration;
	Documents documents;

	@BeforeEach
	void loadApplicationContext() throws IOException {
		ctx = new AnnotationConfigApplicationContext(AllGraphQLCases_Client_SpringConfiguration.class);
		generateCodeDocumentParser = ctx.getBean(GenerateCodeDocumentParser.class);
		pluginConfiguration = ctx.getBean(GraphQLConfiguration.class);
		documents = ctx.getBean(Documents.class);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_parseOneDocument_allGraphQLCases() throws IOException {
		// Go, go, go
		generateCodeDocumentParser.parseDocuments();

		// Verification

		// Check of the CustomJacksonDeserializers
		// Let's check that there are two CustomJacksonDeserializers for Float, with depth of 1 and 2
		boolean foundDepth1 = false;
		boolean foundDepth2 = false;
		for (CustomDeserializer cd : generateCodeDocumentParser.getCustomDeserializers()) {
			if (cd.getGraphQLTypeName().equals("Float")) {
				if (cd.getListDepth() == 1) {
					foundDepth1 = true;
				} else if (cd.getListDepth() == 2) {
					foundDepth2 = true;
				} else {
					fail("Unexpected depth for Float type: " + cd.getListDepth());
				}
			}
		} // for

		assertTrue(foundDepth1, "CustomDeserializer of depth 1 found for Float");
		assertTrue(foundDepth2, "CustomDeserializer of depth 2 found for Float");
	}
}
