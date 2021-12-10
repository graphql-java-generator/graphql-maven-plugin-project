package org.allGraphQLCases.server.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import graphql.parser.ParserOptions;

class GraphQLServerMainTest {

	static int EXPECTED_MAX_TOKENS = 12345;

	@Test
	void testParserOptions() throws IOException {
		// Preparation
		assertEquals(ParserOptions.MAX_QUERY_TOKENS, ParserOptions.getDefaultParserOptions().getMaxTokens());
		assertNotEquals(ParserOptions.MAX_QUERY_TOKENS, EXPECTED_MAX_TOKENS);

		// Go, go, go
		// The generated GraphQLServerMain constructor should change the maxTokens to the value defined in the
		// pom/build.gradle file
		new GraphQLServerMain();

		// Verification
		assertEquals(EXPECTED_MAX_TOKENS, ParserOptions.getDefaultParserOptions().getMaxTokens(),
				"the maxTokens defined in the pom or the gradle.build file must have been taken into account");
	}

}
