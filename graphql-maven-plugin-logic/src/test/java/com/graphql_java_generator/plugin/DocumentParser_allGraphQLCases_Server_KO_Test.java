package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;

import graphql.language.Document;
import graphql.mavenplugin_notscannedbyspring.AllGraphQLCases_Server_SpringConfiguration_KO;
import graphql.schema.GraphQLScalarType;

/**
 * Test of the allGraphQLCases without defining the necessary {@link GraphQLScalarType}
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
class DocumentParser_allGraphQLCases_Server_KO_Test {

	AbstractApplicationContext ctx = null;
	GraphQLDocumentParser documentParser;
	GraphQLConfiguration pluginConfiguration;
	List<Document> documents;

	@BeforeEach
	void loadApplicationContext() throws IOException {
		ctx = new AnnotationConfigApplicationContext(AllGraphQLCases_Server_SpringConfiguration_KO.class);
		documentParser = ctx.getBean(GraphQLDocumentParser.class);
		pluginConfiguration = ctx.getBean(GraphQLConfiguration.class);

		documents = documentParser.documents.getDocuments();
	}

	@AfterEach
	void cleanUp() {
		if (ctx != null) {
			ctx.close();
		}
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_parseOneDocument_allGraphQLCases() {
		// Go, go, go
		Exception e = assertThrows(Exception.class, () -> documentParser.parseDocuments());
		assertTrue(e.getMessage().contains("must provide an implementation for the Custom Scalar 'Date'"));
	}
}
