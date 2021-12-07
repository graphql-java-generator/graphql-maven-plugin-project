package com.graphql_java_generator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.graphql_java_generator.customscalars.CustomScalarRegistryImpl;
import com.graphql_java_generator.customscalars.GraphQLScalarTypeDate;
import com.graphql_java_generator.customscalars.GraphQLScalarTypeIDClient;
import com.graphql_java_generator.customscalars.GraphQLScalarTypeIDServer;
import com.graphql_java_generator.domain.client.forum.TopicInput;
import com.graphql_java_generator.domain.client.forum.TopicPostInput;
import com.graphql_java_generator.domain.client.starwars.Episode;
import com.graphql_java_generator.domain.client.starwars.Human;
import com.graphql_java_generator.testcases.Isssue49AccountInput;
import com.graphql_java_generator.testcases.Issue49Title;

import graphql.scalars.ExtendedScalars;

@Execution(ExecutionMode.CONCURRENT)
class GraphqlUtilsTest {

	GraphqlUtils graphqlUtils = new GraphqlUtils();

	/** A test class for {@link GraphqlUtilsTest#test_addImports()} */
	public class AnInnerClass {
		int dummy;
	}

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
	public void test_addImports() {
		Set<String> imports = new TreeSet<>();

		// Same package
		graphqlUtils.addImport(imports, getClass().getPackage().getName(), getClass().getName());
		assertEquals(0, imports.size(), "Same package: import not added");

		// java.lang
		graphqlUtils.addImport(imports, getClass().getPackage().getName(), java.lang.String.class.getName());
		assertEquals(0, imports.size(), "java.lang: import not added");

		// java.util
		graphqlUtils.addImport(imports, getClass().getPackage().getName(), java.util.Date.class.getName());
		assertEquals(1, imports.size(), "java.util: import added");
		assertEquals("java.util.Date", imports.toArray(new String[0])[0]);

		imports = new TreeSet<>();
		graphqlUtils.addImport(imports, "another.target.package", AnInnerClass.class.getName());
		assertEquals(1, imports.size(), "import added");
		assertEquals("com.graphql_java_generator.util.GraphqlUtilsTest.AnInnerClass",
				imports.toArray(new String[0])[0]);

		imports = new TreeSet<>();
		graphqlUtils.addImport(imports, "another.target.package", Type.class.getName());
		assertEquals(1, imports.size(), "import added");
		assertEquals("com.fasterxml.jackson.annotation.JsonSubTypes.Type", imports.toArray(new String[0])[0]);
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
		//
		// And we need to register the custom scalar
		CustomScalarRegistryImpl.customScalarRegistry.registerGraphQLScalarType(GraphQLScalarTypeIDClient.ID,
				String.class);
		CustomScalarRegistryImpl.customScalarRegistry.registerGraphQLScalarType(GraphQLScalarTypeDate.Date, Date.class);

		// Go, go, go
		com.graphql_java_generator.domain.client.forum.TopicInput topicInput = (com.graphql_java_generator.domain.client.forum.TopicInput) graphqlUtils
				.getArgument(map, "TopicInput", String.class.getName(),
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
		//
		// And we need to register the custom scalar
		CustomScalarRegistryImpl.customScalarRegistry.registerGraphQLScalarType(GraphQLScalarTypeIDServer.ID,
				String.class);
		CustomScalarRegistryImpl.customScalarRegistry.registerGraphQLScalarType(ExtendedScalars.GraphQLLong,
				Long.class);

		// Go, go, go
		com.graphql_java_generator.domain.server.allGraphQLCases.AllFieldCasesInput topicInput = (com.graphql_java_generator.domain.server.allGraphQLCases.AllFieldCasesInput) graphqlUtils
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

		//
		// And we need to register the custom scalar
		CustomScalarRegistryImpl.customScalarRegistry.registerGraphQLScalarType(GraphQLScalarTypeIDServer.ID,
				String.class);
		CustomScalarRegistryImpl.customScalarRegistry.registerGraphQLScalarType(ExtendedScalars.GraphQLLong,
				Long.class);

		// Go, go, go
		com.graphql_java_generator.domain.client.forum.TopicInput topicInput = (com.graphql_java_generator.domain.client.forum.TopicInput) graphqlUtils
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
		//
		// And we need to register the custom scalar
		CustomScalarRegistryImpl.customScalarRegistry.registerGraphQLScalarType(GraphQLScalarTypeDate.Date, Date.class);

		// Go, go, go
		com.graphql_java_generator.domain.server.allGraphQLCases.FieldParameterInput input = (com.graphql_java_generator.domain.server.allGraphQLCases.FieldParameterInput) graphqlUtils
				.getArgument(map, "FieldParameterInput", UUID.class.getName(),
						com.graphql_java_generator.domain.server.allGraphQLCases.FieldParameterInput.class);

		// Verification
		assertTrue(input.getUppercase());
		assertEquals(new GregorianCalendar(2345, 2 - 1, 24).getTime(), input.getDate());
	}

	@Test
	void test_getArgument_scalar() {
		assertEquals("33", graphqlUtils.getArgument("33", "String", "not used", String.class));

		assertEquals(UUID.fromString("00000000-0000-0000-0000-000002000003"),
				graphqlUtils.getArgument("00000000-0000-0000-0000-000002000003", "ID", "java.util.UUID", UUID.class));
		assertEquals("00000000-0000-0000-0000-000002000003", graphqlUtils
				.getArgument("00000000-0000-0000-0000-000002000003", "ID", "java.lang.String", String.class));
		assertEquals((long) 2000003, graphqlUtils.getArgument("00000002000003", "ID", "java.lang.Long", Long.class));

		assertEquals((long) 2, graphqlUtils.getArgument("2", "Int", "not used", Long.class));
		assertEquals(22, graphqlUtils.getArgument("22", "Int", "not used", Integer.class));

		assertEquals((float) 1.234, graphqlUtils.getArgument("1.234", "Int", "not used", Float.class));
		assertEquals(2.456, graphqlUtils.getArgument("2.456", "Int", "not used", Double.class));

		assertEquals(true, graphqlUtils.getArgument("true", "Boolean", "not used", Boolean.class));
		assertEquals(false, graphqlUtils.getArgument("false", "Boolean", "not used", Boolean.class));
	}

	@Test
	void test_getArgument_Enum() {
		// Preparation
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("title", "MRS");

		// Go, go, go
		Isssue49AccountInput input = (Isssue49AccountInput) graphqlUtils.getArgument(map, "Isssue49AccountInput",
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
		Human human = (Human) graphqlUtils.getArgument(map, "Human", UUID.class.getName(), Human.class);

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
		//
		// And we need to register the custom scalar
		CustomScalarRegistryImpl.customScalarRegistry.registerGraphQLScalarType(GraphQLScalarTypeIDClient.ID,
				String.class);

		// Go, go, go
		com.graphql_java_generator.domain.server.allGraphQLCases.AllFieldCasesInput input = (com.graphql_java_generator.domain.server.allGraphQLCases.AllFieldCasesInput) graphqlUtils
				.getArgument(mapAllFieldCasesWithIdSubtypeInput, "AllFieldCasesInput", UUID.class.getName(),
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
		TopicInput topicInput = (TopicInput) graphqlUtils.getArgument(map, "TopicInput", UUID.class.getName(),
				TopicInput.class);

		// Verification
		assertNull(topicInput.getBoardId());
		assertNull(topicInput.getInput());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getArgument_nullMap() {
		assertNull(graphqlUtils.getArgument(null, "TopicInput", UUID.class.getName(), TopicInput.class),
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
		//
		// And we need to register the custom scalar
		CustomScalarRegistryImpl.customScalarRegistry.registerGraphQLScalarType(GraphQLScalarTypeIDClient.ID,
				String.class);
		CustomScalarRegistryImpl.customScalarRegistry.registerGraphQLScalarType(GraphQLScalarTypeDate.Date, Date.class);

		List<Map<String, Object>> list = new ArrayList<>();
		list.add(map1);
		list.add(map2);

		// Go, go, go
		@SuppressWarnings("unchecked")
		List<com.graphql_java_generator.domain.client.forum.TopicInput> result = (List<com.graphql_java_generator.domain.client.forum.TopicInput>) graphqlUtils
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

	// @Test
	// void test_graphqlEncodeString() {
	// assertEquals("No escape characters. Some accents: èéàù",
	// graphqlUtils.graphqlEncodeString("No escape characters. Some accents: èéàù"), "no escape characters");
	//
	// assertEquals("Simple escape chars: \\\" \\\\ \\b \\f \\n \\r \\t",
	// graphqlUtils.graphqlEncodeString("Simple escape chars: \" \\ \b \f \n \r \t"),
	// "simple escape characters");
	//
	// assertEquals("Double escaped chars: \\\\\\\"\\\\ \\\\\\\\ \\\\\\b \\\\\\\"\\f \\n \\r \\t",
	// graphqlUtils.graphqlEncodeString("Double escaped chars: \\\"\\ \\\\ \\\b \\\"\f \n \r \t"),
	// "Double escaped characters");
	//
	// String test = "Simple escape chars: \" \\ \b \f \n \r \t";
	// assertEquals(test, graphqlUtils.graphqlDeencodeString(graphqlUtils.graphqlEncodeString(test)), "encode/decode");
	//
	// test = "Double escaped chars: \\\"\\ \\\\ \\\b \\\"\f \n \r \t";
	// assertEquals(test, graphqlUtils.graphqlDeencodeString(graphqlUtils.graphqlEncodeString(test)), "encode/decode");
	// }
	//
	// @Test
	// void test_graphqlDeencodeString() {
	// assertEquals("No escape characters. Some accents: èéàù",
	// graphqlUtils.graphqlDeencodeString("No escape characters. Some accents: èéàù"), "no escape characters");
	//
	// assertEquals("Simple escape chars: \" \\ \b \f \n \r \t",
	// graphqlUtils.graphqlDeencodeString("Simple escape chars: \\\" \\\\ \\b \\f \\n \\r \\t"),
	// "simple escape characters");
	//
	// assertEquals("Double escaped chars: \\\"\\ \\\\ \\\b \\\"\f \n \r \t",
	// graphqlUtils.graphqlDeencodeString(
	// "Double escaped chars: \\\\\\\"\\\\ \\\\\\\\ \\\\\\b \\\\\\\"\\f \\n \\r \\t"),
	// "simple escape characters");
	// }
}
