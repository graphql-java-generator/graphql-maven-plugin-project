package com.graphql_java_generator.util_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.domain.client.allGraphQLCases.EnumWithReservedJavaKeywordAsValues;
import com.graphql_java_generator.domain.client.forum.TopicInput;
import com.graphql_java_generator.domain.client.forum.TopicPostInput;
import com.graphql_java_generator.util.GraphqlUtils;

@Execution(ExecutionMode.CONCURRENT)
class GraphqlUtilsTest {

	GraphqlClientUtils graphqlClientUtils = new GraphqlClientUtils();
	GraphqlUtils graphqlUtils = new GraphqlUtils();

	@BeforeAll
	public static void initCustomScalarRegistry() {
		com.graphql_java_generator.domain.client.allGraphQLCases.RegistriesInitializer.initializeAllRegistries();
		com.graphql_java_generator.domain.client.forum.RegistriesInitializer.initializeAllRegistries();
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getPascalCase() {
		assertEquals("PascalCase", graphqlUtils.getPascalCase("pascalCase"));
		assertEquals("PascalCase", graphqlUtils.getPascalCase("PascalCase"));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getSetter() throws NoSuchFieldException, SecurityException {
		// Preparation
		Field field = TopicInput.class.getDeclaredField("input");

		// Go, go, go
		Method setter = graphqlUtils.getSetter(TopicInput.class, field);

		// Verification
		assertEquals("setInput", setter.getName());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getGetter() throws NoSuchFieldException, SecurityException {
		// Preparation
		Field field = TopicInput.class.getDeclaredField("input");

		// Go, go, go
		Method getter = graphqlUtils.getGetter(TopicInput.class, field);

		// Verification
		assertEquals("getInput", getter.getName());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_invokeGetter() throws NoSuchFieldException, SecurityException {
		// Preparation
		TopicPostInput topicPostInput = new TopicPostInput();

		TopicInput topicInput = new TopicInput();
		topicInput.setInput(topicPostInput);

		// Go, go, go
		assertEquals(null, graphqlUtils.invokeGetter(topicInput, "boardId"));
		assertEquals(topicPostInput, graphqlUtils.invokeGetter(topicInput, "input"));
		RuntimeException e = assertThrows(RuntimeException.class,
				() -> graphqlUtils.invokeGetter(topicInput, "nonExistingField"));
		assertTrue(e.getMessage().contains("nonExistingField"));
		assertTrue(e.getMessage().contains(TopicInput.class.getName()));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_invokeSetter() {
		TopicPostInput topicPostInput = new TopicPostInput();

		graphqlUtils.invokeSetter(topicPostInput, "date", new GregorianCalendar(2020, 3 - 1, 1).getTime());
		assertEquals(new GregorianCalendar(2020, 3 - 1, 1).getTime(), topicPostInput.getDate());

		graphqlUtils.invokeSetter(topicPostInput, "publiclyAvailable", true);
		assertEquals(true, topicPostInput.getPubliclyAvailable());
	}

	@Test
	void test_stringToEnumValue() {
		assertNull(graphqlUtils.stringToEnumValue(null, EnumWithReservedJavaKeywordAsValues.class));
		assertEquals(//
				EnumWithReservedJavaKeywordAsValues._if, //
				graphqlUtils.stringToEnumValue("if", EnumWithReservedJavaKeywordAsValues.class));
		assertEquals(//
				Arrays.asList(EnumWithReservedJavaKeywordAsValues._assert, null,
						EnumWithReservedJavaKeywordAsValues._break), //
				graphqlUtils.stringToEnumValue(//
						Arrays.asList("assert", null, "break"), //
						EnumWithReservedJavaKeywordAsValues.class));
	}
}
