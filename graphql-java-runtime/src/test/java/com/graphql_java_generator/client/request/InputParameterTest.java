package com.graphql_java_generator.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

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
	void test_getValueAsInteger_enum() throws GraphQLRequestExecutionException {
		String name = "aName";
		Integer value = 666;
		InputParameter param = InputParameter.newHardCodedParameter(name, value);

		assertEquals(name, param.getName(), "name");
		assertEquals(value, param.getValue(), "value");
		assertEquals(value.toString(), param.getValueForGraphqlQuery(new HashMap<>()), "escaped value");
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
