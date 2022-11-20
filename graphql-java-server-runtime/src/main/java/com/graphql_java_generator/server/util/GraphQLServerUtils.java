/**
 * 
 */
package com.graphql_java_generator.server.util;

import org.springframework.graphql.execution.ClassNameTypeResolver;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.annotation.GraphQLEnumType;
import com.graphql_java_generator.annotation.GraphQLInterfaceType;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLUnionType;

/**
 * A class that contains utility method for the server mode
 * 
 * @author etienne-sf
 */
@Component
public class GraphQLServerUtils {

	/**
	 * Implementation of a {@link ClassNameTypeResolver} to manage the possible prefix and suffix on the generated
	 * POJOs.
	 * 
	 * @param cls
	 *            The class which name must be retrieved
	 * @return The GraphQL type name that matches this class
	 */
	public String classNameExtractor(Class<?> cls) {

		GraphQLEnumType graphQLEnumType = cls.getAnnotation(GraphQLEnumType.class);
		if (graphQLEnumType != null) {
			return graphQLEnumType.value();
		}

		GraphQLInterfaceType graphQLInterfaceType = cls.getAnnotation(GraphQLInterfaceType.class);
		if (graphQLInterfaceType != null) {
			return graphQLInterfaceType.value();
		}

		GraphQLObjectType graphQLObjectType = cls.getAnnotation(GraphQLObjectType.class);
		if (graphQLObjectType != null) {
			return graphQLObjectType.value();
		}

		GraphQLUnionType graphQLUnionType = cls.getAnnotation(GraphQLUnionType.class);
		if (graphQLUnionType != null) {
			return graphQLUnionType.value();
		}

		// The default is to return the simple class name
		return cls.getSimpleName();
	}

}
