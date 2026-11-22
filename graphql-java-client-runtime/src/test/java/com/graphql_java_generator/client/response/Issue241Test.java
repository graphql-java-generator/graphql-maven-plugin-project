package com.graphql_java_generator.client.response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.graphql_java_generator.domain.client.forum.QueryRootResponse;

import tools.jackson.databind.json.JsonMapper;

/**
 * Class coming from the
 * <a href="https://github.com/graphql-java-generator/graphql-maven-plugin-project/issues/241">github issue #241</a>
 */

@ExtendWith(MockitoExtension.class)
public class Issue241Test {

	@Test
	void simple() throws Exception {
		final JsonMapper jsonMapper = new JsonMapper();
		final QueryRootResponse qrr = jsonMapper.readValue("""
				{
				  "errors": [
				    {
				      "message": "some error message"
				    }
				  ]
				}""", QueryRootResponse.class);

		jsonMapper.writeValueAsString(qrr);
	}

}
