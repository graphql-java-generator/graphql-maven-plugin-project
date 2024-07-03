package org.allGraphQLCases.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.allGraphQLCases.SpringTestConfig;
import org.allGraphQLCases.client.CIP_Character_CIS;
import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@SpringBootTest(classes = SpringTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Execution(ExecutionMode.CONCURRENT)
public class OverriddenControllerIT {

	@Autowired
	MyQueryTypeExecutorAllGraphQLCases queryExecutor;

	@Test
	void checkThatTheQueryControllerIsOverridden()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		assertEquals("Welcome from the overridden controller", this.queryExecutor.checkOverriddenController(""));
	}

	@Test
	void checkThatTheCharacterControllerIsOverridden()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Let's execute the query that will trigger the overridden controller
		// String req = "{name(uppercase:true) @testDirective(value:\"checkThatTheCharacterControllerIsOverridden\")}";
		String req = "{friends @testDirective(value:\"checkThatTheCharacterControllerIsOverridden\") {name }}";
		List<CIP_Character_CIS> name = this.queryExecutor.withoutParameters(req);

		assertTrue(name.size() > 0,
				"We must have found at least one character, to check that its name comes from the overridden Character controler");
		name.stream()//
				.flatMap(c -> {
					assertTrue(c.getFriends().size() > 0, "The friends list must be not null and not empty");
					return c.getFriends().stream();
				})//
				.forEach(c -> assertTrue(c.getName().endsWith(" overriden by DataFetchersDelegateDroidImpl.friends()"),
						"The human name should finish by ' overriden by DataFetchersDelegateDroidImpl.friends()' but is +"
								+ c.getName()));
	}

}
