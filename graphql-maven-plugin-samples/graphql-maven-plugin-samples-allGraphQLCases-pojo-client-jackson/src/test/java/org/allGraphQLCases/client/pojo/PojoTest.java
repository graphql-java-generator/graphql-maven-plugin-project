package org.allGraphQLCases.client.pojo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;

class PojoTest {

	private AllFieldCases allFieldCases;

	// The simple fact that this class compiles proves that the code is properly generated.
	// There is just a test to check that the jackson annotation have been generated

	@Test
	void testJacksonAnnotations() throws NoSuchFieldException, SecurityException {
		Field id = AllFieldCases.class.getDeclaredField("id");
		assertNotNull(id, "The id field must exist and be accessible");

		JsonProperty jsonProp = id.getAnnotation(JsonProperty.class);
		assertNotNull(jsonProp, "The JsonProperty must have been defined");
	}

}
