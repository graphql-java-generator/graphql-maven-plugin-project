package com.graphql_java_generator.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;
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
	void testGetInputObject() {
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
		TopicInput topicInput = graphqlUtils.getInputObject(map, new TopicInput());

		// Verification
		assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000004"), topicInput.getBoardId());
		assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000003"), topicInput.getInput().getAuthorId());
		assertEquals("Some content", topicInput.getInput().getContent());
		assertEquals("2009-11-20", topicInput.getInput().getDate());
		assertEquals(true, topicInput.getInput().getPubliclyAvailable());
		assertEquals("The good title", topicInput.getInput().getTitle());
		assertEquals(AvailibilityType.SEMI_PRIVATE, topicInput.getInput().getAvailibilityType());
	}

}
