package com.graphql_java_generator.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.graphql_java_generator.client.domain.forum.AvailabilityType;
import com.graphql_java_generator.client.domain.forum.PostInput;
import com.graphql_java_generator.client.domain.forum.TopicPostInput;
import com.graphql_java_generator.client.domain.starwars.Episode;
import com.graphql_java_generator.customscalars.GraphQLScalarTypeDate;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

import graphql.Scalars;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

class InputParameterTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void test_InputParameter() {
		String name = "aName";
		String value = "a Value";
		InputParameter param = InputParameter.newHardCodedParameter(name, value);
		assertEquals(name, param.getName(), "name");
		assertEquals(value, param.getValue(), "value");
	}

	@Test
	void test_getValueForGraphqlQuery_str() throws GraphQLRequestExecutionException {
		String name = "aName";
		String value = "This is a string with two \"\" to be escaped";
		InputParameter param = InputParameter.newHardCodedParameter(name, value);

		assertEquals(name, param.getName(), "name");
		assertEquals(value, param.getValue(), "value");
		assertEquals("\\\"This is a string with two \\\"\\\" to be escaped\\\"", param.getValueForGraphqlQuery(null),
				"escaped value");
	}

	@Test
	void test_getValueForGraphqlQuery_enum() throws GraphQLRequestExecutionException {
		String name = "aName";
		Episode value = Episode.EMPIRE;
		InputParameter param = InputParameter.newHardCodedParameter(name, value);

		assertEquals(name, param.getName(), "name");
		assertEquals(value, param.getValue(), "value");
		assertEquals("EMPIRE", param.getValueForGraphqlQuery(new HashMap<>()));
	}

	@Test
	void test_getValueForGraphqlQuery_int() throws GraphQLRequestExecutionException {
		String name = "aName";
		Integer value = 666;
		InputParameter param = InputParameter.newHardCodedParameter(name, value);

		assertEquals(name, param.getName(), "name");
		assertEquals(value, param.getValue(), "value");
		assertEquals(value.toString(), param.getValueForGraphqlQuery(new HashMap<>()));
	}

	@Test
	void test_getValueForGraphqlQuery_Float() throws GraphQLRequestExecutionException {
		String name = "aName";
		Float value = (float) 666.666;
		InputParameter param = InputParameter.newHardCodedParameter(name, value);

		assertEquals(name, param.getName(), "name");
		assertEquals(value, param.getValue(), "value");
		assertEquals(value.toString(), param.getValueForGraphqlQuery(new HashMap<>()));
	}

	@Test
	void test_getValueForGraphqlQuery_UUID() throws GraphQLRequestExecutionException {
		String name = "aName";
		UUID id = UUID.fromString("00000000-0000-0000-0000-000000000012");
		InputParameter param = InputParameter.newHardCodedParameter(name, id);

		assertEquals(name, param.getName(), "name");
		assertEquals(id, param.getValue(), "value");
		assertEquals("\\\"00000000-0000-0000-0000-000000000012\\\"", param.getValueForGraphqlQuery(new HashMap<>()),
				"escaped value");
	}

	@Test
	void test_getValueForGraphqlQuery_recursive_InputType() throws GraphQLRequestExecutionException {
		// Preparation
		TopicPostInput topicPostInput = new TopicPostInput();
		topicPostInput.setAuthorId(UUID.fromString("00000000-0000-0000-0000-000000000012"));
		topicPostInput.setContent("Some other content");
		topicPostInput.setDate("2009-11-21");
		topicPostInput.setPubliclyAvailable(false);
		topicPostInput.setTitle("The good title for a post");

		PostInput postInput = new PostInput();
		postInput.setTopicId(UUID.fromString("00000000-0000-0000-0000-000000000022"));
		postInput.setInput(topicPostInput);

		String name = "anotherName";
		InputParameter param = InputParameter.newHardCodedParameter(name, postInput);

		// Verification
		assertEquals(
				"{topicId: \\\"00000000-0000-0000-0000-000000000022\\\", input: {authorId: \\\"00000000-0000-0000-0000-000000000012\\\", date: \\\"2009-11-21\\\", publiclyAvailable: false, title: \\\"The good title for a post\\\", content: \\\"Some other content\\\"}}",
				param.getValueForGraphqlQuery(postInput));

		postInput.getInput().setAvailabilityType(AvailabilityType.SEMI_PRIVATE);
		assertEquals(
				"{topicId: \\\"00000000-0000-0000-0000-000000000022\\\", input: {authorId: \\\"00000000-0000-0000-0000-000000000012\\\", date: \\\"2009-11-21\\\", publiclyAvailable: false, title: \\\"The good title for a post\\\", content: \\\"Some other content\\\", availabilityType: SEMI_PRIVATE}}",
				param.getValueForGraphqlQuery(postInput));
	}

	@Test
	void test_getValueForGraphqlQuery_ListEmptyString() throws GraphQLRequestExecutionException {
		String name = "anotherName";
		List<String> values = new ArrayList<>();
		InputParameter param = InputParameter.newHardCodedParameter(name, values);

		assertEquals(name, param.getName(), "name");
		assertEquals(values, param.getValue(), "value");
		assertEquals("[]", param.getValueForGraphqlQuery(new HashMap<>()));
	}

	@Test
	void test_getValueForGraphqlQuery_ListString() throws GraphQLRequestExecutionException {
		String name = "anotherName";
		String value1 = "first value";
		String value2 = "second value";
		String value3 = "third value";
		List<String> values = new ArrayList<>();
		values.add(value1);
		values.add(value2);
		values.add(value3);
		InputParameter param = InputParameter.newHardCodedParameter(name, values);

		assertEquals(name, param.getName(), "name");
		assertEquals(values, param.getValue(), "value");
		assertEquals("[\\\"" + value1 + "\\\",\\\"" + value2 + "\\\",\\\"" + value3 + "\\\"]",
				param.getValueForGraphqlQuery(new HashMap<>()));
	}

	@Test
	void test_getValueForGraphqlQuery_ListEpisode() throws GraphQLRequestExecutionException {
		String name = "anotherName";
		Episode value1 = Episode.EMPIRE;
		Episode value2 = Episode.JEDI;
		Episode value3 = Episode.NEWHOPE;
		List<Episode> values = new ArrayList<>();
		values.add(value1);
		values.add(value2);
		values.add(value3);
		InputParameter param = InputParameter.newHardCodedParameter(name, values);

		assertEquals(name, param.getName(), "name");
		assertEquals(values, param.getValue(), "value");
		assertEquals("[EMPIRE,JEDI,NEWHOPE]", param.getValueForGraphqlQuery(new HashMap<>()));
	}

	@Test
	void getValueForGraphqlQuery_MandatoryBindVariable_OK() throws GraphQLRequestExecutionException {
		String name = "aName";
		String bindParameterName = "variableName";
		InputParameter mandatoryBindParam = InputParameter.newBindParameter(name, bindParameterName, true, null);

		assertEquals(name, mandatoryBindParam.getName(), "name");
		assertEquals(null, mandatoryBindParam.getValue(), "value");
		assertEquals(bindParameterName, mandatoryBindParam.bindParameterName, "bindParameterName");
		assertThrows(GraphQLRequestExecutionException.class, () -> mandatoryBindParam.getValueForGraphqlQuery(null),
				"escaped value (null map)");
		assertThrows(GraphQLRequestExecutionException.class,
				() -> mandatoryBindParam.getValueForGraphqlQuery(new HashMap<>()), "escaped value (empty map)");

		Map<String, Object> bindVariablesValues = new HashMap<>();
		bindVariablesValues.put("anotherBind", "A value");
		bindVariablesValues.put(bindParameterName, 666);
		assertEquals("666", mandatoryBindParam.getValueForGraphqlQuery(bindVariablesValues),
				"escaped value (correct map)");
	}

	@Test
	void getValueForGraphqlQuery_OptionalBindVariable_OK() throws GraphQLRequestExecutionException {
		String name = "aName";
		String bindParameterName = "variableName";
		InputParameter mandatoryBindParam = InputParameter.newBindParameter(name, bindParameterName, false, null);

		assertEquals(name, mandatoryBindParam.getName(), "name");
		assertEquals(null, mandatoryBindParam.getValue(), "value");
		assertEquals(bindParameterName, mandatoryBindParam.bindParameterName, "bindParameterName");
		assertNull(mandatoryBindParam.getValueForGraphqlQuery(null), "with no given map");
		assertNull(mandatoryBindParam.getValueForGraphqlQuery(new HashMap<>()), "escaped value (empty map)");

		Map<String, Object> bindVariablesValues = new HashMap<>();
		bindVariablesValues.put("anotherBind", "A value");
		bindVariablesValues.put(bindParameterName, 666);
		assertEquals("666", mandatoryBindParam.getValueForGraphqlQuery(bindVariablesValues),
				"escaped value (correct map)");
	}

	@Test
	void getValueForGraphqlQuery_BindParameter_CustomScalar_Date_OK() throws GraphQLRequestExecutionException {
		GraphQLScalarTypeDate graphQLScalarTypeDate = new GraphQLScalarTypeDate();
		String name = "aName";
		String bindParameterName = "variableName";
		InputParameter customScalarInputParameter = InputParameter.newBindParameter(name, bindParameterName, false,
				GraphQLScalarTypeDate.Date);

		Map<String, Object> badValues = new HashMap<>();
		badValues.put("variableName", "A bad date");
		CoercingSerializeException e = assertThrows(CoercingSerializeException.class,
				() -> customScalarInputParameter.getValueForGraphqlQuery(badValues));
		assertTrue(e.getMessage().contains("A bad date"));

		@SuppressWarnings("deprecation")
		Date date = new Date(2020 - 1900, 01 - 1, 19); // Years starts at 1900. Month is between 0 and 11
		Map<String, Object> goodValues = new HashMap<>();
		goodValues.put("variableName", date);

		assertEquals("\\\"2020-01-19\\\"", customScalarInputParameter.getValueForGraphqlQuery(goodValues));
	}

	@Test
	void getValueForGraphqlQuery_BindParameter_CustomScalar_Long_OK() throws GraphQLRequestExecutionException {
		GraphQLScalarType graphQLScalarTypeLong = Scalars.GraphQLLong;
		String name = "aName";
		String bindParameterName = "variableName";
		InputParameter customScalarInputParameter = InputParameter.newBindParameter(name, bindParameterName, false,
				graphQLScalarTypeLong);

		Map<String, Object> badValues = new HashMap<>();
		badValues.put("variableName", "A bad long");
		CoercingSerializeException e = assertThrows(CoercingSerializeException.class,
				() -> customScalarInputParameter.getValueForGraphqlQuery(badValues));
		assertTrue(e.getMessage().contains("Long"));

		Long l = Long.MAX_VALUE;
		String ls = ((Long) Long.MAX_VALUE).toString();
		Map<String, Object> goodValues = new HashMap<>();
		goodValues.put("variableName", l);

		assertEquals(ls, customScalarInputParameter.getValueForGraphqlQuery(goodValues));
	}

}
