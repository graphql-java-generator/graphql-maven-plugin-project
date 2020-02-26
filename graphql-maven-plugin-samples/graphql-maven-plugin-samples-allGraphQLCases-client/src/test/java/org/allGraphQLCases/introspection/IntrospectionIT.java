/**
 * 
 */
package org.allGraphQLCases.introspection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.allGraphQLCases.Main;
import org.junit.jupiter.api.Test;

import com.graphql_java_generator.client.introspection.IntrospectionQuery;
import com.graphql_java_generator.client.introspection.__Schema;
import com.graphql_java_generator.client.introspection.__Type;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * This class contains the Integration tests, that will execute GraphQL introspection querie against the
 * allGraphQLCases-server GraphQL server.
 * 
 * @author etienne-sf
 */
public class IntrospectionIT {

	IntrospectionQuery introspectionQuery = new IntrospectionQuery(Main.GRAPHQL_ENDPOINT);

	@Test
	void testSchema() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		__Schema schema = introspectionQuery.__schema("{types {name fields {name type {name}}}}");

		// Verification
		assertEquals(44, schema.getTypes().size());
		assertEquals("AllFieldCases", schema.getTypes().get(0).getName());
		assertEquals("id", schema.getTypes().get(0).getFields().get(0).getName());
	}

	@Test
	void testType() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		__Type type = introspectionQuery.__type("{name fields {name type {name}}}", "AllFieldCases");

		// Verification
		assertEquals("AllFieldCases", type.getName());
		assertEquals("id", type.getFields().get(0).getName());
	}

}
