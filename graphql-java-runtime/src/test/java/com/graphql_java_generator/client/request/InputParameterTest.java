package com.graphql_java_generator.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.graphql_java_generator.client.domain.starwars.Episode;
import com.graphql_java_generator.client.response.GraphQLRequestExecutionException;

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
	void test_getValueAsString_str() throws GraphQLRequestExecutionException {
		String name = "aName";
		String value = "This is a string with two \"\" to be escaped";
		InputParameter param = InputParameter.newHardCodedParameter(name, value);

		assertEquals(name, param.getName(), "name");
		assertEquals(value, param.getValue(), "value");
		assertEquals("\\\"This is a string with two \\\"\\\" to be escaped\\\"", param.getValueForGraphqlQuery(null),
				"escaped value");
	}

	@Test
	void test_getValueAsString_enum() throws GraphQLRequestExecutionException {
		String name = "aName";
		Episode value = Episode.EMPIRE;
		InputParameter param = InputParameter.newHardCodedParameter(name, value);

		assertEquals(name, param.getName(), "name");
		assertEquals(value, param.getValue(), "value");
		assertEquals("EMPIRE", param.getValueForGraphqlQuery(new HashMap<>()), "escaped value");
	}

	@Test
	void test_getValueAsString_int() throws GraphQLRequestExecutionException {
		String name = "aName";
		Integer value = 666;
		InputParameter param = InputParameter.newHardCodedParameter(name, value);

		assertEquals(name, param.getName(), "name");
		assertEquals(value, param.getValue(), "value");
		assertEquals(value.toString(), param.getValueForGraphqlQuery(new HashMap<>()), "escaped value");
	}

	@Test
	void test_getValueAsString_Float() throws GraphQLRequestExecutionException {
		String name = "aName";
		Float value = (float) 666.666;
		InputParameter param = InputParameter.newHardCodedParameter(name, value);

		assertEquals(name, param.getName(), "name");
		assertEquals(value, param.getValue(), "value");
		assertEquals(value.toString(), param.getValueForGraphqlQuery(new HashMap<>()), "escaped value");
	}

	@Test
	void test_getValueAsString_UUID() throws GraphQLRequestExecutionException {
		String name = "aName";
		UUID id = UUID.fromString("00000000-0000-0000-0000-000000000012");
		InputParameter param = InputParameter.newHardCodedParameter(name, id);

		assertEquals(name, param.getName(), "name");
		assertEquals(id, param.getValue(), "value");
		assertEquals("\\\"00000000-0000-0000-0000-000000000012\\\"", param.getValueForGraphqlQuery(new HashMap<>()),
				"escaped value");
	}

	@Test
	void test_getValueAsString_ListString() throws GraphQLRequestExecutionException {
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
				param.getValueForGraphqlQuery(new HashMap<>()), "escaped value");
	}

	@Test
	void getValueForGraphqlQuery_MandatoryBindVariable_OK() throws GraphQLRequestExecutionException {
		String name = "aName";
		String bindParameterName = "variableName";
		InputParameter mandatoryBindParam = InputParameter.newBindParameter(name, bindParameterName, true);

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
		InputParameter mandatoryBindParam = InputParameter.newBindParameter(name, bindParameterName, false);

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

}
