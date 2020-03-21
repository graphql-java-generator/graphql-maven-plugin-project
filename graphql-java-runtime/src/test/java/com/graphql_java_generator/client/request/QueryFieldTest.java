package com.graphql_java_generator.client.request;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.graphql_java_generator.client.domain.allGraphQLCases.MyQueryTypeResponse;
import com.graphql_java_generator.client.domain.allGraphQLCases.MyQueryTypeRootResponse;
import com.graphql_java_generator.client.domain.allGraphQLCases._break;
import com.graphql_java_generator.client.domain.forum.Board;
import com.graphql_java_generator.client.domain.forum.Post;
import com.graphql_java_generator.client.domain.starwars.MutationTypeRootResponse;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

class QueryFieldTest {

	@Test
	void testIsScalar() throws GraphQLRequestPreparationException {
		assertFalse(new QueryField(MyQueryTypeRootResponse.class, "query").isScalar());
		assertFalse(new QueryField(MutationTypeRootResponse.class, "mutation").isScalar());

		assertFalse(new QueryField(MyQueryTypeResponse.class, "aBreak").isScalar());
		assertFalse(new QueryField(Post.class, "author").isScalar());

		// Interface
		assertFalse(new QueryField(MyQueryTypeResponse.class, "withEnum").isScalar());
		// Union
		assertFalse(new QueryField(MyQueryTypeResponse.class, "withEnum").isScalar());

		// Boolean
		assertTrue(new QueryField(Board.class, "publiclyAvailable").isScalar());
		// custom scalar
		assertTrue(new QueryField(Post.class, "date").isScalar());
		// enum
		assertTrue(new QueryField(_break.class, "case").isScalar());
		// ID
		assertTrue(new QueryField(Post.class, "id").isScalar());
		// String
		assertTrue(new QueryField(Post.class, "title").isScalar());
	}

	@Test
	void testIsQueryLevel() throws GraphQLRequestPreparationException {
		assertTrue(new QueryField(MyQueryTypeRootResponse.class, "query").isQueryLevel());
		assertTrue(new QueryField(MyQueryTypeRootResponse.class, "mutation").isQueryLevel());
		assertTrue(new QueryField(MyQueryTypeRootResponse.class, "subscription").isQueryLevel());

		assertFalse(new QueryField(MyQueryTypeResponse.class, "aBreak").isQueryLevel());
		assertFalse(new QueryField(_break.class, "case").isQueryLevel());
	}

}
