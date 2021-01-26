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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@Execution(ExecutionMode.CONCURRENT)
class DirectiveOnFieldIT {

	MyQueryTypeExecutor queryType;

	ApplicationContext ctx;

	@BeforeEach
	void setup() {
		ctx = new AnnotationConfigApplicationContext(SpringTestConfig.class);

		// For some tests, we need to execute additional partialQueries
		queryType = ctx.getBean(MyQueryTypeExecutor.class);
		assertNotNull(queryType);
	}

	@Execution(ExecutionMode.CONCURRENT)
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

	@Execution(ExecutionMode.CONCURRENT)
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
