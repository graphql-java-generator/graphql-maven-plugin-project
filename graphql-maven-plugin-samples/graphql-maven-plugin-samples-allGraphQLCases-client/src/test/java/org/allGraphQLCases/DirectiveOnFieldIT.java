package org.allGraphQLCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.allGraphQLCases.client.Character;
import org.allGraphQLCases.client.util.MyQueryTypeExecutor;
import org.allGraphQLCases.client.util.MyQueryTypeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@Execution(ExecutionMode.CONCURRENT)
class DirectiveOnFieldIT {

	MyQueryTypeExecutor queryType;

	@BeforeEach
	void setup() {
		queryType = new MyQueryTypeExecutor(Main.GRAPHQL_ENDPOINT);
	}

	@Test
	void withDirectiveOneParameter() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		MyQueryTypeResponse resp = queryType.exec(
				"{directiveOnField {id name @testDirective(value: &value) @anotherTestDirective}}", //
				"value", "this is a value");

		// Verifications
		assertNotNull(resp);
		Character ret = resp.getDirectiveOnField();
		assertNotNull(ret);
		assertEquals("this is a value", ret.getName());
	}

	@Test
	void testsIssue35() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		MyQueryTypeResponse resp = queryType.exec(
				"{directiveOnField {id name @testDirective(value: &value)  @anotherTestDirective}}", //
				"value", "this is a value");

		// Verifications
		assertNotNull(resp);
		Character ret = resp.getDirectiveOnField();
		assertNotNull(ret);
		assertEquals("this is a value", ret.getName());
	}

}
