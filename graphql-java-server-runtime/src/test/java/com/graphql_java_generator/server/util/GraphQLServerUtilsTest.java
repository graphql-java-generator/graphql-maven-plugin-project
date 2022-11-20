package com.graphql_java_generator.server.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.graphql_java_generator.server.util.test_classes.AUnion;
import com.graphql_java_generator.server.util.test_classes.AnEnumType;
import com.graphql_java_generator.server.util.test_classes.AnInterface;
import com.graphql_java_generator.server.util.test_classes.AnObjectType;

class GraphQLServerUtilsTest {

	GraphQLServerUtils graphQLServerUtils = new GraphQLServerUtils();

	@Test
	void testClassNameExtractor() {
		assertEquals("TheEnumName", graphQLServerUtils.classNameExtractor(AnEnumType.class));
		assertEquals("TheInterfaceName", graphQLServerUtils.classNameExtractor(AnInterface.class));
		assertEquals("TheObjectName", graphQLServerUtils.classNameExtractor(AnObjectType.class));
		assertEquals("TheUnionName", graphQLServerUtils.classNameExtractor(AUnion.class));
	}

}
