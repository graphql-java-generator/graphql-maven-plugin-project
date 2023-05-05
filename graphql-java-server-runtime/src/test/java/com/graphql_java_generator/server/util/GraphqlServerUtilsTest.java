package com.graphql_java_generator.server.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.graphql_java_generator.domain.client.forum.TopicInput;
import com.graphql_java_generator.domain.server.allGraphQLCases.EnumWithReservedJavaKeywordAsValues;
import com.graphql_java_generator.domain.server.allGraphQLCases.Episode;
import com.graphql_java_generator.domain.server.allGraphQLCases.Human;
import com.graphql_java_generator.server.util.test_classes.AUnion;
import com.graphql_java_generator.server.util.test_classes.AnEnumType;
import com.graphql_java_generator.server.util.test_classes.AnInterface;
import com.graphql_java_generator.server.util.test_classes.AnObjectType;
import com.graphql_java_generator.testcases.Isssue49AccountInput;
import com.graphql_java_generator.testcases.Issue49Title;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class GraphqlServerUtilsTest {

	GraphqlServerUtils graphqlServerUtils = new GraphqlServerUtils();

	@Test
	void testClassNameExtractor() {
		assertEquals("TheEnumName", graphqlServerUtils.classNameExtractor(AnEnumType.class));
		assertEquals("TheInterfaceName", graphqlServerUtils.classNameExtractor(AnInterface.class));
		assertEquals("TheObjectName", graphqlServerUtils.classNameExtractor(AnObjectType.class));
		assertEquals("TheUnionName", graphqlServerUtils.classNameExtractor(AUnion.class));
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
		graphqlServerUtils.getArgument(map, "TopicInput", String.class.getName(),
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
		com.graphql_java_generator.domain.server.allGraphQLCases.AllFieldCasesInput topicInput = (com.graphql_java_generator.domain.server.allGraphQLCases.AllFieldCasesInput) graphqlServerUtils
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
		com.graphql_java_generator.domain.client.forum.TopicInput topicInput = (com.graphql_java_generator.domain.client.forum.TopicInput) graphqlServerUtils
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
		com.graphql_java_generator.domain.server.allGraphQLCases.FieldParameterInput input = (com.graphql_java_generator.domain.server.allGraphQLCases.FieldParameterInput) graphqlServerUtils
				.getArgument(map, "FieldParameterInput", UUID.class.getName(),
						com.graphql_java_generator.domain.server.allGraphQLCases.FieldParameterInput.class);

		// Verification
		assertTrue(input.getUppercase());
		assertEquals(new GregorianCalendar(2345, 2 - 1, 24).getTime(), input.getDate());
	}

	@Test
	void test_getArgument_scalar() {
		assertEquals("33", graphqlServerUtils.getArgument("33", "String", "not used", String.class));

		assertEquals(UUID.fromString("00000000-0000-0000-0000-000002000003"), graphqlServerUtils
				.getArgument("00000000-0000-0000-0000-000002000003", "ID", "java.util.UUID", UUID.class));
		assertEquals("00000000-0000-0000-0000-000002000003", graphqlServerUtils
				.getArgument("00000000-0000-0000-0000-000002000003", "ID", "java.lang.String", String.class));
		assertEquals((long) 2000003,
				graphqlServerUtils.getArgument("00000002000003", "ID", "java.lang.Long", Long.class));

		assertEquals((long) 2, graphqlServerUtils.getArgument("2", "Int", "not used", Long.class));
		assertEquals(22, graphqlServerUtils.getArgument("22", "Int", "not used", Integer.class));

		assertEquals((float) 1.234, graphqlServerUtils.getArgument("1.234", "Int", "not used", Float.class));
		assertEquals(2.456, graphqlServerUtils.getArgument("2.456", "Int", "not used", Double.class));

		assertEquals(true, graphqlServerUtils.getArgument("true", "Boolean", "not used", Boolean.class));
		assertEquals(false, graphqlServerUtils.getArgument("false", "Boolean", "not used", Boolean.class));
	}

	@Test
	void test_getArgument_Enum() {
		// Preparation
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("title", "MRS");

		// Go, go, go
		Isssue49AccountInput input = (Isssue49AccountInput) graphqlServerUtils.getArgument(map, "Isssue49AccountInput",
				UUID.class.getName(), Isssue49AccountInput.class);

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
		Human human = (Human) graphqlServerUtils.getArgument(map, "Human", UUID.class.getName(), Human.class);

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
		graphqlServerUtils.getArgument(mapAllFieldCasesWithIdSubtypeInput, "AllFieldCasesInput", UUID.class.getName(),
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
		TopicInput topicInput = (TopicInput) graphqlServerUtils.getArgument(map, "TopicInput", UUID.class.getName(),
				TopicInput.class);

		// Verification
		assertNull(topicInput.getBoardId());
		assertNull(topicInput.getInput());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getArgument_nullMap() {
		assertNull(graphqlServerUtils.getArgument(null, "TopicInput", UUID.class.getName(), TopicInput.class),
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
		List<com.graphql_java_generator.domain.client.forum.TopicInput> result = (List<com.graphql_java_generator.domain.client.forum.TopicInput>) graphqlServerUtils
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
		assertNull(graphqlServerUtils.enumValueToString(null));
		assertEquals("if", graphqlServerUtils.enumValueToString(EnumWithReservedJavaKeywordAsValues._if));

		///////////////////////////////////////////////////////////////////////////////////////////////////
		// List of enums
		assertArrayEquals(//
				ArrayUtils.toArray("assert", null, "break"),
				((List<String>) graphqlServerUtils.enumValueToString(Arrays.asList(//
						EnumWithReservedJavaKeywordAsValues._assert, //
						null, //
						EnumWithReservedJavaKeywordAsValues._break))).toArray());
		assertArrayEquals(//
				ArrayUtils.toArray(Optional.of("assert"), Optional.empty(), Optional.of("break")), //
				((List<Optional<String>>) graphqlServerUtils.enumValueToString(Arrays.asList(//
						Optional.of(EnumWithReservedJavaKeywordAsValues._assert), //
						Optional.empty(), //
						Optional.of(EnumWithReservedJavaKeywordAsValues._break)))).toArray());

		///////////////////////////////////////////////////////////////////////////////////////////////////
		// List of lists
		List<List<String>> expected1 = Arrays.asList(//
				Arrays.asList("assert", null, "break"), //
				null, //
				Arrays.asList("catch", null, "else"));
		List<List<String>> actual1 = (List<List<String>>) graphqlServerUtils.enumValueToString(Arrays.asList(//
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
		List<Optional<List<String>>> actual2 = (List<Optional<List<String>>>) graphqlServerUtils
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
		Object fluxEnum = graphqlServerUtils.enumValueToString(Flux.just(//
				EnumWithReservedJavaKeywordAsValues._double, //
				EnumWithReservedJavaKeywordAsValues._long));
		assertInstanceOf(Flux.class, fluxEnum);
		StepVerifier//
				.create((Flux<String>) fluxEnum)//
				.expectNext("double")//
				.expectNext("long")//
				.expectComplete()//
				.verify();

		Object fluxOptionalEnum = graphqlServerUtils.enumValueToString(Flux.just(//
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

		Object fluxListEnum = graphqlServerUtils.enumValueToString(Flux.just(//
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
}
