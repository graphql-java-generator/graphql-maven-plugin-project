package com.graphql_java_generator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.graphql_java_generator.client.domain.forum.AvailibilityType;
import com.graphql_java_generator.client.domain.forum.TopicInput;

class GraphqlUtilsTest {

	GraphqlUtils graphqlUtils;

	@BeforeEach
	void setUp() throws Exception {
		graphqlUtils = new GraphqlUtils();
	}

	@Test
	void test_getPascalCase() {
		assertEquals("PascalCase", graphqlUtils.getPascalCase("pascalCase"));
		assertEquals("PascalCase", graphqlUtils.getPascalCase("PascalCase"));
	}

	@Test
	void test_getInputObject() {
		// Preparation
		Map<String, Object> input = new LinkedHashMap<>();
		input.put("authorId", "00000000-0000-0000-0000-000000000003");
		input.put("date", "2009-11-20");
		input.put("publiclyAvailable", true);
		input.put("title", "The good title");
		input.put("content", "Some content");
		input.put("availibilityType", AvailibilityType.SEMI_PRIVATE);

		Map<String, Object> map = new LinkedHashMap<>();
		map.put("boardId", "00000000-0000-0000-0000-000000000004");
		map.put("input", input);

		// Go, go, go
		TopicInput topicInput = graphqlUtils.getInputObject(map, TopicInput.class);

		// Verification
		assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000004"), topicInput.getBoardId());
		assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000003"), topicInput.getInput().getAuthorId());
		assertEquals("Some content", topicInput.getInput().getContent());
		assertEquals("2009-11-20", topicInput.getInput().getDate());
		assertEquals(true, topicInput.getInput().getPubliclyAvailable());
		assertEquals("The good title", topicInput.getInput().getTitle());
		assertEquals(AvailibilityType.SEMI_PRIVATE, topicInput.getInput().getAvailibilityType());
	}

	@Test
	void test_getInputObjects() {
		// Preparation
		Map<String, Object> input1 = new LinkedHashMap<>();
		input1.put("authorId", "00000000-0000-0000-0000-000000000003");
		input1.put("date", "2009-11-20");
		input1.put("publiclyAvailable", true);
		input1.put("title", "The good title");
		input1.put("content", "Some content");
		input1.put("availibilityType", AvailibilityType.SEMI_PRIVATE);
		Map<String, Object> map1 = new LinkedHashMap<>();
		map1.put("boardId", "00000000-0000-0000-0000-000000000004");
		map1.put("input", input1);

		Map<String, Object> input2 = new LinkedHashMap<>();
		input2.put("authorId", "00000000-0000-0000-0000-000000000006");
		input2.put("date", "2009-11-25");
		input2.put("publiclyAvailable", false);
		input2.put("title", "The good title (2)");
		input2.put("content", "Some content (2)");
		input2.put("availibilityType", AvailibilityType.PRIVATE);
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
		assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000004"), topicInput.getBoardId());
		assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000003"), topicInput.getInput().getAuthorId());
		assertEquals("Some content", topicInput.getInput().getContent());
		assertEquals("2009-11-20", topicInput.getInput().getDate());
		assertEquals(true, topicInput.getInput().getPubliclyAvailable());
		assertEquals("The good title", topicInput.getInput().getTitle());
		assertEquals(AvailibilityType.SEMI_PRIVATE, topicInput.getInput().getAvailibilityType());

		topicInput = result.get(1);
		assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000005"), topicInput.getBoardId());
		assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000006"), topicInput.getInput().getAuthorId());
		assertEquals("Some content (2)", topicInput.getInput().getContent());
		assertEquals("2009-11-25", topicInput.getInput().getDate());
		assertEquals(false, topicInput.getInput().getPubliclyAvailable());
		assertEquals("The good title (2)", topicInput.getInput().getTitle());
		assertEquals(AvailibilityType.PRIVATE, topicInput.getInput().getAvailibilityType());
	}

}
