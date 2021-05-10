package com.graphql_java_generator.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.graphql_java_generator.client.GraphQLObjectMapperTestClass.TestEnum;
import com.graphql_java_generator.customscalars.GraphQLScalarTypeDate;

class GraphQLObjectMapperTest {

	GraphQLObjectMapper objectMapper;

	@BeforeEach
	void beforeEach() {
		objectMapper = new GraphQLObjectMapper();
		objectMapper.initObjectMapper();
		objectMapper.graphQLObjectsPackage = this.getClass().getPackage().getName();
	}

	@Test
	void testBoolean() throws JsonMappingException, JsonProcessingException {
		// Go, go, go
		GraphQLObjectMapperTestClass test = objectMapper.readValue(
				"{\"boolTrue\":true, \"boolFalse\":false, \"__typename\":\"GraphQLObjectMapperTestClass\"}",
				GraphQLObjectMapperTestClass.class);

		// Verification
		assertEquals(2, test.aliasParsedValues.keySet().size());
		assertTrue((Boolean) test.aliasParsedValues.get("boolTrue"));
		assertFalse((Boolean) test.aliasParsedValues.get("boolFalse"));
	}

	@Test
	void testCustomScalar() throws JsonMappingException, JsonProcessingException {
		// Preparation
		Date date = new Calendar.Builder().setDate(2021, 5 - 1, 9).build().getTime();

		// Go, go, go
		GraphQLObjectMapperTestClass test = objectMapper.readValue(
				"{\"date\":\"2021-05-09\", \"__typename\":\"GraphQLObjectMapperTestClass\"}",
				GraphQLObjectMapperTestClass.class);

		// Verification
		assertEquals(1, test.aliasParsedValues.keySet().size());
		assertEquals("2021-05-09", test.aliasParsedValues.get("date"), "The date is a regular json String");
		assertEquals(date, test.getCustomScalarValue("date", GraphQLScalarTypeDate.Date));
	}

	@Test
	void testEnum() throws JsonMappingException, JsonProcessingException {
		// Go, go, go
		GraphQLObjectMapperTestClass test = objectMapper.readValue(
				"{\"enum\":\"VALUE2\", \"__typename\":\"GraphQLObjectMapperTestClass\"}",
				GraphQLObjectMapperTestClass.class);

		// Verification
		assertEquals(1, test.aliasParsedValues.keySet().size());
		assertEquals(TestEnum.VALUE2, test.getEnumValue("enum", TestEnum.class));
	}

	@Test
	void testFloat() throws JsonMappingException, JsonProcessingException {
		// Go, go, go
		GraphQLObjectMapperTestClass test = objectMapper.readValue(
				"{\"double\":123.1, \"__typename\":\"GraphQLObjectMapperTestClass\"}",
				GraphQLObjectMapperTestClass.class);

		// Verification
		assertEquals(1, test.aliasParsedValues.keySet().size());
		assertEquals(123.1, test.aliasParsedValues.get("double"));
	}

	@Test
	void testInt() throws JsonMappingException, JsonProcessingException {
		// Go, go, go
		GraphQLObjectMapperTestClass test = objectMapper.readValue(
				"{\"int\":666, \"__typename\":\"GraphQLObjectMapperTestClass\"}", GraphQLObjectMapperTestClass.class);

		// Verification
		assertEquals(1, test.aliasParsedValues.keySet().size());
		assertEquals(666, test.aliasParsedValues.get("int"));
	}

	@Test
	void testListEmpty() throws JsonMappingException, JsonProcessingException {
		// Go, go, go
		GraphQLObjectMapperTestClass test = objectMapper.readValue(
				"{\"list\":[], \"__typename\":\"GraphQLObjectMapperTestClass\"}", GraphQLObjectMapperTestClass.class);

		// Verification
		assertEquals(1, test.aliasParsedValues.keySet().size());
		assertTrue(test.aliasParsedValues.get("list") instanceof List);
		assertEquals(0, ((List<?>) test.aliasParsedValues.get("list")).size());
	}

