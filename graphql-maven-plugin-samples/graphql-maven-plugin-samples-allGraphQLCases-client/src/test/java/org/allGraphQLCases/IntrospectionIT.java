/**
 * 
 */
package org.allGraphQLCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.allGraphQLCases.client.AllFieldCases;
import org.allGraphQLCases.client.Character;
import org.allGraphQLCases.client.MyQueryType;
import org.allGraphQLCases.client.MyQueryTypeExecutor;
import org.allGraphQLCases.client.__Field;
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

	MyQueryTypeExecutor myQuery = new MyQueryTypeExecutor(Main.GRAPHQL_ENDPOINT);

	static String[] AllFieldCases_FIELDS = { "id", "name", "forname", "age", "aFloat", "date", "dates", "nbComments",
			"comments", "booleans", "aliases", "planets", "friends", "oneWithIdSubType", "listWithIdSubTypes",
			"oneWithoutIdSubType", "listWithoutIdSubTypes" };
	static List<String> AllFieldCases_FIELDNAMES = Arrays.asList(AllFieldCases_FIELDS);

	@Test
	void testSchema() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		__Schema schema = myQuery.__schema("{types {name fields(includeDeprecated:true) {name type {name}}}}");

		// Verification
		assertEquals(44, schema.getTypes().size());
		assertEquals("AllFieldCases", schema.getTypes().get(0).getName());
		// As the order of fields seems to depend on the way this is compile, let's use a rough method to check the
		// field names
		assertEquals(AllFieldCases_FIELDS.length, schema.getTypes().get(0).getFields().size());
		for (__Field f : schema.getTypes().get(0).getFields()) {
			assertTrue(AllFieldCases_FIELDNAMES.contains(f.getName()));
		}
	}

	@Test
	void testType() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		__Type type = myQuery.__type("{name fields(includeDeprecated:true) {name type {name}}}", "AllFieldCases");

		// Verification

		assertEquals("AllFieldCases", type.getName());
		// As the order of fields seems to depend on the way this is compile, let's use a rough method to check the
		// field names
		assertEquals(AllFieldCases_FIELDS.length, type.getFields().size());
		for (__Field f : type.getFields()) {
			assertTrue(AllFieldCases_FIELDNAMES.contains(f.getName()));
		}
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