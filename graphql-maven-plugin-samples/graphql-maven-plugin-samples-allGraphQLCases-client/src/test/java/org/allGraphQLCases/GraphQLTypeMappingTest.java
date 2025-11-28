/**
 * 
 */
package org.allGraphQLCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.allGraphQLCases.client.util.GraphQLTypeMappingImpl;
import org.junit.jupiter.api.Test;

import com.graphql_java_generator.client.GraphQLTypeMappingRegistry;

/**
 * @author etienne-sf
 */
public class GraphQLTypeMappingTest {

	@Test
	void test_getResourceAsStream() throws ClassNotFoundException {
		final String RESOURCE_PATH = "typeMappingAllGraphQLCases.csv";
		ClassLoader classLoader = GraphQLTypeMappingImpl.class.getClassLoader();
		assertNotNull(classLoader.getResourceAsStream(RESOURCE_PATH));
	}

	@Test
	void test_GraphQLTypeMapping() throws ClassNotFoundException {
		GraphQLTypeMappingImpl.initGraphQLTypeMappingRegistry();

		assertNotNull(GraphQLTypeMappingRegistry.getGraphQLTypeMapping("AllGraphQLCases"));
		assertEquals("org.allGraphQLCases.client.CTP_break_CTS",
				GraphQLTypeMappingRegistry.getGraphQLTypeMapping("AllGraphQLCases").getJavaClass("break").getName());
		assertEquals("org.allGraphQLCases.client.CEP_extends_CES",
				GraphQLTypeMappingRegistry.getGraphQLTypeMapping("AllGraphQLCases").getJavaClass("extends").getName());
	}

}
