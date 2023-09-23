/** Generated by the default template from graphql-java-generator */
package com.graphql_java_generator.domain.client.starwars.scalar;

import com.graphql_java_generator.util.GraphqlUtils;

public class GraphQLTypeMapping {

	public static Class<?> getJavaClass(String typeName) {
		try {
			return GraphQLTypeMapping.class.getClassLoader().loadClass(GraphQLTypeMapping.class.getPackage().getName()
					+ "." + GraphqlUtils.graphqlUtils.getJavaName(typeName));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Internal test error: could not load the '"
					+ GraphQLTypeMapping.class.getPackage().getName() + "." + typeName + "' class", e);
		}
	}
}