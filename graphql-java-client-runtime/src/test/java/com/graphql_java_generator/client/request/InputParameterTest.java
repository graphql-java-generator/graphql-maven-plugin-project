package com.graphql_java_generator.client.request;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.graphql_java_generator.client.request.InputParameter.InputParameterType;
import com.graphql_java_generator.customscalars.GraphQLScalarTypeDate;
import com.graphql_java_generator.domain.client.allGraphQLCases.Episode;
import com.graphql_java_generator.domain.client.allGraphQLCases.InputWithJson;
import com.graphql_java_generator.domain.client.allGraphQLCases.InputWithObject;
import com.graphql_java_generator.domain.client.allGraphQLCases.RegistriesInitializer;
import com.graphql_java_generator.domain.client.forum.PostInput;
import com.graphql_java_generator.domain.client.forum.TopicPostInput;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

import graphql.scalars.ExtendedScalars;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import tools.jackson.core.JacksonException;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@Execution(ExecutionMode.CONCURRENT)
class InputParameterTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_InputParameter() {
		String name = "aName";
		String value = "a Value";
		InputParameter param = InputParameter.newHardCodedParameter("AllGraphQLCases", name, value, "String", false, 0,
				false);
		assertEquals(name, param.getName(), "name");
		assertEquals(value, param.getDefaultValue(), "value");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getValueForGraphqlQuery_str() throws GraphQLRequestExecutionException {
		String name = "aName";
		String value = "This is a string with two \"\", a 🎉 and some \r \t \\ to be escaped (and a literal tab here: '	')";
		InputParameter param = InputParameter.newHardCodedParameter("AllGraphQLCases", name, value, "String", false, 0,
				false);

		assertEquals(name, param.getName(), "name");
		assertEquals(value, param.getDefaultValue(), "value");
		assertEquals(
				"\"This is a string with two \\\"\\\", a \\uD83C\\uDF89 and some \\r \\t \\\\ to be escaped (and a literal tab here: '\\t')\"",
				param.getStringContentForGraphqlQuery(false, null), "escaped value");
		assertEquals('"' + value.replace("	", "\t") + '"',
				StringEscapeUtils.unescapeJson(param.getStringContentForGraphqlQuery(false, null)),
				"roundtripped value");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getValueForGraphqlQuery_doubleQuoteAfterEscapedTrailingAntiSlash()
			throws GraphQLRequestExecutionException {
		String name = "aName";
		String value = "A double quote after an escaped antislash: \\\" (it's not the end of string)";
		InputParameter param = InputParameter.newHardCodedParameter("AllGraphQLCases", name, value, "String", false, 0,
				false);

		assertEquals(name, param.getName(), "name");
		assertEquals(value, param.getDefaultValue(), "value");
		assertEquals("\"A double quote after an escaped antislash: \\\\\\\" (it's not the end of string)\"",
				param.getStringContentForGraphqlQuery(false, null), "escaped value");
		assertEquals('"' + value + '"',
				StringEscapeUtils.unescapeJson(param.getStringContentForGraphqlQuery(false, null)),
				"roundtripped value");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getValueForGraphqlQuery_oneTrailingAntiSlash() throws GraphQLRequestExecutionException {
		String name = "aName";
		String value = "One trailing antislash: \\";
		InputParameter param = InputParameter.newHardCodedParameter("AllGraphQLCases", name, value, "String", false, 0,
				false);

		assertEquals(name, param.getName(), "name");
		assertEquals(value, param.getDefaultValue(), "value");
		assertEquals("\"One trailing antislash: \\\\\"", param.getStringContentForGraphqlQuery(false, null),
				"escaped value");
		assertEquals('"' + value + '"',
				StringEscapeUtils.unescapeJson(param.getStringContentForGraphqlQuery(false, null)),
				"roundtripped value");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getValueForGraphqlQuery_twoTrailingAntiSlahes() throws GraphQLRequestExecutionException {
		String name = "aName";
		String value = "One trailing antislash: \\\\";
		InputParameter param = InputParameter.newHardCodedParameter("AllGraphQLCases", name, value, "String", false, 0,
				false);

		assertEquals(name, param.getName(), "name");
		assertEquals(value, param.getDefaultValue(), "value");
		assertEquals("\"One trailing antislash: \\\\\\\\\"", param.getStringContentForGraphqlQuery(false, null),
				"escaped value");
		assertEquals('"' + value + '"',
				StringEscapeUtils.unescapeJson(param.getStringContentForGraphqlQuery(false, null)),
				"roundtripped value");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getValueForGraphqlQuery_enum() throws GraphQLRequestExecutionException {
		String name = "aName";
		Episode value = Episode.EMPIRE;
		InputParameter param = InputParameter.newHardCodedParameter("AllGraphQLCases", name, value, "Episode", false, 0,
				false);

		assertEquals(name, param.getName(), "name");
		assertEquals(value, param.getDefaultValue(), "value");
		assertEquals("EMPIRE", param.getStringContentForGraphqlQuery(false, new HashMap<>()));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getValueForGraphqlQuery_int() throws GraphQLRequestExecutionException {
		String name = "aName";
		Integer value = 666;
		InputParameter param = InputParameter.newHardCodedParameter("AllGraphQLCases", name, value, "Int", false, 0,
				false);

		assertEquals(name, param.getName(), "name");
		assertEquals(value, param.getDefaultValue(), "value");
		assertEquals(value.toString(), param.getStringContentForGraphqlQuery(false, new HashMap<>()));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getValueForGraphqlQuery_Float() throws GraphQLRequestExecutionException {
		String name = "aName";
		Float value = (float) 666.666;
		InputParameter param = InputParameter.newHardCodedParameter("AllGraphQLCases", name, value, "Float", false, 0,
				false);

		assertEquals(name, param.getName(), "name");
		assertEquals(value, param.getDefaultValue(), "value");
		assertEquals(value.toString(), param.getStringContentForGraphqlQuery(false, new HashMap<>()));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getValueForGraphqlQuery_UUID() throws GraphQLRequestExecutionException {
		String name = "aName";
		UUID id = UUID.fromString("00000000-0000-0000-0000-000000000012");
		InputParameter param = InputParameter.newHardCodedParameter("AllGraphQLCases", name, id, "ID", false, 0, false);

		assertEquals(name, param.getName(), "name");
		assertEquals(id, param.getDefaultValue(), "value");
		assertEquals("\"00000000-0000-0000-0000-000000000012\"",
				param.getStringContentForGraphqlQuery(false, new HashMap<>()), "escaped value");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getValueForGraphqlQuery_byteArray()
			throws GraphQLRequestExecutionException, UnsupportedEncodingException {
		// Preparation
		RegistriesInitializer.initializeAllRegistries();
		//
		String name = "aName";
		String str = "This a string with some special characters éàëöô";
		byte[] bytes = str.getBytes("UTF-8");

		// Go, go, go
		InputParameter param = InputParameter.newHardCodedParameter("AllGraphQLCases", name, bytes, "Base64String",
				false, 0, false);

		// Verification
		assertEquals(name, param.getName(), "name");
		assertEquals(bytes, param.getDefaultValue(), "value");
		assertEquals("\"" + Base64.getEncoder().encodeToString(bytes) + "\"",
				param.getStringContentForGraphqlQuery(false, new HashMap<>()), "base 64");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getValueForGraphqlQuery_recursive_InputType() throws GraphQLRequestExecutionException {
		// Preparation
		TopicPostInput topicPostInput = new TopicPostInput();
		topicPostInput.setAuthorId("00000000-0000-0000-0000-000000000012");
		topicPostInput.setContent("Some other content");
		topicPostInput.setDate(new GregorianCalendar(2009, 11 - 1, 21).getTime());
		topicPostInput.setPubliclyAvailable(false);
		topicPostInput.setTitle("The good title for a post");

		PostInput postInput = new PostInput();
		postInput.setTopicId("00000000-0000-0000-0000-000000000022");
		postInput.setInput(topicPostInput);

		String name = "anotherName";
		InputParameter param = InputParameter.newHardCodedParameter("AllGraphQLCases", name, postInput, "PostInput",
				false, 0, false);

		// Verification
		assertEquals(
				"{topicId:\"00000000-0000-0000-0000-000000000022\",input:{authorId:\"00000000-0000-0000-0000-000000000012\",date:\"2009-11-21\",publiclyAvailable:false,title:\"The good title for a post\",content:\"Some other content\"}}",
				param.getStringContentForGraphqlQuery(false, postInput, 0, null, false));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getValueForGraphqlQuery_ListEmptyString() throws GraphQLRequestExecutionException {
		String name = "anotherName";
		List<String> values = new ArrayList<>();
		InputParameter param = InputParameter.newHardCodedParameter("AllGraphQLCases", name, values, "String", false, 1,
				false);

		assertEquals(name, param.getName(), "name");
		assertEquals(values, param.getDefaultValue(), "value");
		assertEquals("[]", param.getStringContentForGraphqlQuery(false, new HashMap<>()));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getValueForGraphqlQuery_ListEmptyStringKO() throws GraphQLRequestExecutionException {
		String name = "anotherName";
		String values = "A string, but should be a list";
		InputParameter inputParameter = InputParameter.newHardCodedParameter("", name, values, "String", false, 1,
				false);
		GraphQLRequestExecutionException ex = assertThrows(GraphQLRequestExecutionException.class,
				() -> inputParameter.getStringContentForGraphqlQuery(false, null));
		assertTrue(ex.getMessage().contains("parameter  '" + name + "'"), "The error message is: " + ex.getMessage());
		assertTrue(ex.getMessage().contains("it should be either a java.lang.List or an Array"),
				"The error message is: " + ex.getMessage());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getValueForGraphqlQuery_ListString() throws GraphQLRequestExecutionException {
		String name = "anotherName";
		String value1 = "first value";
		String value2 = "second value";
		String value3 = "third value";
		List<String> values = new ArrayList<>();
		values.add(value1);
		values.add(value2);
		values.add(value3);
		InputParameter param = InputParameter.newHardCodedParameter("AllGraphQLCases", name, values, "String", false, 1,
				false);

		assertEquals(name, param.getName(), "name");
		assertEquals(values, param.getDefaultValue(), "value");
		assertEquals("[\"" + value1 + "\",\"" + value2 + "\",\"" + value3 + "\"]",
				param.getStringContentForGraphqlQuery(false, new HashMap<>()));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getValueForGraphqlQuery_ListEpisode() throws GraphQLRequestExecutionException {
		String name = "anotherName";
		Episode value1 = Episode.EMPIRE;
		Episode value2 = Episode.JEDI;
		Episode value3 = Episode.NEWHOPE;
		List<Episode> values = new ArrayList<>();
		values.add(value1);
		values.add(value2);
		values.add(value3);
		InputParameter param = InputParameter.newHardCodedParameter("AllGraphQLCases", name, values, "Episode", false,
				1, false);

		assertEquals(name, param.getName(), "name");
		assertEquals(values, param.getDefaultValue(), "value");
		assertEquals("[EMPIRE,JEDI,NEWHOPE]", param.getStringContentForGraphqlQuery(false, new HashMap<>()));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void getValueForGraphqlQuery_MandatoryBindVariable_OK() throws GraphQLRequestExecutionException {
		String name = "aName";
		String bindParameterName = "variableName";
		InputParameter mandatoryBindParam = InputParameter.newBindParameter("AllGraphQLCases", name, bindParameterName,
				InputParameterType.MANDATORY, "Int", false, 0, false);

		assertEquals(name, mandatoryBindParam.getName(), "name");
		assertEquals(null, mandatoryBindParam.getDefaultValue(), "value");
		assertEquals(bindParameterName, mandatoryBindParam.bindParameterName, "bindParameterName");
		assertThrows(GraphQLRequestExecutionException.class,
				() -> mandatoryBindParam.getStringContentForGraphqlQuery(false, null), "escaped value (null map)");
		assertThrows(GraphQLRequestExecutionException.class,
				() -> mandatoryBindParam.getStringContentForGraphqlQuery(false, new HashMap<>()),
				"escaped value (empty map)");

		Map<String, Object> bindVariablesValues = new HashMap<>();
		bindVariablesValues.put("anotherBind", "A value");
		bindVariablesValues.put(bindParameterName, 666);
		assertEquals("666", mandatoryBindParam.getStringContentForGraphqlQuery(false, bindVariablesValues),
				"escaped value (correct map)");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void getValueForGraphqlQuery_OptionalBindVariable_OK() throws GraphQLRequestExecutionException {
		String name = "aName";
		String bindParameterName = "variableName";
		InputParameter mandatoryBindParam = InputParameter.newBindParameter("AllGraphQLCases", name, bindParameterName,
				InputParameterType.OPTIONAL, "Int", false, 0, false);

		assertEquals(name, mandatoryBindParam.getName(), "name");
		assertEquals(null, mandatoryBindParam.getDefaultValue(), "value");
		assertEquals(bindParameterName, mandatoryBindParam.bindParameterName, "bindParameterName");
		assertNull(mandatoryBindParam.getStringContentForGraphqlQuery(false, null), "with no given map");
		assertNull(mandatoryBindParam.getStringContentForGraphqlQuery(false, new HashMap<>()),
				"escaped value (empty map)");

		Map<String, Object> bindVariablesValues = new HashMap<>();
		bindVariablesValues.put("anotherBind", "A value");
		bindVariablesValues.put(bindParameterName, 666);
		assertEquals("666", mandatoryBindParam.getStringContentForGraphqlQuery(false, bindVariablesValues),
				"escaped value (correct map)");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void getValueForGraphqlQuery_BindParameter_CustomScalar_Date_OK() throws GraphQLRequestExecutionException {
		RegistriesInitializer.initializeAllRegistries();
		String name = "aName";
		String bindParameterName = "variableName";
		InputParameter customScalarInputParameter = InputParameter.newBindParameter("AllGraphQLCases", name,
				bindParameterName, InputParameterType.OPTIONAL, GraphQLScalarTypeDate.Date.getName(), false, 0, false);

		Map<String, Object> badValues = new HashMap<>();
		badValues.put("variableName", "A bad date");
		CoercingSerializeException e = assertThrows(CoercingSerializeException.class,
				() -> customScalarInputParameter.getStringContentForGraphqlQuery(false, badValues));
		assertTrue(e.getMessage().contains("A bad date"));

		@SuppressWarnings("deprecation")
		Date date = new Date(2020 - 1900, 01 - 1, 19); // Years starts at 1900. Month is between 0 and 11
		Map<String, Object> goodValues = new HashMap<>();
		goodValues.put("variableName", date);

		assertEquals("\"2020-01-19\"", customScalarInputParameter.getStringContentForGraphqlQuery(false, goodValues));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void getValueForGraphqlQuery_BindParameter_CustomScalar_Long_OK() throws GraphQLRequestExecutionException {
		RegistriesInitializer.initializeAllRegistries();

		GraphQLScalarType graphQLScalarTypeLong = ExtendedScalars.GraphQLLong;
		String name = "aName";
		String bindParameterName = "variableName";
		InputParameter customScalarInputParameter = InputParameter.newBindParameter("AllGraphQLCases", name,
				bindParameterName, InputParameterType.OPTIONAL, graphQLScalarTypeLong.getName(), false, 0, false);

		Map<String, Object> badValues = new HashMap<>();
		badValues.put("variableName", "A bad long");
		CoercingSerializeException e = assertThrows(CoercingSerializeException.class,
				() -> customScalarInputParameter.getStringContentForGraphqlQuery(false, badValues));
		assertTrue(e.getMessage().contains("Long"));

		Long l = Long.MAX_VALUE;
		String ls = ((Long) Long.MAX_VALUE).toString();
		Map<String, Object> goodValues = new HashMap<>();
		goodValues.put("variableName", l);

		assertEquals(ls, customScalarInputParameter.getStringContentForGraphqlQuery(false, goodValues));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void getValueForGraphqlQuery_BindParameter_InputType_CustomScalar_Date_OK()
			throws GraphQLRequestExecutionException {
		// Given
		RegistriesInitializer.initializeAllRegistries();

		String name = "aName";
		String bindParameterName = "variableName";

		PostInput postInput = new PostInput();
		postInput.setFrom(getDateFromDifferentFormat("01-01-2020"));
		postInput.setIn(asList(getDateFromDifferentFormat("01-02-2020"), getDateFromDifferentFormat("01-03-2020")));

		InputParameter inputTypeInputParameter = InputParameter.newBindParameter("AllGraphQLCases", name,
				bindParameterName, InputParameterType.OPTIONAL, "PostInput", false, 0, false);

		Map<String, Object> parameters = new HashMap<>();
		parameters.put(bindParameterName, postInput);

		// When
		assertEquals("{from:\"2020-01-01\",in:[\"2020-02-01\",\"2020-03-01\"]}",
				inputTypeInputParameter.getStringContentForGraphqlQuery(false, parameters));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void getValueForGraphqlQuery_GraphQLVariable_InputType_CustomScalar_Date_OK()
			throws GraphQLRequestExecutionException {
		// Preparation
		RegistriesInitializer.initializeAllRegistries();
		TopicPostInput topicPostInput = TopicPostInput.builder().withAuthorId("12")
				.withDate(new GregorianCalendar(2021, 3 - 1, 13).getTime()).withPubliclyAvailable(true)
				.withTitle("a title").withContent("some content").build();
		PostInput inputPost = PostInput.builder().withTopicId("22").withInput(topicPostInput).build();
		InputParameter inputTypeInputParameter = InputParameter.newBindParameter("AllGraphQLCases", "name",
				"bindParameterName", InputParameterType.GRAPHQL_VARIABLE, "PostInput", false, 0, false);
		Map<String, Object> params = new HashMap<>();
		params.put("bindParameterName", inputPost);

		// Go, go, go
		String result = inputTypeInputParameter.getStringContentForGraphqlQuery(true, params);

		// Verification
		String expected = "{\"topicId\":\"22\",\"input\":{\"authorId\":\"12\",\"date\":\"2021-03-13\",\"publiclyAvailable\":true,\"title\":\"a title\",\"content\":\"some content\"}}";
		assertEquals(expected, result);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void getValueForGraphqlQuery_InputTypeWithJsonField()
			throws StreamReadException, JacksonException, GraphQLRequestExecutionException {
		// Preparation
		RegistriesInitializer.initializeAllRegistries();// Schema AllGraphQLCases
		ObjectNode json = new ObjectMapper().readValue(
				"{\"field\":\"value\", \"subObject\": {\"field2\" : [1,2,3], \"field3\" : [1.1,22.2,3.3]} ,  \"booleans\" : [true , false]}",
				ObjectNode.class);
		InputWithJson input = InputWithJson.builder()//
				.withTest("getValueForGraphqlQuery_InputTypeWithJsonField")//
				.withJson(json)//
				.withJsons(Arrays.asList(json, json))//
				.build();
		InputParameter inputTypeInputParameter = InputParameter.newBindParameter("AllGraphQLCases", "name",
				"bindParameterName", InputParameterType.MANDATORY, "InputWithObject", false, 0, false);
		Map<String, Object> params = new HashMap<>();
		params.put("bindParameterName", input);

		// Go, go, go
		String result = inputTypeInputParameter.getStringContentForGraphqlQuery(false, params);

		// Verification
		assertEquals("{"//
				+ "test:\"getValueForGraphqlQuery_InputTypeWithJsonField\","
				+ "json:{field:\"value\",subObject:{field2:[1,2,3],field3:[1.1,22.2,3.3]},booleans:[true,false]},"
				+ "jsons:["//
				+ "{field:\"value\",subObject:{field2:[1,2,3],field3:[1.1,22.2,3.3]},booleans:[true,false]},"
				+ "{field:\"value\",subObject:{field2:[1,2,3],field3:[1.1,22.2,3.3]},booleans:[true,false]}" + "]}",
				result);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void getValueForGraphqlQuery_InputTypeWithObjectField()
			throws StreamReadException, JacksonException, GraphQLRequestExecutionException {
		// Preparation
		Map<?, ?> map = new ObjectMapper().readValue(
				"{\"field\":\"value\", \"subObject\": {\"field2\" : [1,2,3], \"field3\" : [1.1,22.2,3.3]} ,  \"booleans\" : [true , false]}",
				HashMap.class);
		RegistriesInitializer.initializeAllRegistries();
		InputWithObject input = InputWithObject.builder()//
				.withTest("getValueForGraphqlQuery_InputTypeWithObjectField")//
				.withObject(map)//
				.withObjects(Arrays.asList(map, map))//
				.build();
		InputParameter inputTypeInputParameter = InputParameter.newBindParameter("AllGraphQLCases", "name",
				"bindParameterName", InputParameterType.MANDATORY, "InputWithObject", false, 0, false);
		Map<String, Object> params = new HashMap<>();
		params.put("bindParameterName", input);

		// Go, go, go
		String result = inputTypeInputParameter.getStringContentForGraphqlQuery(false, params);

		// Verification
		assertEquals("{"//
				+ "test:\"getValueForGraphqlQuery_InputTypeWithObjectField\","
				+ "object:{subObject:{field2:[1,2,3],field3:[1.1,22.2,3.3]},field:\"value\",booleans:[true,false]},"
				+ "objects:["//
				+ "{subObject:{field2:[1,2,3],field3:[1.1,22.2,3.3]},field:\"value\",booleans:[true,false]},"
				+ "{subObject:{field2:[1,2,3],field3:[1.1,22.2,3.3]},field:\"value\",booleans:[true,false]}" + "]}",
				result);
	}

	private Date getDateFromDifferentFormat(String dateInString) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		try {
			return formatter.parse(dateInString);
		} catch (ParseException e) {
			Assertions.fail("Invalid date format.");
			return null;
		}
	}

}
