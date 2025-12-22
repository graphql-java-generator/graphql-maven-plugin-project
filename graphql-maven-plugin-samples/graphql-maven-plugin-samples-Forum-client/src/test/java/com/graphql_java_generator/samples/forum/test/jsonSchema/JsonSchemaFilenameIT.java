package com.graphql_java_generator.samples.forum.test.jsonSchema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.graphql.client.ClientGraphQlResponse;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.graphql_java_generator.samples.forum.test.SpringTestConfig;

import graphql.introspection.IntrospectionQuery;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

/**
 * This class contains tests for the jsonSchemaFilename parameter. This parameter defines a json file that contains the
 * result of an introspection query.
 * 
 * @author etienne_sf
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SpringTestConfig.class })
@TestPropertySource("classpath:application.properties")
public class JsonSchemaFilenameIT {

	final String FORUM_GRAPHQL_JSON_SCHEMA_FILE = "src/main/resources/forum_GraphQLSchema.json";

	@Autowired
	@Qualifier("httpGraphQlClient")
	GraphQlClient graphQlClient;

	@Autowired
	com.graphql_java_generator.samples.forum.test.MavenTestHelper mavenTestHelper;

	/**
	 * The actual test is that the client code is generated from this json schema. <br/>
	 * The objective of this test is to check that the json schema is correct. That is: that the introspection query is
	 * executed. If its content is the same as the content of the json schema file, then the test is ok. <br/>
	 * Otherwise, the result of the introspection query is written into the json schema file. And the test fails, with
	 * an indication to just re-run the tests: the next time the client code will be generated from the good json schema
	 * file, and this test will be ok.
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	void testJsonSchema_checkSchema() throws JacksonException {
		ObjectMapper mapper = new ObjectMapper();

		// Step 1: read the current json file
		String currentJson = mavenTestHelper.readFile(FORUM_GRAPHQL_JSON_SCHEMA_FILE);

		// Step 2: execute the introspection query
		ClientGraphQlResponse result = graphQlClient//
				.document(IntrospectionQuery.INTROSPECTION_QUERY)//
				.execute()//
				.block();
		Map<String, Object> map = result.getData();
		// Let's write a properly formatted json, as this file will be part of the sources (and a manual check may be
		// useful).
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);

		// Step 3: are these two json identitical?
		if (!currentJson.equals(json)) {
			// The forum GraphQL schema changed. Let's save the new one, and fail: the test must be re-run, with the
			// correct json.
			mavenTestHelper.writeFile(FORUM_GRAPHQL_JSON_SCHEMA_FILE, json);
			// Let's fail with a comparison of both json
			String msg = "The json file was not up to date. It has now been updated. Please re-run the test with 'mvn clean install' or 'gradlew clean build' to use the re-generated json file";
			assertEquals(currentJson, json, msg);
			fail(msg); // We should never go there
		} else {
			// Ok, the used json file is correct
		}
	}

}
