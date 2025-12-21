package com.graphql_java_generator.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.graphql_java_generator.domain.client.allGraphQLCases.Character;
import com.graphql_java_generator.domain.client.allGraphQLCases.Droid;
import com.graphql_java_generator.domain.client.allGraphQLCases.Episode;
import com.graphql_java_generator.domain.client.allGraphQLCases.Human;
import com.graphql_java_generator.domain.client.allGraphQLCases.MyQueryType;
import com.graphql_java_generator.domain.client.allGraphQLCases.MyQueryTypeExecutorAllGraphQLCases;
import com.graphql_java_generator.domain.client.allGraphQLCases._break;
import com.graphql_java_generator.domain.client.allGraphQLCases._extends;
import com.graphql_java_generator.domain.client.forum.Post;
import com.graphql_java_generator.domain.client.forum.PostInput;
import com.graphql_java_generator.domain.client.starwars.scalar.ScalarTest;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.util.test_cases.GraphQLDirectiveTest_ClassCase;
import com.graphql_java_generator.util.test_cases.GraphQLDirectiveTest_InterfaceCase;

import graphql.schema.GraphQLScalarType;

@Execution(ExecutionMode.CONCURRENT)
class GraphqlClientUtilsTest {

	GraphqlClientUtils graphqlClientUtils;

	@BeforeAll
	static void beforeAll() {
		com.graphql_java_generator.domain.client.allGraphQLCases.RegistriesInitializer.initializeAllRegistries();
		com.graphql_java_generator.domain.client.forum.RegistriesInitializer.initializeAllRegistries();
	}

	@BeforeEach
	void setUp() throws Exception {
		graphqlClientUtils = new GraphqlClientUtils();
	}

	@Test
	void testCheckName() throws GraphQLRequestPreparationException {
		// Some valid name: we call check, and no exception should be thrown
		graphqlClientUtils.checkName("avalidname");
		graphqlClientUtils.checkName("aValidName");
		graphqlClientUtils.checkName("_aValidName");
		graphqlClientUtils.checkName("ValidName");

		// Various types of checks KO
		assertThrows(NullPointerException.class, () -> graphqlClientUtils.checkName(null));
		assertThrows(GraphQLRequestPreparationException.class, () -> graphqlClientUtils.checkName("qdqd qdsq"));
		assertThrows(GraphQLRequestPreparationException.class, () -> graphqlClientUtils.checkName("qdqd.qdsq"));
		assertThrows(GraphQLRequestPreparationException.class, () -> graphqlClientUtils.checkName("qdqdqdsq."));
		assertThrows(GraphQLRequestPreparationException.class, () -> graphqlClientUtils.checkName(".qdqdqdsq"));
		assertThrows(GraphQLRequestPreparationException.class, () -> graphqlClientUtils.checkName("qdqdqdsq*"));
		assertThrows(GraphQLRequestPreparationException.class, () -> graphqlClientUtils.checkName("qdqdqdsèq"));
	}

	@Test
	void test_checkIsScalar_field() throws NoSuchFieldException, SecurityException, GraphQLRequestPreparationException {

		assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkIsScalar(ScalarTest.class.getDeclaredField("episode"), false));
		graphqlClientUtils.checkIsScalar(ScalarTest.class.getDeclaredField("episode"), true);

		assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkIsScalar(Human.class.getDeclaredField("id"), false));
		graphqlClientUtils.checkIsScalar(Human.class.getDeclaredField("id"), true);

		assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkIsScalar(Human.class.getDeclaredField("name"), false));
		graphqlClientUtils.checkIsScalar(Human.class.getDeclaredField("name"), true);

		assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkIsScalar(Human.class.getDeclaredField("appearsIn"), false));
		graphqlClientUtils.checkIsScalar(Human.class.getDeclaredField("appearsIn"), true);

	}

	@Test
	void test_checkIsScalar_method()
			throws NoSuchMethodException, SecurityException, GraphQLRequestPreparationException {
		GraphQLRequestPreparationException e;

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkIsScalar("id", Character.class.getMethod("getId"), false));
		assertTrue(e.getMessage().contains("getId"), "getId");
		assertTrue(e.getMessage().contains("id"), "id");
		graphqlClientUtils.checkIsScalar("id", Character.class.getMethod("getId"), true);

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkIsScalar("name", Character.class.getMethod("getName"), false));
		assertTrue(e.getMessage().contains("getName"), "getName");
		assertTrue(e.getMessage().contains("name"), "name");
		graphqlClientUtils.checkIsScalar("name", Character.class.getMethod("getName"), true);

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkIsScalar("friends", Character.class.getMethod("getFriends"), true));
		assertTrue(e.getMessage().contains("getFriends"), "getFriends");
		assertTrue(e.getMessage().contains("friends"), "friends");
		graphqlClientUtils.checkIsScalar("friends", Character.class.getMethod("getFriends"), false);
	}

	@Test
	void checkFieldOfGraphQLType_Objets() throws GraphQLRequestPreparationException {
		GraphQLRequestPreparationException e;

		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Wrong field names, for interface and classes (shouldBeScalar : null)
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkFieldOfGraphQLType("wrong", null, Character.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkFieldOfGraphQLType("wrong", null, Droid.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkFieldOfGraphQLType("wrong", null, Human.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Wrong field names, for interface and classes (shouldBeScalar : false)
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkFieldOfGraphQLType("wrong", false, Character.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkFieldOfGraphQLType("wrong", false, Droid.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkFieldOfGraphQLType("wrong", false, Human.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Wrong field names, for interface and classes (shouldBeScalar : true)
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkFieldOfGraphQLType("wrong", true, Character.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkFieldOfGraphQLType("wrong", true, Droid.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkFieldOfGraphQLType("wrong", true, Human.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Correct field names (check with wrong scalar type, then correct scalar type). Variation for shouldBeScalar
		// (null or not)
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkFieldOfGraphQLType("id", false, Character.class));
		assertTrue(e.getMessage().contains("id"), "id");
		//
		assertEquals(String.class, graphqlClientUtils.checkFieldOfGraphQLType("id", null, Character.class),
				"id : scalar OK");
		assertEquals(String.class, graphqlClientUtils.checkFieldOfGraphQLType("id", true, Character.class),
				"id : scalar OK");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkFieldOfGraphQLType("id", false, Droid.class));
		assertTrue(e.getMessage().contains("id"), "id");
		//
		assertEquals(String.class, graphqlClientUtils.checkFieldOfGraphQLType("id", null, Droid.class),
				"id : scalar OK");
		assertEquals(String.class, graphqlClientUtils.checkFieldOfGraphQLType("id", true, Droid.class),
				"id : scalar OK");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkFieldOfGraphQLType("id", false, Human.class));
		assertTrue(e.getMessage().contains("id"), "id");
		//
		assertEquals(String.class, graphqlClientUtils.checkFieldOfGraphQLType("id", null, Human.class),
				"id : scalar OK");
		assertEquals(String.class, graphqlClientUtils.checkFieldOfGraphQLType("id", true, Human.class),
				"id : scalar OK");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkFieldOfGraphQLType("friends", true, Character.class));
		assertTrue(e.getMessage().contains("friends"), "friends");
		//
		assertEquals(String.class, graphqlClientUtils.checkFieldOfGraphQLType("id", null, Character.class),
				"id : scalar OK");
		assertEquals(String.class, graphqlClientUtils.checkFieldOfGraphQLType("id", true, Character.class),
				"id : scalar OK");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkFieldOfGraphQLType("friends", true, Droid.class));
		assertTrue(e.getMessage().contains("friends"), "friends");
		//
		assertEquals(String.class, graphqlClientUtils.checkFieldOfGraphQLType("id", null, Droid.class),
				"id : scalar OK");
		assertEquals(String.class, graphqlClientUtils.checkFieldOfGraphQLType("id", true, Droid.class),
				"id : scalar OK");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkFieldOfGraphQLType("friends", true, Human.class));
		assertTrue(e.getMessage().contains("friends"), "friends");
		//
		assertEquals(String.class, graphqlClientUtils.checkFieldOfGraphQLType("id", null, Human.class),
				"id : scalar OK");
		assertEquals(String.class, graphqlClientUtils.checkFieldOfGraphQLType("id", true, Human.class),
				"id : scalar OK");

	}

	@Test
	void checkFieldOfGraphQLType_Query() throws GraphQLRequestPreparationException {
		GraphQLRequestPreparationException e;

		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Wrong field name, for interface and classes (shouldBeScalar : null, true, false)
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkFieldOfGraphQLType("wrong", null, MyQueryType.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkFieldOfGraphQLType("wrong", true, MyQueryType.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkFieldOfGraphQLType("wrong", false, MyQueryType.class));
		assertTrue(e.getMessage().contains("wrong"), "wrong");

		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Correct field name, for interface and classes (shouldBeScalar : null, true, false)
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> graphqlClientUtils.checkFieldOfGraphQLType("hero", true, MyQueryType.class));
		assertTrue(e.getMessage().contains("hero"), "hero");

		assertEquals(Character.class,
				graphqlClientUtils.checkFieldOfGraphQLType("withOneMandatoryParam", null, MyQueryType.class),
				"withOneMandatoryParam : scalar null");
		assertEquals(Character.class,
				graphqlClientUtils.checkFieldOfGraphQLType("withOneMandatoryParam", false, MyQueryType.class),
				"withOneMandatoryParam : scalar false");

		// With a query that returns a scalar
		assertEquals(String.class, graphqlClientUtils.checkFieldOfGraphQLType("instanceof", null, MyQueryType.class),
				"instanceof: scalar OK");
		assertEquals(String.class, graphqlClientUtils.checkFieldOfGraphQLType("instanceof", true, MyQueryType.class),
				"instanceof: scalar OK");

	}

	@Test
	void test_generatesBindVariableValuesMap() throws GraphQLRequestExecutionException {
		Object[] objects = { "1", "2", "3" };
		Map<String, Object> map;

		map = graphqlClientUtils.generatesBindVariableValuesMap(null);
		assertNotNull(map);
		assertEquals(0, map.size(), "The map is empty");

		// Check that there is an even number of parameters
		assertThrows(GraphQLRequestExecutionException.class,
				() -> graphqlClientUtils.generatesBindVariableValuesMap(objects),
				"There must be an even number of parameters, in series 1");

		// Check ClassCastException
		Object[] objects2 = { "param1", 2, "param2", Episode.JEDI, 3, "a String" };
		assertThrows(ClassCastException.class, () -> graphqlClientUtils.generatesBindVariableValuesMap(objects2));

		// Check normal behavior
		Object[] objects3 = { "param1", 2, "param2", Episode.JEDI, "param3", "a String" };
		map = graphqlClientUtils.generatesBindVariableValuesMap(objects3);
		//
		assertEquals(3, map.size());
		assertEquals(2, map.get("param1"));
		assertEquals(Episode.JEDI, map.get("param2"));
		assertEquals("a String", map.get("param3"));
	}

	@Test
	void test_getGraphQLScalarType() throws Exception {

		// When
		Field field = Post.class.getDeclaredField("date");

		GraphQLScalarType graphQlScalarType = graphqlClientUtils.getGraphQLCustomScalarType(field, "AllGraphQLCases");

		// Then
		assertNotNull(graphQlScalarType);
	}

	@Test
	void test_getGraphQLScalarTypeGivenInputPojo() throws Exception {

		// When
		Field field = PostInput.class.getDeclaredField("from");

		GraphQLScalarType graphQlScalarType = graphqlClientUtils.getGraphQLCustomScalarType(field, "AllGraphQLCases");

		// Then
		assertNotNull(graphQlScalarType);
	}

	@Test
	void test_getGraphQLTypeNameFromClass() {
		// Enum
		assertEquals("Episode", graphqlClientUtils.getGraphQLTypeNameFromClass(Episode.class));
		// Interface
		assertEquals("Character", graphqlClientUtils.getGraphQLTypeNameFromClass(Character.class));
		// Object
		assertEquals("Human", graphqlClientUtils.getGraphQLTypeNameFromClass(Human.class));
		assertEquals("Droid", graphqlClientUtils.getGraphQLTypeNameFromClass(Droid.class));
		// Union
		assertEquals("Droid", graphqlClientUtils.getGraphQLTypeNameFromClass(Droid.class));

		// With a different name GraphQL and java
		assertEquals("break", graphqlClientUtils.getGraphQLTypeNameFromClass(_break.class));
		assertEquals("extends", graphqlClientUtils.getGraphQLTypeNameFromClass(_extends.class));

	}

	@Test
	public void test_getClass() {
		// Creating a MyQueryTypeExecutorMySchema is mandatory to initialize the GraphQLTypeMappingRegistry
		new MyQueryTypeExecutorAllGraphQLCases();

		String packageName = "com.graphql_java_generator.domain.client.allGraphQLCases";

		assertEquals("java.lang.Integer",
				graphqlClientUtils.getClass(packageName, "Integer", "AllGraphQLCases").getName());
		assertEquals("com.graphql_java_generator.domain.client.allGraphQLCases.Human",
				graphqlClientUtils.getClass(packageName, "Human", "AllGraphQLCases").getName());
		assertEquals("java.util.Date", graphqlClientUtils.getClass(packageName, "Date", "AllGraphQLCases").getName());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_getDirectiveParameters() throws NoSuchFieldException, SecurityException, NoSuchMethodException {
		Map<String, String> map;
		RuntimeException e;

		//////////////////////////////////////////////////////////////////////////////////////////////
		// Error checks
		e = assertThrows(RuntimeException.class, () -> graphqlClientUtils
				.getDirectiveParameters(GraphQLDirectiveTest_ClassCase.class, null, "does not exist"));
		assertTrue(e.getMessage().contains("No directive of name \"does not exist\""));

		e = assertThrows(RuntimeException.class, () -> graphqlClientUtils
				.getDirectiveParameters(GraphQLDirectiveTest_ClassCase.class, "does not exist", "oneParameter"));
		assertTrue(e.getMessage().contains("must be a Method"));

		e = assertThrows(RuntimeException.class,
				() -> graphqlClientUtils.getDirectiveParameters(
						GraphQLDirectiveTest_ClassCase.class.getMethod("method", int.class), "does not exist",
						"oneParameter"));
		assertTrue(e.getMessage().contains("does not exist"));

		//////////////////////////////////////////////////////////////////////////////////////////////
		// Directive on the GraphQLDirectiveTest_ClassCase class
		assertEquals(0, graphqlClientUtils
				.getDirectiveParameters(GraphQLDirectiveTest_ClassCase.class, null, "noParameters").size(),
				"The noParameters directive has no parameters");

		map = graphqlClientUtils.getDirectiveParameters(GraphQLDirectiveTest_ClassCase.class, null, "oneParameter");
		assertEquals(1, map.size(), "The oneParameter directive has one parameter");
		assertTrue(map.keySet().contains("param"));
		assertEquals("value", map.get("param"));

		map = graphqlClientUtils.getDirectiveParameters(GraphQLDirectiveTest_ClassCase.class, null, "twoParameters");
		assertEquals(2, map.size(), "The oneParameter directive has two parameters");
		assertTrue(map.keySet().contains("param1"));
		assertTrue(map.keySet().contains("param2"));
		assertEquals("value1", map.get("param1"));
		assertEquals("value2", map.get("param2"));

		map = graphqlClientUtils.getDirectiveParameters(GraphQLDirectiveTest_ClassCase.class.getField("field"), null,
				"field's annotation");
		assertEquals(1, map.size());
		assertTrue(map.keySet().contains("paramField"));
		assertEquals("valueField", map.get("paramField"));

		map = graphqlClientUtils.getDirectiveParameters(
				GraphQLDirectiveTest_ClassCase.class.getMethod("method", int.class), null, "method's annotation");
		assertEquals(1, map.size());
		assertTrue(map.keySet().contains("paramMethod"));
		assertEquals("valueMethod", map.get("paramMethod"));

		map = graphqlClientUtils.getDirectiveParameters(
				GraphQLDirectiveTest_ClassCase.class.getMethod("method", int.class), "i", "param's annotation");
		assertEquals(1, map.size());
		assertTrue(map.keySet().contains("paramParam"));
		assertEquals("valueParam", map.get("paramParam"));

		//////////////////////////////////////////////////////////////////////////////////////////////
		// Directive on the GraphQLDirectiveTest_InterfaceCase interface
		map = graphqlClientUtils.getDirectiveParameters(GraphQLDirectiveTest_InterfaceCase.class, null,
				"interface directive");
		assertEquals(1, map.size(), "The 'interface directive' directive has one parameter");
		assertTrue(map.keySet().contains("paramInterface"));
		assertEquals("valueInterface", map.get("paramInterface"));

		map = graphqlClientUtils.getDirectiveParameters(
				GraphQLDirectiveTest_InterfaceCase.class.getMethod("method", int.class), null,
				"interface method's annotation");
		assertEquals(1, map.size());
		assertTrue(map.keySet().contains("paramInterfaceMethod"));
		assertEquals("valueInterfaceMethod", map.get("paramInterfaceMethod"));

		map = graphqlClientUtils.getDirectiveParameters(
				GraphQLDirectiveTest_InterfaceCase.class.getMethod("method", int.class), null,
				"interface method's annotation");
		assertEquals(1, map.size());
		assertTrue(map.keySet().contains("paramInterfaceMethod"));
		assertEquals("valueInterfaceMethod", map.get("paramInterfaceMethod"));
	}
}
