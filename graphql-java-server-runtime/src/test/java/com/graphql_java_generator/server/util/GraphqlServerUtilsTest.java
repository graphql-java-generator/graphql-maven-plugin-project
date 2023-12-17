package com.graphql_java_generator.server.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.domain.client.forum.TopicInput;
import com.graphql_java_generator.domain.server.allGraphQLCases.EnumWithReservedJavaKeywordAsValues;
import com.graphql_java_generator.domain.server.allGraphQLCases.Episode;
import com.graphql_java_generator.domain.server.allGraphQLCases.Human;
import com.graphql_java_generator.server.util.test_classes.AUnion;
import com.graphql_java_generator.server.util.test_classes.AnEnumType;
import com.graphql_java_generator.server.util.test_classes.AnInputType;
import com.graphql_java_generator.server.util.test_classes.AnInterface;
import com.graphql_java_generator.server.util.test_classes.AnObjectType;
import com.graphql_java_generator.testcases.Isssue49AccountInput;
import com.graphql_java_generator.testcases.Issue49Title;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class GraphqlServerUtilsTest {

	GraphqlServerUtils graphqlServerUtils = new GraphqlServerUtils();

	@GraphQLInputType("ATestClass")
	public static class ClassToTest_mapArgumentToRelevantPojoOrScalar {
		@GraphQLScalar(fieldName = "test", graphQLTypeSimpleName = "String", javaClass = java.lang.String.class, listDepth = 0)
		String test;

		@GraphQLScalar(fieldName = "date", graphQLTypeSimpleName = "Date", javaClass = Date.class, listDepth = 0)
		Date date;

		@GraphQLScalar(fieldName = "Node", graphQLTypeSimpleName = "JSON", javaClass = ObjectNode.class, listDepth = 0)
		ObjectNode node;

		@GraphQLScalar(fieldName = "o", graphQLTypeSimpleName = "Object", javaClass = Object.class, listDepth = 0)
		Object o;

		@GraphQLScalar(fieldName = "list", graphQLTypeSimpleName = "ClassToTest_mapArgumentToRelevantPojoOrScalar", javaClass = ClassToTest_mapArgumentToRelevantPojoOrScalar.class, listDepth = 2)
		List<List<ClassToTest_mapArgumentToRelevantPojoOrScalar>> list;

		public ClassToTest_mapArgumentToRelevantPojoOrScalar() {
		}

		public ClassToTest_mapArgumentToRelevantPojoOrScalar(String test) {
			this.test = test;
		}

		public void setTest(String test) {
			this.test = test;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public void setNode(ObjectNode node) {
			this.node = node;
		}

		public void setO(Object o) {
			this.o = o;
		}

		public void setList(List<List<ClassToTest_mapArgumentToRelevantPojoOrScalar>> list) {
			this.list = list;
		}
	}

	@Test
	void testClassNameExtractor() {
		assertEquals("TheEnumName", this.graphqlServerUtils.classNameExtractor(AnEnumType.class));
		assertEquals("TheInterfaceName", this.graphqlServerUtils.classNameExtractor(AnInterface.class));
		assertEquals("TheObjectName", this.graphqlServerUtils.classNameExtractor(AnObjectType.class));
		assertEquals("TheUnionName", this.graphqlServerUtils.classNameExtractor(AUnion.class));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getArgument_forum() {
		// Preparation
		Map<String, Object> mapTopicPostInput = new LinkedHashMap<>();
		mapTopicPostInput.put("authorId", "00000000-0000-0000-0000-000000000003");
		mapTopicPostInput.put("date", new GregorianCalendar(2009, 11 - 1, 20).getTime());
		mapTopicPostInput.put("publiclyAvailable", true);
		mapTopicPostInput.put("title", "The good title");
		mapTopicPostInput.put("content", "Some content");
		//
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("boardId", "00000000-0000-0000-0000-000000000004");
		map.put("input", mapTopicPostInput);

		// Go, go, go
		com.graphql_java_generator.domain.client.forum.TopicInput topicInput = (com.graphql_java_generator.domain.client.forum.TopicInput) //
		this.graphqlServerUtils.getArgument(map, "TopicInput", String.class.getName(),
				com.graphql_java_generator.domain.client.forum.TopicInput.class);

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
	void test_getArgument_serverMode_UUIDID() {
		// Preparation
		List<String> comments = new ArrayList<>();
		comments.add("comment1");
		comments.add("comment2");

		List<Boolean> booleans = new ArrayList<>();
		booleans.add(true);
		booleans.add(false);

		Map<String, Object> withoutIdSubtype1 = new LinkedHashMap<>();
		withoutIdSubtype1.put("name", "subname1");
		Map<String, Object> withoutIdSubtype2 = new LinkedHashMap<>();
		withoutIdSubtype2.put("name", "subname2");
		List<Object> withoutIdSubtypes = new ArrayList<>();
		withoutIdSubtypes.add(withoutIdSubtype1);
		withoutIdSubtypes.add(withoutIdSubtype2);

		Map<String, Object> map = new LinkedHashMap<>();
		map.put("id", "00000000-0000-0000-0000-000000000003");
		map.put("name", "name");
		map.put("forname", "forname");
		map.put("age", Long.MAX_VALUE);
		map.put("nbComments", -1);
		map.put("comments", comments);
		map.put("booleans", booleans);
		map.put("withoutIdSubtype", withoutIdSubtypes);

		// Go, go, go
		com.graphql_java_generator.domain.server.allGraphQLCases.AllFieldCasesInput topicInput = (com.graphql_java_generator.domain.server.allGraphQLCases.AllFieldCasesInput) this.graphqlServerUtils
				.getArgument(map, "AllFieldCasesInput", UUID.class.getName(),
						com.graphql_java_generator.domain.server.allGraphQLCases.AllFieldCasesInput.class);

		// Verification
		assertEquals("00000000-0000-0000-0000-000000000003", topicInput.getId().toString());
		assertEquals("name", topicInput.getName());
		assertEquals("forname", topicInput.getForname());
		assertEquals(Long.MAX_VALUE, topicInput.getAge());
		assertEquals(-1, topicInput.getNbComments());
		// comments
		assertEquals(2, topicInput.getComments().size());
		assertEquals("comment1", topicInput.getComments().get(0));
		assertEquals("comment2", topicInput.getComments().get(1));
		// Booleans
		assertEquals(2, topicInput.getBooleans().size());
		assertEquals(true, topicInput.getBooleans().get(0));
		assertEquals(false, topicInput.getBooleans().get(1));
		// Booleans
		assertEquals(2, topicInput.getWithoutIdSubtype().size());
		assertEquals("subname1", topicInput.getWithoutIdSubtype().get(0).getName());
		assertEquals("subname2", topicInput.getWithoutIdSubtype().get(1).getName());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getArgument_serverMode_StringID() {
		// Preparation
		List<String> comments = new ArrayList<>();
		comments.add("comment1");
		comments.add("comment2");

		List<Boolean> booleans = new ArrayList<>();
		booleans.add(true);
		booleans.add(false);

		Map<String, Object> withoutIdSubtype1 = new LinkedHashMap<>();
		withoutIdSubtype1.put("name", "subname1");
		Map<String, Object> withoutIdSubtype2 = new LinkedHashMap<>();
		withoutIdSubtype2.put("name", "subname2");
		List<Object> withoutIdSubtypes = new ArrayList<>();
		withoutIdSubtypes.add(withoutIdSubtype1);
		withoutIdSubtypes.add(withoutIdSubtype2);

		Map<String, Object> map = new LinkedHashMap<>();
		map.put("boardId", "00000000-0000-0000-0000-000000000003");

		// Go, go, go
		com.graphql_java_generator.domain.client.forum.TopicInput topicInput = (com.graphql_java_generator.domain.client.forum.TopicInput) this.graphqlServerUtils
				.getArgument(map, "AllFieldCasesInput", String.class.getName(),
						com.graphql_java_generator.domain.client.forum.TopicInput.class);

		// Verification
		assertEquals("00000000-0000-0000-0000-000000000003", topicInput.getBoardId());
	}

	@Test
	void test_getArgument_CustomScalar() {
		// Preparation
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("uppercase", true);
		map.put("date", new GregorianCalendar(2345, 2 - 1, 24).getTime());

		// Go, go, go
		com.graphql_java_generator.domain.server.allGraphQLCases.FieldParameterInput input = (com.graphql_java_generator.domain.server.allGraphQLCases.FieldParameterInput) this.graphqlServerUtils
				.getArgument(map, "FieldParameterInput", UUID.class.getName(),
						com.graphql_java_generator.domain.server.allGraphQLCases.FieldParameterInput.class);

		// Verification
		assertTrue(input.getUppercase());
		assertEquals(new GregorianCalendar(2345, 2 - 1, 24).getTime(), input.getDate());
	}

	@Test
	void test_getArgument_scalar() {
		assertEquals("33", this.graphqlServerUtils.getArgument("33", "String", "not used", String.class));

		assertEquals(UUID.fromString("00000000-0000-0000-0000-000002000003"), this.graphqlServerUtils
				.getArgument("00000000-0000-0000-0000-000002000003", "ID", "java.util.UUID", UUID.class));
		assertEquals("00000000-0000-0000-0000-000002000003", this.graphqlServerUtils
				.getArgument("00000000-0000-0000-0000-000002000003", "ID", "java.lang.String", String.class));
		assertEquals((long) 2000003,
				this.graphqlServerUtils.getArgument("00000002000003", "ID", "java.lang.Long", Long.class));

		assertEquals((long) 2, this.graphqlServerUtils.getArgument("2", "Int", "not used", Long.class));
		assertEquals(22, this.graphqlServerUtils.getArgument("22", "Int", "not used", Integer.class));

		assertEquals((float) 1.234, this.graphqlServerUtils.getArgument("1.234", "Int", "not used", Float.class));
		assertEquals(2.456, this.graphqlServerUtils.getArgument("2.456", "Int", "not used", Double.class));

		assertEquals(true, this.graphqlServerUtils.getArgument("true", "Boolean", "not used", Boolean.class));
		assertEquals(false, this.graphqlServerUtils.getArgument("false", "Boolean", "not used", Boolean.class));
	}

	@Test
	void test_getArgument_Enum() {
		// Preparation
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("title", "MRS");

		// Go, go, go
		Isssue49AccountInput input = (Isssue49AccountInput) this.graphqlServerUtils.getArgument(map,
				"Isssue49AccountInput", UUID.class.getName(), Isssue49AccountInput.class);

		// Verification
		assertEquals(Issue49Title.MRS, input.getTitle());
	}

	@Test
	void test_getArgument_ListOfEnum() {
		// Preparation
		List<String> episodes = new ArrayList<>();
		episodes.add(Episode.JEDI.toString());
		episodes.add(Episode.EMPIRE.toString());
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("appearsIn", episodes);

		// Go, go, go
		Human human = (Human) this.graphqlServerUtils.getArgument(map, "Human", UUID.class.getName(), Human.class);

		// Verification
		assertEquals(2, human.getAppearsIn().size());
		assertTrue(human.getAppearsIn().contains(Episode.JEDI));
		assertTrue(human.getAppearsIn().contains(Episode.EMPIRE));
	}

	@Test
	void test_getArgument_ListOfInputTypes() {
		// Preparation
		//
		Map<String, Object> map1 = new LinkedHashMap<>();
		map1.put("id", "00000000-0000-0000-0000-000000000001");
		map1.put("name", "name1");
		//
		Map<String, Object> map2 = new LinkedHashMap<>();
		map2.put("id", "00000000-0000-0000-0000-000000000002");
		map2.put("name", "name2");
		//
		List<Map<String, Object>> listAllFieldCasesWithIdSubtypeInputs = new ArrayList<>();
		listAllFieldCasesWithIdSubtypeInputs.add(map1);
		listAllFieldCasesWithIdSubtypeInputs.add(map2);
		//
		Map<String, Object> mapAllFieldCasesWithIdSubtypeInput = new LinkedHashMap<>();
		mapAllFieldCasesWithIdSubtypeInput.put("withIdSubtype", listAllFieldCasesWithIdSubtypeInputs);

		// Go, go, go
		com.graphql_java_generator.domain.server.allGraphQLCases.AllFieldCasesInput input = (com.graphql_java_generator.domain.server.allGraphQLCases.AllFieldCasesInput) //
		this.graphqlServerUtils.getArgument(mapAllFieldCasesWithIdSubtypeInput, "AllFieldCasesInput",
				UUID.class.getName(),
				com.graphql_java_generator.domain.server.allGraphQLCases.AllFieldCasesInput.class);

		// Verification
		assertEquals(2, input.getWithIdSubtype().size());
		assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), input.getWithIdSubtype().get(0).getId());
		assertEquals("name1", input.getWithIdSubtype().get(0).getName());
		assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000002"), input.getWithIdSubtype().get(1).getId());
		assertEquals("name2", input.getWithIdSubtype().get(1).getName());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getInput_emptyMap() {
		// Preparation
		Map<String, Object> map = new LinkedHashMap<>();

		// Go, go, go
		TopicInput topicInput = (TopicInput) this.graphqlServerUtils.getArgument(map, "TopicInput",
				UUID.class.getName(), TopicInput.class);

		// Verification
		assertNull(topicInput.getBoardId());
		assertNull(topicInput.getInput());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getArgument_nullMap() {
		assertNull(this.graphqlServerUtils.getArgument(null, "TopicInput", UUID.class.getName(), TopicInput.class),
				"A null map return a null object");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getArguments_list() {
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
		//
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
		@SuppressWarnings("unchecked")
		List<com.graphql_java_generator.domain.client.forum.TopicInput> result = (List<com.graphql_java_generator.domain.client.forum.TopicInput>) this.graphqlServerUtils
				.getArgument(list, "TopicInput", String.class.getName(),
						com.graphql_java_generator.domain.client.forum.TopicInput.class);

		// Preparation
		com.graphql_java_generator.domain.client.forum.TopicInput topicInput = result.get(0);
		assertEquals("00000000-0000-0000-0000-000000000004", topicInput.getBoardId());
		assertEquals("00000000-0000-0000-0000-000000000003", topicInput.getInput().getAuthorId());
		assertEquals("Some content", topicInput.getInput().getContent());
		assertEquals(new GregorianCalendar(2009, 11 - 1, 20).getTime(), topicInput.getInput().getDate());
		assertEquals(true, topicInput.getInput().getPubliclyAvailable());
		assertEquals("The good title", topicInput.getInput().getTitle());

		topicInput = result.get(1);
		assertEquals("00000000-0000-0000-0000-000000000005", topicInput.getBoardId());
		assertEquals("00000000-0000-0000-0000-000000000006", topicInput.getInput().getAuthorId());
		assertEquals("Some content (2)", topicInput.getInput().getContent());
		assertEquals(new GregorianCalendar(2009, 11 - 1, 25).getTime(), topicInput.getInput().getDate());
		assertEquals(false, topicInput.getInput().getPubliclyAvailable());
		assertEquals("The good title (2)", topicInput.getInput().getTitle());
	}

	@SuppressWarnings("unchecked")
	@Test
	void test_enumValueToString() {
		///////////////////////////////////////////////////////////////////////////////////////////////////
		// Basic case: not a list
		assertNull(this.graphqlServerUtils.enumValueToString(null));
		assertEquals("if", this.graphqlServerUtils.enumValueToString(EnumWithReservedJavaKeywordAsValues._if));

		///////////////////////////////////////////////////////////////////////////////////////////////////
		// List of enums
		assertArrayEquals(//
				ArrayUtils.toArray("assert", null, "break"),
				((List<String>) this.graphqlServerUtils.enumValueToString(Arrays.asList(//
						EnumWithReservedJavaKeywordAsValues._assert, //
						null, //
						EnumWithReservedJavaKeywordAsValues._break))).toArray());
		assertArrayEquals(//
				ArrayUtils.toArray(Optional.of("assert"), Optional.empty(), Optional.of("break")), //
				((List<Optional<String>>) this.graphqlServerUtils.enumValueToString(Arrays.asList(//
						Optional.of(EnumWithReservedJavaKeywordAsValues._assert), //
						Optional.empty(), //
						Optional.of(EnumWithReservedJavaKeywordAsValues._break)))).toArray());

		///////////////////////////////////////////////////////////////////////////////////////////////////
		// List of lists
		List<List<String>> expected1 = Arrays.asList(//
				Arrays.asList("assert", null, "break"), //
				null, //
				Arrays.asList("catch", null, "else"));
		List<List<String>> actual1 = (List<List<String>>) this.graphqlServerUtils.enumValueToString(Arrays.asList(//
				Arrays.asList(EnumWithReservedJavaKeywordAsValues._assert, null,
						EnumWithReservedJavaKeywordAsValues._break), //
				null, //
				Arrays.asList(EnumWithReservedJavaKeywordAsValues._catch, null,
						EnumWithReservedJavaKeywordAsValues._else)));
		assertEquals(expected1.size(), actual1.size());
		for (int i = 0; i < expected1.size(); i += 1) {
			assertIterableEquals(expected1.get(i), actual1.get(i), "Comparison of item " + i);
		} // for

		List<Optional<List<String>>> expected2 = Arrays.asList(//
				Optional.of(Arrays.asList("assert", null, "break")), //
				Optional.empty(), //
				Optional.of(Arrays.asList("catch", null, "else")));
		List<Optional<List<String>>> actual2 = (List<Optional<List<String>>>) this.graphqlServerUtils
				.enumValueToString(Arrays.asList(//
						Optional.of(Arrays.asList(EnumWithReservedJavaKeywordAsValues._assert, null,
								EnumWithReservedJavaKeywordAsValues._break)), //
						Optional.empty(), //
						Optional.of(Arrays.asList(EnumWithReservedJavaKeywordAsValues._catch, null,
								EnumWithReservedJavaKeywordAsValues._else))));
		assertEquals(expected2.size(), actual2.size());
		for (int i = 0; i < expected2.size(); i += 1) {
			Optional<List<String>> o1 = expected2.get(i);
			Optional<List<String>> o2 = actual2.get(i);
			if (o1.isPresent()) {
				assertTrue(o2.isPresent());
				assertIterableEquals(o1.get(), o2.get(), "Comparison of item " + i);
			} else {
				assertFalse(o2.isPresent());
			}
		} // for

		///////////////////////////////////////////////////////////////////////////////////////////////////
		// Flux
		Object fluxEnum = this.graphqlServerUtils.enumValueToString(Flux.just(//
				EnumWithReservedJavaKeywordAsValues._double, //
				EnumWithReservedJavaKeywordAsValues._long));
		assertInstanceOf(Flux.class, fluxEnum);
		StepVerifier//
				.create((Flux<String>) fluxEnum)//
				.expectNext("double")//
				.expectNext("long")//
				.expectComplete()//
				.verify();

		Object fluxOptionalEnum = this.graphqlServerUtils.enumValueToString(Flux.just(//
				Optional.of(EnumWithReservedJavaKeywordAsValues._double), //
				Optional.empty(), //
				Optional.of(EnumWithReservedJavaKeywordAsValues._long)));
		assertInstanceOf(Flux.class, fluxOptionalEnum);
		StepVerifier//
				.create((Flux<Optional<String>>) fluxOptionalEnum)//
				.expectNext(Optional.of("double"))//
				.expectNext(Optional.empty())//
				.expectNext(Optional.of("long"))//
				.expectComplete()//
				.verify();

		Object fluxListEnum = this.graphqlServerUtils.enumValueToString(Flux.just(//
				Arrays.asList(EnumWithReservedJavaKeywordAsValues._double, //
						EnumWithReservedJavaKeywordAsValues._long), //
				Arrays.asList(), //
				Arrays.asList(EnumWithReservedJavaKeywordAsValues._continue, //
						EnumWithReservedJavaKeywordAsValues._void)//
		));
		assertInstanceOf(Flux.class, fluxListEnum);
		StepVerifier//
				.create((Flux<List<String>>) fluxListEnum)//
				.expectNext(Arrays.asList("double", "long"))//
				.expectNext(Arrays.asList())//
				.expectNext(Arrays.asList("continue", "void"))//
				.expectComplete()//
				.verify();
	}

	@SuppressWarnings("unchecked")
	@Test
	void test_mapArgumentToRelevantPojoOrScalar() throws JsonMappingException, JsonProcessingException {
		final String ELEMENT = "an element";
		final String ARGUMENT = "an argument";
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode objectNode;
		String json;
		Map<Object, Object> map;

		//////////////////////////////////////////////////////////////////
		// Null value
		assertNull(this.graphqlServerUtils.mapArgumentToRelevantPojoOrScalar(null, ObjectNode.class, 4, ELEMENT,
				ARGUMENT));

		//////////////////////////////////////////////////////////////////
		// Enum value
		String scalarString = AnEnumType.VALUE1.name();
		assertEquals(//
				AnEnumType.VALUE1, //
				this.graphqlServerUtils.mapArgumentToRelevantPojoOrScalar(scalarString, AnEnumType.class, 0, ELEMENT,
						ARGUMENT));

		//////////////////////////////////////////////////////////////////
		// Enum field
		map = new HashMap<>();
		map.put("enumField", AnEnumType.VALUE1.toString());
		Object response = this.graphqlServerUtils.mapArgumentToRelevantPojoOrScalar(map, AnInputType.class, 0, ELEMENT,
				ARGUMENT);
		assertTrue(response instanceof AnInputType, response.getClass().getName());
		assertEquals(AnEnumType.VALUE1, ((AnInputType) response).enumField);

		//////////////////////////////////////////////////////////////////
		// An ObjectNode object
		json = "{\"field\":\"value\",\"subObject\":{\"field2\":[1,2,3],\"field3\":[1.1,22.2,3.3]},\"booleans\":[true,false]}";
		map = objectMapper.readValue(json, Map.class);
		objectNode = (ObjectNode) this.graphqlServerUtils.mapArgumentToRelevantPojoOrScalar(map, ObjectNode.class, 0,
				ELEMENT, ARGUMENT);
		assertNotNull(objectNode);
		assertEquals(json, objectNode.toString());

		//////////////////////////////////////////////////////////////////
		// A list of null values
		List<Object> arrayOfNulls = Arrays.asList(null, null);
		List<ObjectNode> objectNodes = (List<ObjectNode>) this.graphqlServerUtils
				.mapArgumentToRelevantPojoOrScalar(arrayOfNulls, ObjectNode.class, 1, ELEMENT, ARGUMENT);
		assertNotNull(objectNodes);
		assertEquals(2, objectNodes.size());
		assertNull(objectNodes.get(0));
		assertNull(objectNodes.get(1));

		//////////////////////////////////////////////////////////////////
		// A list of ObjectNode objects
		String json1 = "{\"field2\":\"value\",\"subObject1\":{\"field2\":[1,2,3],\"field3\":[1.1,22.2,3.3]},\"booleans\":[true,false]}";
		String json2 = "{\"field1\":\"value\",\"subObject2\":{\"field2\":[1,2,3],\"field3\":[1.1,22.2,3.3]},\"booleans\":[true,false]}";
		Map<?, ?> map1 = objectMapper.readValue(json1, Map.class);
		Map<?, ?> map2 = objectMapper.readValue(json2, Map.class);
		List<Object> list = Arrays.asList(map1, map2);
		objectNodes = (List<ObjectNode>) this.graphqlServerUtils.mapArgumentToRelevantPojoOrScalar(list,
				ObjectNode.class, 1, ELEMENT, ARGUMENT);
		assertNotNull(objectNodes);
		assertEquals(2, objectNodes.size());
		assertEquals(json1, objectNodes.get(0).toString());
		assertEquals(json2, objectNodes.get(1).toString());

		//////////////////////////////////////////////////////////////////
		// A list of lists of ObjectNode objects
		list = Arrays.asList(Arrays.asList(map1, map2));
		List<List<ObjectNode>> objectNodesNodes = (List<List<ObjectNode>>) this.graphqlServerUtils
				.mapArgumentToRelevantPojoOrScalar(list, ObjectNode.class, 2, ELEMENT, ARGUMENT);
		assertNotNull(objectNodesNodes);
		assertEquals(1, objectNodesNodes.size());
		assertEquals(2, objectNodesNodes.get(0).size());
		assertEquals(json1, objectNodesNodes.get(0).get(0).toString());
		assertEquals(json2, objectNodesNodes.get(0).get(1).toString());

		//////////////////////////////////////////////////////////////////
		// Test with a fake InputType
		map = new HashMap<>();
		map.put("test", "a test value");
		Date date = Calendar.getInstance().getTime();
		map.put("date", date);
		map.put("node", objectMapper.readValue(
				"{\"field\":\"value\",\"subObject\":{\"field2\":[1,2,3],\"field3\":[1.1,22.2,3.3]},\"booleans\":[true,false]}",
				Map.class));
		map.put("o", this);
		map.put("list", Arrays.asList(//
				Arrays.asList(new ClassToTest_mapArgumentToRelevantPojoOrScalar("item1"),
						new ClassToTest_mapArgumentToRelevantPojoOrScalar("item2")),
				Arrays.asList(new ClassToTest_mapArgumentToRelevantPojoOrScalar("item3"),
						new ClassToTest_mapArgumentToRelevantPojoOrScalar("item4"))));
		//
		ClassToTest_mapArgumentToRelevantPojoOrScalar test = (ClassToTest_mapArgumentToRelevantPojoOrScalar) this.graphqlServerUtils
				.mapArgumentToRelevantPojoOrScalar(map, ClassToTest_mapArgumentToRelevantPojoOrScalar.class, 0, ELEMENT,
						ARGUMENT);
		//
		assertEquals("a test value", test.test);
		assertEquals(date, test.date);
		assertEquals(
				"{\"field\":\"value\",\"subObject\":{\"field2\":[1,2,3],\"field3\":[1.1,22.2,3.3]},\"booleans\":[true,false]}",
				test.node.toString());
		assertEquals(2, test.list.size());
		assertEquals(2, test.list.get(0).size());
		assertEquals(2, test.list.get(1).size());
		assertEquals("item1", test.list.get(0).get(0).test);
		assertEquals("item2", test.list.get(0).get(1).test);
		assertEquals("item3", test.list.get(1).get(0).test);
		assertEquals("item4", test.list.get(1).get(1).test);

		//////////////////////////////////////////////////////////////////
		// Test with a list of fake InputType
		list = Arrays.asList(map, map);
		List<ClassToTest_mapArgumentToRelevantPojoOrScalar> testList = (List<ClassToTest_mapArgumentToRelevantPojoOrScalar>) this.graphqlServerUtils
				.mapArgumentToRelevantPojoOrScalar(list, ClassToTest_mapArgumentToRelevantPojoOrScalar.class, 1,
						ELEMENT, ARGUMENT);
		assertEquals(2, testList.size());
		assertEquals("a test value", testList.get(0).test);
		assertEquals("a test value", testList.get(0).test);
	}
}