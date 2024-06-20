package com.graphql_java_generator.plugin.generate_code;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.graphql_java_generator.plugin.conf.GenerateGraphQLSchemaConfiguration;
import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;

import graphql.mavenplugin_notscannedbyspring.AllGraphQLCases_Server_SpringConfiguration;
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

	@AfterEach
	void cleanUp() {
		if (this.ctx != null) {
			this.ctx.close();
		}
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_parseOneDocument_allGraphQLCases() throws IOException {
		// Preparation
		this.ctx = new AnnotationConfigApplicationContext(AllGraphQLCases_Server_SpringConfiguration_KO.class);
		GenerateCodeDocumentParser documentParser = this.ctx.getBean(GenerateCodeDocumentParser.class);

		// Go, go, go
		Exception e = assertThrows(Exception.class, () -> documentParser.parseGraphQLSchemas());
		assertTrue(e.getMessage()
				.contains("must provide an implementation for the Custom Scalar 'MyCustomScalarForADate'"));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_allGraphQLCases_relayConnTrue_defaultGraphqlsFolder() throws IOException {
		// Preparation
		this.ctx = new AnnotationConfigApplicationContext(AllGraphQLCases_Server_SpringConfiguration.class);
		GenerateCodeDocumentParser documentParser = this.ctx.getBean(GenerateCodeDocumentParser.class);
		GraphQLConfigurationTestHelper pluginConfiguration = this.ctx.getBean(GraphQLConfigurationTestHelper.class);

		// Let's update some configuration parameters AFTER the documents are loaded, to check the control tests, when
		// the parsing starts
		pluginConfiguration.addRelayConnections = true;
		pluginConfiguration.schemaFilePattern = GenerateGraphQLSchemaConfiguration.DEFAULT_TARGET_SCHEMA_FILE_NAME;

		// Go, go, go
		Exception e = assertThrows(IllegalArgumentException.class, () -> documentParser.parseGraphQLSchemas());
		assertTrue(e.getMessage().contains("addRelayConnections is set to true"));
	}

}