	@Test
	void testListOneString() throws JsonMappingException, JsonProcessingException {
		// Go, go, go
		GraphQLObjectMapperTestClass test = objectMapper.readValue(
				"{\"list\":[\"my str\"], \"__typename\":\"GraphQLObjectMapperTestClass\"}",
				GraphQLObjectMapperTestClass.class);

		// Verification
		assertEquals(1, test.aliasParsedValues.keySet().size());
		assertTrue(test.aliasParsedValues.get("list") instanceof List);
		assertEquals(1, ((List<?>) test.aliasParsedValues.get("list")).size());
		assertTrue(((List<?>) test.aliasParsedValues.get("list")).get(0) instanceof String);
		assertEquals("my str", ((List<?>) test.aliasParsedValues.get("list")).get(0));
	}

	@Test
	void testListOneObject() throws JsonMappingException, JsonProcessingException {
		// Go, go, go
		GraphQLObjectMapperTestClass test = objectMapper.readValue(""//
				+ "{"//
				+ "\"list\":[{\"theProperty\":\"the str value\", \"__typename\":\"GraphQLObjectMapperTestClass\"}], "
				+ "\"__typename\":\"GraphQLObjectMapperTestClass\""//
				+ "}", GraphQLObjectMapperTestClass.class);

		// Verification
		assertEquals(1, test.aliasParsedValues.keySet().size());
		assertTrue(test.aliasParsedValues.get("list") instanceof List);
		assertEquals(1, ((List<?>) test.aliasParsedValues.get("list")).size());
		assertTrue(((List<?>) test.aliasParsedValues.get("list")).get(0) instanceof GraphQLObjectMapperTestClass);
		GraphQLObjectMapperTestClass verif = (GraphQLObjectMapperTestClass) ((List<?>) test.aliasParsedValues
				.get("list")).get(0);
		assertEquals("the str value", verif.theProperty);
		assertEquals("GraphQLObjectMapperTestClass", verif.__typename);
	}

	@Test
	void testListTwoEnums() throws JsonMappingException, JsonProcessingException {
		// Go, go, go
		GraphQLObjectMapperTestClass test = objectMapper.readValue(""//
				+ "{"//
				+ "\"list\":[\"VALUE1\", \"VALUE3\"], " + "\"__typename\":\"GraphQLObjectMapperTestClass\""//
				+ "}", GraphQLObjectMapperTestClass.class);

		// Verification
		assertEquals(1, test.aliasParsedValues.keySet().size());
		assertTrue(test.aliasParsedValues.get("list") instanceof List);
		assertEquals(2, ((List<?>) test.aliasParsedValues.get("list")).size());
		assertEquals(TestEnum.VALUE1, ((List<?>) test.getEnumValue("list", TestEnum.class)).get(0));
		assertEquals(TestEnum.VALUE3, ((List<?>) test.aliasParsedValues.get("list")).get(1));
	}

	@Test
	void testListTwoCustomScalars() throws JsonMappingException, JsonProcessingException {
		// Preparation
		Date date1 = new Calendar.Builder().setDate(2021, 5 - 1, 9).build().getTime();
		Date date2 = new Calendar.Builder().setDate(2021, 5 - 1, 10).build().getTime();

		// Go, go, go
		GraphQLObjectMapperTestClass test = objectMapper.readValue(""//
				+ "{"//
				+ "\"list\":[\"2021-05-09\", \"2021-05-10\"], " + "\"__typename\":\"GraphQLObjectMapperTestClass\""//
				+ "}", GraphQLObjectMapperTestClass.class);

		// Verification
		assertEquals(1, test.aliasParsedValues.keySet().size());
		assertTrue(test.aliasParsedValues.get("list") instanceof List);
		assertEquals(2, ((List<?>) test.aliasParsedValues.get("list")).size());
		assertEquals(date1, ((List<?>) test.aliasParsedValues.get("list")).get(0));
		assertEquals(date2, ((List<?>) test.aliasParsedValues.get("list")).get(1));
	}

