package com.graphql_java_generator.client.request;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.graphql_java_generator.client.domain.starwars.QueryType;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

public class Builder2Test {

	@Test
	void test_build_KO() {

		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new Builder(QueryType.class, "query").build());
		assertTrue(e.getMessage().contains("withQueryResponseDef"));

	}

}
