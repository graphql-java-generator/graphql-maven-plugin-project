/**
 * 
 */
package org.allGraphQLCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.allGraphQLCases.client.AllFieldCases;
import org.allGraphQLCases.client.Character;
import org.allGraphQLCases.client.MyQueryType;
import org.allGraphQLCases.client.__Schema;
import org.allGraphQLCases.client.__Type;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * This class contains the Integration tests, that will execute GraphQL introspection querie against the
 * allGraphQLCases-server GraphQL server.
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
public class IntrospectionIT {

	MyQueryType myQuery = new MyQueryType(Main.GRAPHQL_ENDPOINT);

	@Test
	void testSchema() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		__Schema schema = myQuery.__schema("{types {name fields {name type {name}}}}");

		// Verification
		assertEquals(44, schema.getTypes().size());
		assertEquals("AllFieldCases", schema.getTypes().get(0).getName());
		// With the maven compilation, the order from the GraphQL schema is kept.
		// But with the gradle build, the field are returned in alphabetical order?!
		// As this test is run against the maven and the gradle implementation (see the gradle plugin), we need to be
		// compliant with both sort orders.
		assertTrue(schema.getTypes().get(0).getFields().get(0).getName().contentEquals("id")
				|| schema.getTypes().get(0).getFields().get(0).getName().contentEquals("aliases"));
	}

	@Test
	void testType() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		__Type type = myQuery.__type("{name fields {name type {name}}}", "AllFieldCases");

		// Verification
		assertEquals("AllFieldCases", type.getName());
		// With the maven compilation, the order from the GraphQL schema is kept.
		// But with the gradle build, the field are returned in alphabetical order?!
		// As this test is run against the maven and the gradle implementation (see the gradle plugin), we need to be
		// compliant with both sort orders.
		assertTrue(
				type.getFields().get(0).getName().equals("id") || type.getFields().get(0).getName().equals("aliases"));
	}

	@Test
	void test__datatype_allFieldCases() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Verification
		MyQueryType queryType = new MyQueryType(Main.GRAPHQL_ENDPOINT);

		// Go, go, go
		// AllFieldCases ret = queryType.allFieldCases("{allFieldCases {id __typename}}", null);
		AllFieldCases ret = queryType.allFieldCases("{id __typename}", null);

		// Verification
		assertEquals("AllFieldCases", ret.get__typename());
	}

	@Test
	void test__datatype_withoutParameters()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Verification
		MyQueryType queryType = new MyQueryType(Main.GRAPHQL_ENDPOINT);

		// Go, go, go
		List<Character> ret = queryType.withoutParameters(" {id __typename}");

		// Verification
		assertTrue(ret.size() >= 10);
		assertEquals("Droid", ret.get(0).get__typename());
	}

}