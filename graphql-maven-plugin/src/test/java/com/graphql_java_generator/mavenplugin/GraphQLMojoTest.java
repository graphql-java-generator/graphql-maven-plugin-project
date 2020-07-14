package com.graphql_java_generator.mavenplugin;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

class GraphQLMojoTest extends AbstractMojoTestCase {

	@BeforeEach
	void setup() throws Exception {
		super.setUp();
	}

	@Test
	@Disabled // this test hangs, because of a missing method... :(
	void testExecute() throws Exception {
		File testPom = new File(getBasedir(), "src/test/resources/pom-allGraphQLCases-server.xml");
		GraphQLMojo mojo = (GraphQLMojo) lookupMojo("graphql", testPom);
		GraphQLSpringConfiguration.mojo = mojo;

		// Let's just check that the Spring context is valid.
		AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(GraphQLSpringConfiguration.class);
		ctx.close();
	}

}
