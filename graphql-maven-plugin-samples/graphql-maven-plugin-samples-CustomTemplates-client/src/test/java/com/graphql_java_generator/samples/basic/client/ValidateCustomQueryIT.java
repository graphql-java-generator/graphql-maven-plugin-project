package com.graphql_java_generator.samples.basic.client;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.generated.graphql.Character;
import com.generated.graphql.QueryType;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;


@SpringBootTest()
@RunWith(SpringRunner.class)
@ContextConfiguration(
		classes = ValidateCustomQueryConfiguration.class)
class ValidateCustomQueryIT {

	@Autowired
	QueryType query;

	@Test
	void test_hello() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		List<Character> response = query.characters("{id name}", null);
		assertNotNull(response);
		assertTrue(response.size() > 0);
		assertTrue(response.get(0) instanceof Character);
	}
	
}
