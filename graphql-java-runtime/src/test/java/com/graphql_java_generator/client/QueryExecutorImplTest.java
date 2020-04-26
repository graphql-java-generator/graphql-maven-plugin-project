package com.graphql_java_generator.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

class QueryExecutorImplTest {

	@Test
	void testGetWebSocketURI_http() throws GraphQLRequestExecutionException {
		QueryExecutorImpl queryExecutorImpl = new QueryExecutorImpl("http://my.local.host:123/my/path");
		assertEquals("ws://my.local.host:123/my/path", queryExecutorImpl.getWebSocketURI().toString());

	}

	@Test
	void testGetWebSocketURI_https() throws GraphQLRequestExecutionException {
		QueryExecutorImpl queryExecutorImpl = new QueryExecutorImpl("https://my.local.host:123/my/path");
		assertEquals("wss://my.local.host:123/my/path", queryExecutorImpl.getWebSocketURI().toString());
	}

	@Test
	void testGetWebSocketURI_KO() {
		QueryExecutorImpl queryExecutorImpl = new QueryExecutorImpl("ftp://my.local.host:123/my/path");
		GraphQLRequestExecutionException e = assertThrows(GraphQLRequestExecutionException.class,
				() -> queryExecutorImpl.getWebSocketURI());
		assertTrue(e.getMessage().contains("ftp://my.local.host:123/my/path"));
	}

}
