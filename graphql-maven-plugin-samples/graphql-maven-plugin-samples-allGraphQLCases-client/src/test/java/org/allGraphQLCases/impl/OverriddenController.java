package org.allGraphQLCases.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.allGraphQLCases.SpringTestConfig;
import org.allGraphQLCases.client.MyQueryTypeExecutorAllGraphQLCases;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@SpringBootTest(classes = SpringTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Execution(ExecutionMode.CONCURRENT)
public class OverriddenController {

	@Autowired
	MyQueryTypeExecutorAllGraphQLCases queryExecutor;

	@Test
	void checkThatTheControllerIsOverrided()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		assertEquals("Welcome from the overridden controller", queryExecutor.checkOverriddenController(""));
	}

}
