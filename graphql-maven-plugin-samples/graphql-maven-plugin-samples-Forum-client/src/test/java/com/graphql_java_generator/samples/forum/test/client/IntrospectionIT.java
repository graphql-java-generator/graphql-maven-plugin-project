/**
 * 
 */
package com.graphql_java_generator.samples.forum.test.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Board;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.__Schema;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.__Type;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.util.QueryExecutor;
import com.graphql_java_generator.samples.forum.test.SpringTestConfig;

/**
 * This class contains the Integration tests, that will execute GraphQL introspection querie against the
 * allGraphQLCases-server GraphQL server.
 * 
 * @author etienne-sf
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SpringTestConfig.class })
@TestPropertySource("classpath:application.properties")
@Execution(ExecutionMode.CONCURRENT)
public class IntrospectionIT {

	@Autowired
	QueryExecutor myQuery;

	@Test
	void testSchema() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		__Schema schema = myQuery.__schema("{types {name fields {name type {name}}}}");

		// Verification
		assertEquals(25, schema.getTypes().size());
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

		// Go, go, go
		List<Board> ret = myQuery.boards("{id __typename}");

		// Verification
		assertEquals("Board", ret.get(0).get__typename());
	}

}