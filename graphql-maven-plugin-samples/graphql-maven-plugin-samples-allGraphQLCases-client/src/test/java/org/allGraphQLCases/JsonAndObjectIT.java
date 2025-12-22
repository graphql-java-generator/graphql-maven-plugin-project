package org.allGraphQLCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.allGraphQLCases.client.CEP_Episode_CES;
import org.allGraphQLCases.client.CINP_InputWithJson_CINS;
import org.allGraphQLCases.client.CINP_InputWithObject_CINS;
import org.allGraphQLCases.client.CTP_TypeWithJson_CTS;
import org.allGraphQLCases.client.CTP_TypeWithObject_CTS;
import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

//Adding "webEnvironment = SpringBootTest.WebEnvironment.NONE" avoid this error:
//"No qualifying bean of type 'ReactiveClientRegistrationRepository' available"
//More details here: https://stackoverflow.com/questions/62558552/error-when-using-enablewebfluxsecurity-in-springboot
@SpringBootTest(classes = SpringTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Execution(ExecutionMode.CONCURRENT)
public class JsonAndObjectIT {

	@Autowired
	MyQueryTypeExecutorAllGraphQLCases queryExecutor;

	ObjectMapper objectMapper = new ObjectMapper();

	String testInstance = "a global value for test";
	@SuppressWarnings("deprecation")
	Date dateInstance = new Date(2023 - 1900, 12 - 1, 30);// DateUtils.truncate(new Date(),
															// java.util.Calendar.DAY_OF_MONTH);
	Long longInstance = 2345284L;
	Boolean booleanInstance = true;
	CEP_Episode_CES enumInstance = CEP_Episode_CES.NEWHOPE;
	ObjectNode jsonInstance;
	List<ObjectNode> jsonsInstance;

	@BeforeEach
	void beforeEach() throws DatabindException, JacksonException {
		jsonInstance = objectMapper.readValue("{\"field1\":\"value1\"}", ObjectNode.class);
		jsonsInstance = Arrays.asList(//
				objectMapper.readValue("{\"field11\":\"value11\"}", ObjectNode.class),
				objectMapper.readValue("{\"field12\":\"value12\"}", ObjectNode.class));
	}

	/**
	 * Test for issue 205
	 * 
	 * @throws DatabindException
	 * @throws JacksonException
	 * @throws GraphQLRequestExecutionException
	 * @throws GraphQLRequestPreparationException
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue205_JsonArgumentsAndJsonParameter() throws DatabindException, JacksonException,
			GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		ObjectNode json = objectMapper.readValue(
				"{\"field\":\"value\", \"subObject\": {\"field2\" : [1,2,3], \"field3\" : [1.1,22.2,3.3]} ,  \"booleans\" : [true , false]}",
				ObjectNode.class);

		// Go, go, go
		ObjectNode response = queryExecutor.json("", json);

		// Verification
		assertEquals(
				"{\"field\":\"value\",\"subObject\":{\"field2\":[1,2,3],\"field3\":[1.1,22.2,3.3]},\"booleans\":[true,false]}",
				response.toString());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue205_JsonArgumentsAndInputTypeWithJsonField() throws DatabindException, JacksonException,
			GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		ObjectNode json = objectMapper.readValue(
				"{\"field\":\"value\", \"subObject\": {\"field2\" : [1,2,3], \"field3\" : [1.1,22.2,3.3]} ,  \"booleans\" : [true , false]}",
				ObjectNode.class);
		CINP_InputWithJson_CINS input = CINP_InputWithJson_CINS.builder()
				.withTest("test_Issue205_JsonArgumentsAndInputTypeWithJsonField")//
				.withDate(dateInstance)//
				.withLong(longInstance)//
				.withBoolean(booleanInstance)//
				.withEnum(enumInstance)//
				.withJson(json)//
				.withJsons(Arrays.asList(json, json))//
				.build();

		// Go, go, go
		CTP_TypeWithJson_CTS response = queryExecutor.jsonWithInput(""//
				+ "{"//
				+ "   test"//
				+ "	  withArguments("//
				+ "      test: &test,"//
				+ "      date: &date,"//
				+ "      long: &long,"//
				+ "      boolean: &boolean,"//
				+ "      enum: &enum,"//
				+ "      json: &json,"//
				+ "      jsons: &jsons)"//
				+ "	  long"//
				+ "	  boolean"//
				+ "	  enum"//
				+ "	  json"//
				+ "	  jsons"//
				+ "}"//
				, input, //
				"test", testInstance, //
				"date", dateInstance, //
				"long", longInstance, //
				"boolean", booleanInstance, //
				"enum", enumInstance, //
				"json", jsonInstance, //
				"jsons", jsonsInstance);

		// Verification
		assertNotNull(response);
		assertEquals("test_Issue205_JsonArgumentsAndInputTypeWithJsonField", response.getTest());
		assertEquals(
				"{\"field\":\"value\",\"subObject\":{\"field2\":[1,2,3],\"field3\":[1.1,22.2,3.3]},\"booleans\":[true,false]}",
				response.getJson().toString());
		//
		checkWithArgumentsValue(response.getWithArguments(), response.toString());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue205_JsonArgumentsAndListOfInputTypeWithJsonField() throws DatabindException, JacksonException,
			GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		ObjectNode json = objectMapper.readValue(
				"{\"field\":\"value\", \"subObject\": {\"field2\" : [1,2,3], \"field3\" : [1.1,22.2,3.3]} ,  \"booleans\" : [true , false]}",
				ObjectNode.class);
		CINP_InputWithJson_CINS item = CINP_InputWithJson_CINS.builder()//
				.withTest("test_Issue205_JsonArgumentsAndInputTypeWithJsonField")//
				.withDate(dateInstance)//
				.withLong(longInstance)//
				.withBoolean(booleanInstance)//
				.withEnum(enumInstance)//
				.withJson(json)//
				.withJsons(Arrays.asList(json, json))//
				.build();

		// Go, go, go
		List<CTP_TypeWithJson_CTS> response = queryExecutor.jsonsWithInput("{test date long boolean enum json jsons}",
				Arrays.asList(item, item));

		// Verification
		assertNotNull(response);
		assertEquals(2, response.size());
		assertEquals("test_Issue205_JsonArgumentsAndInputTypeWithJsonField", response.get(0).getTest());
		assertEquals(
				"{\"field\":\"value\",\"subObject\":{\"field2\":[1,2,3],\"field3\":[1.1,22.2,3.3]},\"booleans\":[true,false]}",
				response.get(0).getJson().toString());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue205_JsonArrayArgumentsAndParameters() throws DatabindException, JacksonException,
			GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		List<ObjectNode> jsons = Arrays.asList(
				objectMapper.readValue("{\"field1\":\"value1\", \"field2\":[1,2,3]}", ObjectNode.class),
				objectMapper.readValue("{\"field3\":\"value4\", \"field5\":[]}", ObjectNode.class));

		// Go, go, go
		List<ObjectNode> response = queryExecutor.jsons("", jsons);

		// Verification
		assertNotNull(response);
		assertEquals(2, response.size());
		assertEquals("{\"field1\":\"value1\",\"field2\":[1,2,3]}", response.get(0).toString());
		assertEquals("{\"field3\":\"value4\",\"field5\":[]}", response.get(1).toString());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue205_Json() throws DatabindException, JacksonException, GraphQLRequestExecutionException,
			GraphQLRequestPreparationException {
		// Preparation

		// Go, go, go
		ObjectNode response = queryExecutor.exec("query {json}").getJson();

		// Verification
		assertNotNull(response);
		assertEquals("{\"field1\":\"value1\",\"field2\":\"value2\"}", response.toString());
	}

	/**
	 * Test for issue 205<br/>
	 * 
	 * @throws DatabindException
	 * @throws JacksonException
	 * @throws GraphQLRequestExecutionException
	 * @throws GraphQLRequestPreparationException
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue205_JsonArgumentsAndQueryVariable() throws DatabindException, JacksonException,
			GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		ObjectNode json = objectMapper.readValue(
				"{\"field\":\"test_Issue205_JsonArgumentsAndQueryVariable\",\"field2\":[1,2,3]}", ObjectNode.class);

		// Go, go, go
		ObjectNode response = queryExecutor.exec(//
				"query json($json: JSON) {json(json: $json) {} }", //
				"json", json).getJson();

		// Verification
		assertEquals("{\"field\":\"test_Issue205_JsonArgumentsAndQueryVariable\",\"field2\":[1,2,3]}",
				response.toString());
	}

	/**
	 * Test for issue 205<br/>
	 * 
	 * @throws DatabindException
	 * @throws JacksonException
	 * @throws GraphQLRequestExecutionException
	 * @throws GraphQLRequestPreparationException
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue205_JsonTypeArgumentsAndQueryVariable() throws DatabindException, JacksonException,
			GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		ObjectNode json = objectMapper.readValue(
				"{\"field\":\"test_Issue205_JsonTypeArgumentsAndQueryVariable\",\"field2\":[1,2,3]}", ObjectNode.class);
		CINP_InputWithJson_CINS input = CINP_InputWithJson_CINS.builder()//
				.withTest("test_Issue205_JsonTypeArgumentsAndQueryVariable")//
				.withDate(dateInstance)//
				.withLong(longInstance)//
				.withBoolean(booleanInstance)//
				.withEnum(enumInstance)//
				.withJson(json)//
				.withJsons(Arrays.asList(json, json))//
				.build();

		// Go, go, go
		CTP_TypeWithJson_CTS response = queryExecutor.exec(
				"query jsonWithInputQuery($input: InputWithJson!) {jsonWithInput(input: $input) {test date long boolean enum json jsons} }",
				"input", input).getJsonWithInput();

		// Verification
		assertNotNull(response);
		assertEquals("test_Issue205_JsonTypeArgumentsAndQueryVariable", response.getTest());
		assertEquals(dateInstance, response.getDate());
		assertEquals(longInstance, response.getLong());
		assertEquals(booleanInstance, response.getBoolean());
		assertEquals(enumInstance, response.getEnum());
		assertEquals("{\"field\":\"test_Issue205_JsonTypeArgumentsAndQueryVariable\",\"field2\":[1,2,3]}",
				response.getJson().toString());
		assertEquals(2, response.getJsons().size());
		assertEquals("{\"field\":\"test_Issue205_JsonTypeArgumentsAndQueryVariable\",\"field2\":[1,2,3]}",
				response.getJsons().get(0).toString());
		assertEquals("{\"field\":\"test_Issue205_JsonTypeArgumentsAndQueryVariable\",\"field2\":[1,2,3]}",
				response.getJsons().get(1).toString());
	}

	/**
	 * Test for issue 205<br/>
	 * 
	 * @throws DatabindException
	 * @throws JacksonException
	 * @throws GraphQLRequestExecutionException
	 * @throws GraphQLRequestPreparationException
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue205_JsonTypeArrayArgumentsAndQueryVariable() throws DatabindException, JacksonException,
			GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		ObjectNode json = objectMapper.readValue(
				"{\"field\":\"test_Issue205_JsonTypeArrayArgumentsAndQueryVariable\",\"field2\":[1,2,3]}",
				ObjectNode.class);
		CINP_InputWithJson_CINS item = CINP_InputWithJson_CINS.builder()//
				.withTest("test_Issue205_JsonTypeArrayArgumentsAndQueryVariable")//
				.withDate(dateInstance)//
				.withLong(1234L)//
				.withBoolean(true)//
				.withEnum(CEP_Episode_CES.JEDI)//
				.withJson(json)//
				.withJsons(Arrays.asList(json, json))//
				.build();

		// Go, go, go
		List<CTP_TypeWithJson_CTS> response = queryExecutor.exec(
				"query jsonsWithInputQuery($input: [InputWithJson!]!) {jsonsWithInput(input: $input) {test date long boolean enum json jsons} }", //
				"input", Arrays.asList(item, item)).getJsonsWithInput();

		// Verification
		assertNotNull(response);
		assertEquals(2, response.size());
		assertTrue(response.get(0) instanceof CTP_TypeWithJson_CTS);
		assertTrue(response.get(1) instanceof CTP_TypeWithJson_CTS);

		CTP_TypeWithJson_CTS responseItem1 = response.get(0);
		assertEquals("test_Issue205_JsonTypeArrayArgumentsAndQueryVariable", responseItem1.getTest());
		assertEquals(dateInstance, responseItem1.getDate());
		assertEquals(1234L, responseItem1.getLong());
		assertEquals(true, responseItem1.getBoolean());
		assertEquals(CEP_Episode_CES.JEDI, responseItem1.getEnum());
		assertEquals("{\"field\":\"test_Issue205_JsonTypeArrayArgumentsAndQueryVariable\",\"field2\":[1,2,3]}",
				responseItem1.getJson().toString());
		assertEquals(2, responseItem1.getJsons().size());
		assertEquals("{\"field\":\"test_Issue205_JsonTypeArrayArgumentsAndQueryVariable\",\"field2\":[1,2,3]}",
				responseItem1.getJsons().get(0).toString());
		assertEquals("{\"field\":\"test_Issue205_JsonTypeArrayArgumentsAndQueryVariable\",\"field2\":[1,2,3]}",
				responseItem1.getJsons().get(1).toString());
	}

	/**
	 * Test for issue 205<br/>
	 * The returned error was:
	 * 
	 * <pre>
	 * 09:12:17.959 [http-nio-8180-exec-9] DEBUG o.s.g.s.webmvc.GraphQlHttpHandler - Executing: document='mutation{json(json:{"field1":"value1","field2":"value2"})}', id=27f76768-e8c7-f9b3-ddb8-c8ceb6a70101, Locale=fr_FR 
	 * 09:12:17.962 [http-nio-8180-exec-9] WARN notprivacysafe.graphql.GraphQL - Query did not parse : 'mutation{json(json:{"field1":"value1","field2":"value2"})}' 
	 * 09:12:17.962 [http-nio-8180-exec-9] DEBUG o.s.g.s.webmvc.GraphQlHttpHandler - Execution complete
	 * </pre>
	 * 
	 * @throws DatabindException
	 * @throws JacksonException
	 * @throws GraphQLRequestExecutionException
	 * @throws GraphQLRequestPreparationException
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)

	void test_Issue205_ObjectArgumentsAndJsonParameter() throws DatabindException, JacksonException,
			GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		ObjectNode object = objectMapper.readValue(
				"{\"field\":\"value\", \"subObject\": {\"field2\" : [1,2,3], \"field3\" : [1.1,22.2,3.3]} ,  \"booleans\" : [true , false]}",
				ObjectNode.class);

		// Go, go, go
		ObjectNode response = queryExecutor.json("", object);

		// Verification
		assertEquals(
				"{\"field\":\"value\",\"subObject\":{\"field2\":[1,2,3],\"field3\":[1.1,22.2,3.3]},\"booleans\":[true,false]}",
				response.toString());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue205_ObjectArgumentsAndInputTypeWithObjectField() throws DatabindException, JacksonException,
			GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		Map<?, ?> object = objectMapper.readValue(
				"{\"field\":\"value\", \"subObject\": {\"field2\" : [1,2,3], \"field3\" : [1.1,22.2,3.3]} ,  \"booleans\" : [true , false]}",
				HashMap.class);
		CINP_InputWithObject_CINS input = CINP_InputWithObject_CINS.builder()//
				.withTest("test_Issue205_ObjectArgumentsAndInputTypeWithObjectField")//
				.withDate(dateInstance)//
				.withLong(1234L)//
				.withBoolean(true)//
				.withEnum(CEP_Episode_CES.JEDI)//
				.withObject(object)//
				.withObjects(Arrays.asList(object, object))//
				.build();

		// Go, go, go
		CTP_TypeWithObject_CTS response = queryExecutor.objectWithInput("{test date long boolean enum object objects}",
				input);

		// Verification
		assertNotNull(response);
		assertEquals("test_Issue205_ObjectArgumentsAndInputTypeWithObjectField", response.getTest());
		assertEquals(dateInstance, response.getDate());
		assertEquals(1234L, response.getLong());
		assertEquals(true, response.getBoolean());
		assertEquals(CEP_Episode_CES.JEDI, response.getEnum());
		assertEquals(
				"{\"subObject\":{\"field2\":[1,2,3],\"field3\":[1.1,22.2,3.3]},\"field\":\"value\",\"booleans\":[true,false]}",
				response.getObject().toString());
		assertEquals("" //
				+ "["//
				+ "{\"subObject\":{\"field2\":[1,2,3],\"field3\":[1.1,22.2,3.3]},\"field\":\"value\",\"booleans\":[true,false]}"
				+ ", "//
				+ "{\"subObject\":{\"field2\":[1,2,3],\"field3\":[1.1,22.2,3.3]},\"field\":\"value\",\"booleans\":[true,false]}"
				+ "]", //
				response.getObjects().toString());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue205_ObjectArgumentsAndListOfInputTypeWithObjectField() throws DatabindException, JacksonException,
			GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		ObjectNode json = objectMapper.readValue(
				"{\"field\":\"value\", \"subObject\": {\"field2\" : [1,2,3], \"field3\" : [1.1,22.2,3.3]} ,  \"booleans\" : [true , false]}",
				ObjectNode.class);
		CINP_InputWithObject_CINS input = CINP_InputWithObject_CINS.builder()
				.withTest("test_Issue205_ObjectArgumentsAndListOfInputTypeWithObjectField")//
				.withDate(dateInstance)//
				.withLong(1234L)//
				.withBoolean(true)//
				.withEnum(CEP_Episode_CES.JEDI)//
				.withObject(json)//
				.withObjects(Arrays.asList(json, json))//
				.build();

		// Go, go, go
		List<CTP_TypeWithObject_CTS> response = queryExecutor
				.objectsWithInput("{test long boolean enum object objects}", Arrays.asList(input, input, input));

		// Verification
		assertNotNull(response);
		assertEquals(3, response.size());
		assertEquals("test_Issue205_ObjectArgumentsAndListOfInputTypeWithObjectField", response.get(0).getTest());
		assertEquals(
				"{\"field\":\"value\",\"subObject\":{\"field2\":[1,2,3],\"field3\":[1.1,22.2,3.3]},\"booleans\":[true,false]}",
				response.get(0).getObject().toString());
		assertEquals("" //
				+ "["//
				+ "{\"field\":\"value\",\"subObject\":{\"field2\":[1,2,3],\"field3\":[1.1,22.2,3.3]},\"booleans\":[true,false]}"
				+ ", "//
				+ "{\"field\":\"value\",\"subObject\":{\"field2\":[1,2,3],\"field3\":[1.1,22.2,3.3]},\"booleans\":[true,false]}"
				+ "]", //
				response.get(0).getObjects().toString());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue205_ObjectArrayArgumentsAndParameters() throws DatabindException, JacksonException,
			GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		List<ObjectNode> jsons = Arrays.asList(
				objectMapper.readValue("{\"field1\":\"value1\", \"field2\":[1,2,3]}", ObjectNode.class),
				objectMapper.readValue("{\"field3\":\"value4\", \"field5\":[]}", ObjectNode.class));

		// Go, go, go
		List<ObjectNode> response = queryExecutor.jsons("", jsons);

		// Verification
		assertNotNull(response);
		assertEquals(2, response.size());
		assertEquals("{\"field1\":\"value1\",\"field2\":[1,2,3]}", response.get(0).toString());
		assertEquals("{\"field3\":\"value4\",\"field5\":[]}", response.get(1).toString());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue205_Object() throws DatabindException, JacksonException, GraphQLRequestExecutionException,
			GraphQLRequestPreparationException {
		// Go, go, go
		ObjectNode response = queryExecutor.exec("query {json}").getJson();

		// Verification
		assertNotNull(response);
		assertEquals("{\"field1\":\"value1\",\"field2\":\"value2\"}", response.toString());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)

	void test_Issue205_ObjectArgumentsAndQueryVariable() throws DatabindException, JacksonException,
			GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		ObjectNode object = objectMapper.readValue(
				"{\"field\":\"test_Issue205_JsonArgumentsAndQueryVariable\",\"field2\":[1,2,3]}", ObjectNode.class);

		// Go, go, go
		Object response = queryExecutor.exec("object($object: Object) {object(object: $object) {} }", "object", object)
				.getObject();

		// Verification
		assertEquals("{\"field\":\"test_Issue205_JsonArgumentsAndQueryVariable\",\"field2\":[1,2,3]}",
				response.toString());
	}

	/**
	 * Test for issue 205<br/>
	 * 
	 * @throws DatabindException
	 * @throws JacksonException
	 * @throws GraphQLRequestExecutionException
	 * @throws GraphQLRequestPreparationException
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue205_ObjectTypeArgumentsAndQueryVariable() throws DatabindException, JacksonException,
			GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		Map<?, ?> map = objectMapper.readValue(
				"{\"field\":\"test_Issue205_ObjectTypeArgumentsAndQueryVariable\",\"field2\":[1,2,3]}", HashMap.class);
		CINP_InputWithObject_CINS input = CINP_InputWithObject_CINS.builder()//
				.withTest("test_Issue205_ObjectTypeArgumentsAndQueryVariable")//
				.withDate(dateInstance)//
				.withLong(1234L)//
				.withBoolean(true)//
				.withEnum(CEP_Episode_CES.JEDI)//
				.withObject(map)//
				.withObjects(Arrays.asList(map, map))//
				.build();

		// Go, go, go
		CTP_TypeWithObject_CTS response = queryExecutor//
				.exec(""//
						+ "query objectWithInputQuery($input: InputWithObject!) {objectWithInput(input: $input) {"//
						+ "   test"//
						+ "	  withArguments("//
						+ "      test: &test,"//
						+ "      date: &date,"//
						+ "      long: &long,"//
						+ "      boolean: &boolean,"//
						+ "      enum: &enum,"//
						+ "      object: &json,"//
						+ "      objects: &jsons)"//
						+ "	  date"//
						+ "	  long"//
						+ "	  boolean"//
						+ "	  enum"//
						+ "	  object"//
						+ "	  objects"//
						+ "} }", //
						"input", input, //
						"test", testInstance, //
						"date", dateInstance, //
						"long", longInstance, //
						"boolean", booleanInstance, //
						"enum", enumInstance, //
						"json", jsonInstance, //
						"jsons", jsonsInstance)//
				.getObjectWithInput();

		// Verification
		assertNotNull(response);
		assertEquals("test_Issue205_ObjectTypeArgumentsAndQueryVariable", response.getTest());
		assertEquals(dateInstance, response.getDate());
		assertEquals(1234L, response.getLong());
		assertEquals(true, response.getBoolean());
		assertEquals(CEP_Episode_CES.JEDI, response.getEnum());
		assertEquals("{\"field\":\"test_Issue205_ObjectTypeArgumentsAndQueryVariable\",\"field2\":[1,2,3]}",
				response.getObject().toString());
		assertEquals(2, response.getObjects().size());
		assertEquals("{\"field\":\"test_Issue205_ObjectTypeArgumentsAndQueryVariable\",\"field2\":[1,2,3]}",
				response.getObjects().get(0).toString());
		assertEquals("{\"field\":\"test_Issue205_ObjectTypeArgumentsAndQueryVariable\",\"field2\":[1,2,3]}",
				response.getObjects().get(1).toString());
		//
		// Check of the withArguments value:
		checkWithArgumentsValue(response.getWithArguments(), response.toString());
	}

	/**
	 * Test for issue 205<br/>
	 * 
	 * @throws DatabindException
	 * @throws JacksonException
	 * @throws GraphQLRequestExecutionException
	 * @throws GraphQLRequestPreparationException
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue205_ObjectTypeArrayArgumentsAndQueryVariable() throws DatabindException, JacksonException,
			GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		Map<?, ?> map = objectMapper.readValue(
				"{\"field\":\"test_Issue205_ObjectTypeArrayArgumentsAndQueryVariable\",\"field2\":[1,2,3]}",
				HashMap.class);
		CINP_InputWithObject_CINS item = CINP_InputWithObject_CINS.builder()//
				.withTest("test_Issue205_ObjectTypeArrayArgumentsAndQueryVariable")//
				.withDate(dateInstance)//
				.withLong(1234L)//
				.withBoolean(true)//
				.withEnum(CEP_Episode_CES.JEDI)//
				.withObject(map)//
				.withObjects(Arrays.asList(map, map))//
				.build();

		// Go, go, go
		List<CTP_TypeWithObject_CTS> response = queryExecutor
				.exec("query objectsWithInputQuery($input: [InputWithObject!]!) {objectsWithInput(input: $input) {"
						+ "   test"//
						+ "	  withArguments("//
						+ "      test: &test,"//
						+ "      date: &date,"//
						+ "      long: &long,"//
						+ "      boolean: &boolean,"//
						+ "      enum: &enum,"//
						+ "      object: &json,"//
						+ "      objects: &jsons)"//
						+ "	  date"//
						+ "	  long"//
						+ "	  boolean"//
						+ "	  enum"//
						+ "	  object"//
						+ "	  objects"//
						+ "} }", //
						"input", Arrays.asList(item, item), //
						"test", testInstance, //
						"date", dateInstance, //
						"long", longInstance, //
						"boolean", booleanInstance, //
						"enum", enumInstance, //
						"json", jsonInstance, //
						"jsons", jsonsInstance)//
				.getObjectsWithInput();

		// Verification
		assertNotNull(response);
		assertEquals(2, response.size());
		assertTrue(response.get(0) instanceof CTP_TypeWithObject_CTS);
		assertTrue(response.get(1) instanceof CTP_TypeWithObject_CTS);

		CTP_TypeWithObject_CTS responseItem1 = response.get(0);
		assertEquals("test_Issue205_ObjectTypeArrayArgumentsAndQueryVariable", responseItem1.getTest());
		assertEquals(dateInstance, responseItem1.getDate());
		assertEquals(1234L, responseItem1.getLong());
		assertEquals(true, responseItem1.getBoolean());
		assertEquals(CEP_Episode_CES.JEDI, responseItem1.getEnum());
		assertEquals("{\"field\":\"test_Issue205_ObjectTypeArrayArgumentsAndQueryVariable\",\"field2\":[1,2,3]}",
				responseItem1.getObject().toString());
		assertEquals(2, responseItem1.getObjects().size());
		assertEquals("{\"field\":\"test_Issue205_ObjectTypeArrayArgumentsAndQueryVariable\",\"field2\":[1,2,3]}",
				responseItem1.getObjects().get(0).toString());
		assertEquals("{\"field\":\"test_Issue205_ObjectTypeArrayArgumentsAndQueryVariable\",\"field2\":[1,2,3]}",
				responseItem1.getObjects().get(1).toString());
		//
		// Check of the withArguments value:
		checkWithArgumentsValue(responseItem1.getWithArguments(), responseItem1.toString());
	}

	private void checkWithArgumentsValue(String withArguments, String response) {
		assertTrue(withArguments.contains("test=StringValue{value='" + testInstance), response);
		assertTrue(withArguments.contains("date=StringValue{value='2023-12-30'}"), response);
		assertTrue(withArguments.contains("long=IntValue{value=" + longInstance.toString()), response);
		assertTrue(withArguments.contains("boolean=BooleanValue{value=" + booleanInstance.toString()), response);
		assertTrue(withArguments.contains("enum=EnumValue{name='" + enumInstance.toString()), response);
		assertTrue(withArguments.contains("{name='field1', value=StringValue{value='value1'}"), response);
		// assertTrue(withArguments.contains(this.jsonsInstance.toString()), response);
	}
}
