package com.graphql_java_generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.graphql_java_generator.client.domain.forum.TopicInput;
import com.graphql_java_generator.client.domain.forum.TopicPostInput;

@Execution(ExecutionMode.CONCURRENT)
class GraphqlUtilsTest {

	GraphqlUtils graphqlUtils = new GraphqlUtils();

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
	@Execution(ExecutionMode.CONCURRENT)
	void test_getInputObject() {
		// Preparation
		Map<String, Object> input = new LinkedHashMap<>();
		input.put("authorId", "00000000-0000-0000-0000-000000000003");
		input.put("date", new GregorianCalendar(2009, 11 - 1, 20).getTime());
		input.put("publiclyAvailable", true);
		input.put("title", "The good title");
		input.put("content", "Some content");

		Map<String, Object> map = new LinkedHashMap<>();
		map.put("boardId", "00000000-0000-0000-0000-000000000004");
		map.put("input", input);

		// Go, go, go
		TopicInput topicInput = graphqlUtils.getInputObject(map, TopicInput.class);

		// Verification
		assertEquals("00000000-0000-0000-0000-000000000004", topicInput.getBoardId());
		assertEquals("00000000-0000-0000-0000-000000000003", topicInput.getInput().getAuthorId());
		assertEquals("Some content", topicInput.getInput().getContent());
		assertEquals(new GregorianCalendar(2009, 11 - 1, 20).getTime(), topicInput.getInput().getDate());
		assertEquals(true, topicInput.getInput().getPubliclyAvailable());
		assertEquals("The good title", topicInput.getInput().getTitle());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getInput_emptyMap() {
		// Preparation
		Map<String, Object> map = new LinkedHashMap<>();

		// Go, go, go
		TopicInput topicInput = graphqlUtils.getInputObject(map, TopicInput.class);

		// Verification
		assertNull(topicInput.getBoardId());
		assertNull(topicInput.getInput());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getInputObject_nullMap() {
		assertNull(graphqlUtils.getInputObject(null, TopicInput.class), "A null map return a null object");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getInputObjects() {
		// Preparation
		Map<String, Object> input1 = new LinkedHashMap<>();
		input1.put("authorId", "00000000-0000-0000-0000-000000000003");
		input1.put("date", new GregorianCalendar(2009, 11 - 1, 20).getTime());
		input1.put("publiclyAvailable", true);
		input1.put("title", "The good title");
		input1.put("content", "Some content");
		Map<String, Object> map1 = new LinkedHashMap<>();
		map1.put("boardId", "00000000-0000-0000-0000-000000000004");
		map1.put("input", input1);

		Map<String, Object> input2 = new LinkedHashMap<>();
		input2.put("authorId", "00000000-0000-0000-0000-000000000006");
		input2.put("date", new GregorianCalendar(2009, 11 - 1, 25).getTime());
		input2.put("publiclyAvailable", false);
		input2.put("title", "The good title (2)");
		input2.put("content", "Some content (2)");
		Map<String, Object> map2 = new LinkedHashMap<>();
		map2.put("boardId", "00000000-0000-0000-0000-000000000005");
		map2.put("input", input2);

		List<Map<String, Object>> list = new ArrayList<>();
		list.add(map1);
		list.add(map2);

		// Go, go, go
		List<TopicInput> result = graphqlUtils.getListInputObjects(list, TopicInput.class);

		// Preparation
		TopicInput topicInput = result.get(0);
		assertEquals("00000000-0000-0000-0000-000000000004", topicInput.getBoardId().toString());
		assertEquals("00000000-0000-0000-0000-000000000003", topicInput.getInput().getAuthorId().toString());
		assertEquals("Some content", topicInput.getInput().getContent());
		assertEquals(new GregorianCalendar(2009, 11 - 1, 20).getTime(), topicInput.getInput().getDate());
		assertEquals(true, topicInput.getInput().getPubliclyAvailable());
		assertEquals("The good title", topicInput.getInput().getTitle());

		topicInput = result.get(1);
		assertEquals("00000000-0000-0000-0000-000000000005", topicInput.getBoardId().toString());
		assertEquals("00000000-0000-0000-0000-000000000006", topicInput.getInput().getAuthorId().toString());
		assertEquals("Some content (2)", topicInput.getInput().getContent());
		assertEquals(new GregorianCalendar(2009, 11 - 1, 25).getTime(), topicInput.getInput().getDate());
		assertEquals(false, topicInput.getInput().getPubliclyAvailable());
		assertEquals("The good title (2)", topicInput.getInput().getTitle());
	}

}
