package com.graphql_java_generator.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class QueryTokenizerTest {

	@Test
	void testQueryTokenizer() {

		assertThrows(NullPointerException.class, () -> new QueryTokenizer(null));

		// Check of all known delimiters
		assertEquals(Arrays.asList("{", "}", ",", ":", "(", ")", "@", "\"", " "),
				new QueryTokenizer("{},:()@\" ").tokens);
		// Check with a mix of words, empty delimiters and meaningful delimiters
		assertEquals(Arrays.asList("(", "This", " ", "is", "\r", ":", "\n", "a", " ", "non", " ", "valid", " ", "query",
				"\t", ")"), new QueryTokenizer("(This is\r:\na non valid query\t)").tokens);
	}

	@Test
	void testNextToken_includesEmptyToken() {
		// Preparation
		List<String> realTokens = new ArrayList<>();
		QueryTokenizer qt = new QueryTokenizer("(This is\r:\na non \"valid\" query \t)");

		// Go, go, go
		while (qt.hasMoreTokens(true)) {
			realTokens.add(qt.nextToken(true));
		}

		// Verification
		assertEquals(Arrays.asList("(", "This", " ", "is", "\r", ":", "\n", "a", " ", "non", " ", "\"", "valid", "\"",
				" ", "query", " ", "\t", ")"), realTokens);
	}

	@Test
	void testNextToken_withoutEmptyTokens() {
		// Preparation
		List<String> realTokens = new ArrayList<>();
		QueryTokenizer qt = new QueryTokenizer("(This is\r:\na non \"valid\" query \t)");

		// Go, go, go
		while (qt.hasMoreTokens()) {
			realTokens.add(qt.nextToken());
		}

		// Verification
		assertEquals(Arrays.asList("(", "This", "is", ":", "a", "non", "\"", "valid", "\"", "query", ")"), realTokens);
	}

	@Test
	void testCheckNextToken() {
		QueryTokenizer qt = new QueryTokenizer("(This is\r:\na non \"valid\" query \t)");

		assertTrue(qt.checkNextToken("("));
		assertFalse(qt.checkNextToken("something else"));
		assertEquals("(", qt.nextToken());

		assertEquals("This", qt.nextToken());

		assertTrue(qt.checkNextToken("is"));
		assertFalse(qt.checkNextToken("something else"));
		assertEquals("is", qt.nextToken());

		assertTrue(qt.checkNextToken(":"));
		assertFalse(qt.checkNextToken("something else"));
		assertEquals(":", qt.nextToken());
	}

}
