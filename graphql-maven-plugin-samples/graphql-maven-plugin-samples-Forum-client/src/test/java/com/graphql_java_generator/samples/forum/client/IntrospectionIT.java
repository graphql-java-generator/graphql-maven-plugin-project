/**
 * 
 */
package com.graphql_java_generator.samples.forum.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Board;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.QueryType;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.__Schema;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.__Type;

/**
 * This class contains the Integration tests, that will execute GraphQL introspection querie against the
 * allGraphQLCases-server GraphQL server.
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
public class IntrospectionIT {

	QueryType myQuery = new QueryType(Main.GRAPHQL_ENDPOINT_URL);

	@Test
	void testSchema() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		__Schema schema = myQuery.__schema("{types {name fields {name type {name}}}}");

		// Verification
		assertEquals(24, schema.getTypes().size());
		assertEquals("Board", schema.getTypes().get(0).getName());
		assertEquals("id", schema.getTypes().get(0).getFields().get(0).getName());
	}

	@Test
	void testType() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		__Type type = myQuery.__type("{name fields {name type {name}}}", "Board");

		// Verification
		assertEquals("Board", type.getName());
		assertEquals("id", type.getFields().get(0).getName());
	}

	@Test
	void test__datatype_allFieldCases() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Verification
		QueryType queryType = new QueryType(Main.GRAPHQL_ENDPOINT_URL);

		// Go, go, go
		// AllFieldCases ret = queryType.allFieldCases("{allFieldCases {id __typename}}", null);
		List<Board> ret = queryType.boards("{id __typename}");

		// Verification
		assertEquals("Board", ret.get(0).get__typename());
	}

}