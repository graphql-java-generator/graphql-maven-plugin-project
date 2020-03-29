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

		// Check of all known delimiters
		assertEquals(Arrays.asList("{", "}", ",", ":", "(", ")", "@", "\"", " "),
				new QueryTokenizer("{},:()@\" ").tokens);
		// Check with a mix of words, empty delimiters and meaningful delimiters
		assertEquals(Arrays.asList("(", "This", " ", "is", "\r", ":", "\n", "a", " ", "non", " ", "valid", " ", "query",
				"\t", ")"), new QueryTokenizer("(This is\r:\na non valid query\t)").tokens);
	}

	@Test
	void test_QueryTokenizer_empty() {
		QueryTokenizer qt;
		RuntimeException e;

		qt = new QueryTokenizer(null);
		assertEquals(0, qt.tokens.size());
		assertFalse(qt.hasMoreTokens());
		assertFalse(qt.hasMoreTokens(true));
		assertFalse(qt.hasMoreTokens(false));
		e = assertThrows(RuntimeException.class, () -> new QueryTokenizer(null).nextToken());
		assertTrue(e.getMessage().contains("No more token"));
		e = assertThrows(RuntimeException.class, () -> new QueryTokenizer(null).nextToken(true));
		assertTrue(e.getMessage().contains("No more token"));
		e = assertThrows(RuntimeException.class, () -> new QueryTokenizer(null).nextToken(false));
		assertTrue(e.getMessage().contains("No more token"));

		qt = new QueryTokenizer("");
		assertEquals(0, qt.tokens.size());
		assertFalse(qt.hasMoreTokens());
		assertFalse(qt.hasMoreTokens(true));
		assertFalse(qt.hasMoreTokens(false));
		e = assertThrows(RuntimeException.class, () -> new QueryTokenizer("").nextToken());
		assertTrue(e.getMessage().contains("No more token"));
		e = assertThrows(RuntimeException.class, () -> new QueryTokenizer("").nextToken(true));
		assertTrue(e.getMessage().contains("No more token"));
		e = assertThrows(RuntimeException.class, () -> new QueryTokenizer("").nextToken(false));
		assertTrue(e.getMessage().contains("No more token"));
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
	void testNextToken_withoutEmptyTokens2() {
		// Preparation
		List<String> realTokens = new ArrayList<>();
		QueryTokenizer qt = new QueryTokenizer("{human(id:\"00000000-0000-0000-0000-000000000031\") {name}}");

		// Go, go, go
		while (qt.hasMoreTokens()) {
			realTokens.add(qt.nextToken());
		}

		// Verification
		assertEquals(Arrays.asList("{", "human", "(", "id", ":", "\"", "00000000-0000-0000-0000-000000000031", "\"",
				")", "{", "name", "}", "}"), realTokens);
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
