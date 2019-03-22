package graphql.java.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import graphql.java.client.domain.Episode;

class InputParameterTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testInputParameter() {
		String name = "aName";
		String value = "a Value";
		InputParameter param = new InputParameter(name, value);
		assertEquals(name, param.getName(), "name");
		assertEquals(value, param.getValue(), "value");
	}

	@Test
	void testGetValueAsString_str() {
		String name = "aName";
		String value = "This is a string with two \"\" to be escaped";
		InputParameter param = new InputParameter(name, value);

		assertEquals(name, param.getName(), "name");
		assertEquals(value, param.getValue(), "value");
		assertEquals("\\\"This is a string with two \\\"\\\" to be escaped\\\"", param.getValueForGraphqlQuery(),
				"escaped value");
	}

	@Test
	void testGetValueAsString_enum() {
		String name = "aName";
		Episode value = Episode.EMPIRE;
		InputParameter param = new InputParameter(name, value);

		assertEquals(name, param.getName(), "name");
		assertEquals(value, param.getValue(), "value");
		assertEquals("EMPIRE", param.getValueForGraphqlQuery(), "escaped value");
	}

}
