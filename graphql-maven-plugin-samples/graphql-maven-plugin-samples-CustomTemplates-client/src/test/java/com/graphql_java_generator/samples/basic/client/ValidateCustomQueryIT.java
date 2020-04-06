package com.graphql_java_generator.samples.basic.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.customtemplates.client.graphql.forum.client.Board;
import com.graphql_java_generator.samples.customtemplates.client.graphql.forum.client.QueryType;


@SpringBootTest()
@RunWith(SpringRunner.class)
@ContextConfiguration(
		classes = ValidateCustomQueryConfiguration.class)
class ValidateCustomQueryIT {

	@Autowired
	QueryType query;

	@Test
	void test_hello() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		List<Board> response = query.boards("{id name}");
		assertNotNull(response);
		assertTrue(response.size() > 0);
		assertTrue(response.get(0) instanceof Board);
	}
	
}
