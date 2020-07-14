package com.graphql_java_generator.mavenplugin;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

class GenerateRelaySchemaMojoTest {

	@Test
	@Disabled // this test hangs, because of a missing method... :(
	void testExecute() {
		GenerateRelaySchemaMojo mojo = new GenerateRelaySchemaMojo();
		GenerateRelaySchemaSpringConfiguration.mojo = mojo;

		// Let's just check that the Spring context is valid.
		AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(
				GenerateRelaySchemaSpringConfiguration.class);
		ctx.close();
	}

}