	@Test
	void testListTwoItems() throws JsonMappingException, JsonProcessingException {
		// Go, go, go
		GraphQLObjectMapperTestClass test = objectMapper.readValue(
				"{\"list\":[\"my str1\", \"my str2\"], \"__typename\":\"GraphQLObjectMapperTestClass\"}",
				GraphQLObjectMapperTestClass.class);

		// Verification
		assertEquals(1, test.aliasParsedValues.keySet().size());
		assertTrue(test.aliasParsedValues.get("list") instanceof List);
		//
		assertEquals(2, ((List<?>) test.aliasParsedValues.get("list")).size());
		//
		assertTrue(((List<?>) test.aliasParsedValues.get("list")).get(0) instanceof String);
		assertEquals("my str1", ((List<?>) test.aliasParsedValues.get("list")).get(0));
		//
		assertTrue(((List<?>) test.aliasParsedValues.get("list")).get(1) instanceof String);
		assertEquals("my str2", ((List<?>) test.aliasParsedValues.get("list")).get(1));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testListList() throws JsonMappingException, JsonProcessingException {
		// Go, go, go
		GraphQLObjectMapperTestClass test = objectMapper.readValue(
				"{\"list\":[[\"my str1\", \"my str2\"],[],[\"my str3\"]], \"__typename\":\"GraphQLObjectMapperTestClass\"}",
				GraphQLObjectMapperTestClass.class);

		// Verification
		assertEquals(1, test.aliasParsedValues.keySet().size());
		assertTrue(test.aliasParsedValues.get("list") instanceof List);
		//
		assertEquals(3, ((List<?>) test.aliasParsedValues.get("list")).size(), "3 sublists");
		//
		List<String> list = ((List<List<String>>) test.aliasParsedValues.get("list")).get(0);
		assertEquals(2, list.size());
		assertEquals("my str1", list.get(0));
		assertEquals("my str2", list.get(1));
		//
		list = ((List<List<String>>) test.aliasParsedValues.get("list")).get(1);
		assertEquals(0, list.size());
		//
		list = ((List<List<String>>) test.aliasParsedValues.get("list")).get(2);
		assertEquals(1, list.size());
		assertEquals("my str3", list.get(0));
	}

	@Test
	void testObject() throws JsonMappingException, JsonProcessingException {
		// Go, go, go
		GraphQLObjectMapperTestClass test = objectMapper.readValue(""//
				+ "{"//
				+ "  \"theProperty\":\"the root str value\", "//
				+ "  \"anObject\": {\"theProperty\":\"the alias's str value\", \"__typename\":\"GraphQLObjectMapperTestClass\"},"
				+ "  \"__typename\":\"GraphQLObjectMapperTestClass\" " //
				+ "}", GraphQLObjectMapperTestClass.class);

		// Verification
		assertEquals("the root str value", test.theProperty);
		assertEquals("GraphQLObjectMapperTestClass", test.__typename);

		assertEquals(1, test.aliasParsedValues.keySet().size());
		assertTrue(test.aliasParsedValues.get("anObject") instanceof GraphQLObjectMapperTestClass);
		GraphQLObjectMapperTestClass verif = (GraphQLObjectMapperTestClass) test.aliasParsedValues.get("anObject");
		assertEquals("the alias's str value", verif.theProperty);
		assertEquals("GraphQLObjectMapperTestClass", verif.__typename);
	}

	@Test
	void testString() throws JsonMappingException, JsonProcessingException {
		// Go, go, go
		GraphQLObjectMapperTestClass test = objectMapper.readValue(
				"{\"str\":\"a String\", \"__typename\":\"GraphQLObjectMapperTestClass\"}",
				GraphQLObjectMapperTestClass.class);

		// Verification
		assertEquals(1, test.aliasParsedValues.keySet().size());
		assertEquals("a String", test.aliasParsedValues.get("str"));
	}

}
