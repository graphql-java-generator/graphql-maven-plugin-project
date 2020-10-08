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

import com.graphql_java_generator.client.domain.allGraphQLCases.AllFieldCasesInput;
import com.graphql_java_generator.client.domain.allGraphQLCases.FieldParameterInput;
import com.graphql_java_generator.client.domain.forum.TopicInput;
import com.graphql_java_generator.client.domain.forum.TopicPostInput;
import com.graphql_java_generator.client.domain.starwars.Episode;
import com.graphql_java_generator.client.domain.starwars.Human;
import com.graphql_java_generator.customscalars.CustomScalarRegistryImpl;
import com.graphql_java_generator.customscalars.GraphQLScalarTypeDate;
import com.graphql_java_generator.testcases.Isssue49AccountInput;
import com.graphql_java_generator.testcases.Issue49Title;

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
		input.put("date", "2009-11-20");
		input.put("publiclyAvailable", "true");
		input.put("title", "The good title");
		input.put("content", "Some content");
		//
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("boardId", "00000000-0000-0000-0000-000000000004");
		map.put("input", input);
		//
		// And we need to register the custom scalar
		CustomScalarRegistryImpl.customScalarRegistry.registerGraphQLScalarType(GraphQLScalarTypeDate.Date);

		// Go, go, go
		TopicInput topicInput = graphqlUtils.getInputObject(map, false, TopicInput.class);

		// Verification
		assertEquals("00000000-0000-0000-0000-000000000004", topicInput.getBoardId());
		assertEquals("00000000-0000-0000-0000-000000000003", topicInput.getInput().getAuthorId());
		assertEquals("Some content", topicInput.getInput().getContent());
		assertEquals(new GregorianCalendar(2009, 11 - 1, 20).getTime(), topicInput.getInput().getDate());
		assertEquals(true, topicInput.getInput().getPubliclyAvailable());
		assertEquals("The good title", topicInput.getInput().getTitle());
	}

	@Test
	void test_getInputObject_CustomScalar() {
		// Preparation
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("uppercase", "true");
		map.put("date", "2345-02-24");
		//
		// And we need to register the custom scalar
		CustomScalarRegistryImpl.customScalarRegistry.registerGraphQLScalarType(GraphQLScalarTypeDate.Date);

		// Go, go, go
		FieldParameterInput input = graphqlUtils.getInputObject(map, false, FieldParameterInput.class);

		// Verification
		assertTrue(input.getUppercase());
		assertEquals(new GregorianCalendar(2345, 2 - 1, 24).getTime(), input.getDate());
	}

	@Test
	void test_getInputObject_Enum() {
		// Preparation
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("title", "MRS");

		// Go, go, go
		Isssue49AccountInput input = graphqlUtils.getInputObject(map, false, Isssue49AccountInput.class);

		// Verification
		assertEquals(Issue49Title.MRS, input.getTitle());
	}

	@Test
	void test_getInputObject_ListOfEnum() {
		// Preparation
		List<String> episodes = new ArrayList<>();
		episodes.add(Episode.JEDI.toString());
		episodes.add(Episode.EMPIRE.toString());
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("appearsIn", episodes);

		// Go, go, go
		Human human = graphqlUtils.getInputObject(map, true, Human.class);

		// Verification
		assertEquals(2, human.getAppearsIn().size());
		assertTrue(human.getAppearsIn().contains(Episode.JEDI));
		assertTrue(human.getAppearsIn().contains(Episode.EMPIRE));
	}

	@Test
	void test_getInputObject_ListOfInputTypes() {
		// Preparation
		//
		Map<String, Object> map1 = new LinkedHashMap<>();
		map1.put("id", "id1");
		map1.put("name", "name1");
		//
		Map<String, Object> map2 = new LinkedHashMap<>();
		map2.put("id", "id2");
		map2.put("name", "name2");
		//
		List<Map<String, Object>> listAllFieldCasesWithIdSubtypeInputs = new ArrayList<>();
		listAllFieldCasesWithIdSubtypeInputs.add(map1);
		listAllFieldCasesWithIdSubtypeInputs.add(map2);
		//
		Map<String, Object> mapAllFieldCasesWithIdSubtypeInput = new LinkedHashMap<>();
		mapAllFieldCasesWithIdSubtypeInput.put("withIdSubtype", listAllFieldCasesWithIdSubtypeInputs);

		// Go, go, go
		AllFieldCasesInput input = graphqlUtils.getInputObject(mapAllFieldCasesWithIdSubtypeInput, true,
				AllFieldCasesInput.class);

		// Verification
		assertEquals(2, input.getWithIdSubtype().size());
		assertEquals("id1", input.getWithIdSubtype().get(0).getId());
		assertEquals("name1", input.getWithIdSubtype().get(0).getName());
		assertEquals("id2", input.getWithIdSubtype().get(1).getId());
		assertEquals("name2", input.getWithIdSubtype().get(1).getName());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getInput_emptyMap() {
		// Preparation
		Map<String, Object> map = new LinkedHashMap<>();

		// Go, go, go
		TopicInput topicInput = graphqlUtils.getInputObject(map, false, TopicInput.class);

		// Verification
		assertNull(topicInput.getBoardId());
		assertNull(topicInput.getInput());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getInputObject_nullMap() {
		assertNull(graphqlUtils.getInputObject(null, false, TopicInput.class), "A null map return a null object");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getInputObjects() {
		// Preparation
		Map<String, Object> input1 = new LinkedHashMap<>();
		input1.put("authorId", "00000000-0000-0000-0000-000000000003");
		input1.put("date", "2009-11-20");
		input1.put("publiclyAvailable", "true");
		input1.put("title", "The good title");
		input1.put("content", "Some content");
		Map<String, Object> map1 = new LinkedHashMap<>();
		map1.put("boardId", "00000000-0000-0000-0000-000000000004");
		map1.put("input", input1);
		//
		Map<String, Object> input2 = new LinkedHashMap<>();
		input2.put("authorId", "00000000-0000-0000-0000-000000000006");
		input2.put("date", "2009-11-25");
		input2.put("publiclyAvailable", "false");
		input2.put("title", "The good title (2)");
		input2.put("content", "Some content (2)");
		Map<String, Object> map2 = new LinkedHashMap<>();
		map2.put("boardId", "00000000-0000-0000-0000-000000000005");
		map2.put("input", input2);
		//
		// And we need to register the custom scalar
		CustomScalarRegistryImpl.customScalarRegistry.registerGraphQLScalarType(GraphQLScalarTypeDate.Date);

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
