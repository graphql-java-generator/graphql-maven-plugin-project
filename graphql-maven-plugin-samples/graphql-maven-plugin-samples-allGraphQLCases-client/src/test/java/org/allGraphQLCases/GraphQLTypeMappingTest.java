/**
 * 
 */
package org.allGraphQLCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.allGraphQLCases.client.util.GraphQLTypeMapping;
import org.junit.jupiter.api.Test;

/**
 * @author etienne-sf
 */
public class GraphQLTypeMappingTest {

	@Test
	void test_getResourceAsStream() throws ClassNotFoundException {
		final String RESOURCE_PATH = "typeMappingAllGraphQLCases.csv";
		ClassLoader classLoader = GraphQLTypeMapping.class.getClassLoader();
		assertNotNull(classLoader.getResourceAsStream(RESOURCE_PATH));
	}

	@Test
	void test_GraphQLTypeMapping() throws ClassNotFoundException {
		assertEquals("org.allGraphQLCases.client.CTP_break_CTS", GraphQLTypeMapping.getJavaClass("break").getName());
		assertEquals("org.allGraphQLCases.client.CEP_extends_CES",
				GraphQLTypeMapping.getJavaClass("extends").getName());
	}

}
