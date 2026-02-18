package org.allGraphQLCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import org.allGraphQLCases.client.util.GraphQLTypeMappingImpl;
import org.junit.jupiter.api.Test;

import com.graphql_java_generator.client.GraphQLTypeMappingRegistry;

/**
 * @author etienne-sf
 */
public class GraphQLTypeMappingTest {

	@Test
	void test_getResourceAsStream() throws ClassNotFoundException, IOException {
		final String RESOURCE_PATH = "typeMappingAllGraphQLCases.csv";
		ClassLoader classLoader = GraphQLTypeMappingImpl.class.getClassLoader();
		try (InputStream resource = classLoader.getResourceAsStream(RESOURCE_PATH)) {
			assertNotNull(resource);
		}

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
