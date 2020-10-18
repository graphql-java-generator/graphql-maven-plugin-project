package com.graphql_java_generator.mavenplugin;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Disabled // The default values doesn't seem to initialize the Mojo's parameters in these tests :(
@Execution(ExecutionMode.CONCURRENT)
class GraphQLMojoTest extends AbstractMojoTestCase {

	@BeforeEach
	void setup() throws Exception {
		super.setUp();
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testExecute_generateClientCode() throws Exception {
		File testPom = new File(getBasedir(), "src/test/resources/pom-allGraphQLCases-server.xml");
		GenerateClientCodeMojo mojo = (GenerateClientCodeMojo) lookupMojo("generateClientCode", testPom);
		assertNotNull(mojo);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testExecute_generateGraphQLSchema() throws Exception {
		File testPom = new File(getBasedir(), "src/test/resources/pom-allGraphQLCases-server.xml");
		GenerateGraphQLSchemaMojo mojo = (GenerateGraphQLSchemaMojo) lookupMojo("generateGraphQLSchema", testPom);
		assertNotNull(mojo);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testExecute_generateServerCode() throws Exception {
		File testPom = new File(getBasedir(), "src/test/resources/pom-allGraphQLCases-server.xml");
		GenerateServerCodeMojo mojo = (GenerateServerCodeMojo) lookupMojo("generateServerCode", testPom);
		assertNotNull(mojo);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testExecute_graphql() throws Exception {
		File testPom = new File(getBasedir(), "src/test/resources/pom-allGraphQLCases-server.xml");
		GraphQLMojo mojo = (GraphQLMojo) lookupMojo("graphql", testPom);
		assertNotNull(mojo);
	}

}
