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
import org.allGraphQLCases.client.__Field;
import org.allGraphQLCases.client.__Schema;
import org.allGraphQLCases.client.__Type;
import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * This class contains the Integration tests, that will execute GraphQL introspection querie against the
 * allGraphQLCases-server GraphQL server.
 * 
 * @author etienne-sf
 */
// Adding "webEnvironment = SpringBootTest.WebEnvironment.NONE" avoid this error:
// "No qualifying bean of type 'ReactiveClientRegistrationRepository' available"
// More details here: https://stackoverflow.com/questions/62558552/error-when-using-enablewebfluxsecurity-in-springboot
@SpringBootTest(classes = SpringTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Execution(ExecutionMode.CONCURRENT)
public class IntrospectionIT {

	static String[] AllFieldCases_FIELDS = { "id", "name", "forname", "age", "aFloat", "date", "dates", "nbComments",
			"comments", "booleans", "aliases", "planets", "friends", "matrix", "oneWithIdSubType", "listWithIdSubTypes",
			"oneWithoutIdSubType", "listWithoutIdSubTypes", "issue65", "issue66" };
	static List<String> AllFieldCases_FIELDNAMES = Arrays.asList(AllFieldCases_FIELDS);

	@Autowired
	MyQueryTypeExecutorAllGraphQLCases myQuery;

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void testSchema() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		__Schema schema = myQuery.__schema("{types {name fields(includeDeprecated:true) {name type {name}}}}");

		// Verification
		assertEquals(51, schema.getTypes().size());
		assertEquals("AllFieldCases", schema.getTypes().get(0).getName());
		// As the order of fields seems to depend on the way this is compile, let's use a rough method to check the
		// field names
		assertEquals(AllFieldCases_FIELDS.length, schema.getTypes().get(0).getFields().size());
		for (__Field f : schema.getTypes().get(0).getFields()) {
			assertTrue(AllFieldCases_FIELDNAMES.contains(f.getName()));
		}
	}

	@Execution(ExecutionMode.CONCURRENT)
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

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test__datatype_allFieldCases() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		// AllFieldCases ret = queryType.allFieldCases("{allFieldCases {id __typename}}", null);
		AllFieldCases ret = myQuery.allFieldCases("{id __typename}", null);

		// Verification
		assertEquals("AllFieldCases", ret.get__typename());
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test__datatype_withoutParameters()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		List<Character> ret = myQuery.withoutParameters(" {id __typename}");

		// Verification
		assertTrue(ret.size() >= 10);
		assertEquals("Droid", ret.get(0).get__typename());
	}

}