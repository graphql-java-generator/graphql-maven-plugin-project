package com.graphql_java_generator.server.util.test_classes;

import com.graphql_java_generator.annotation.GraphQLObjectType;

/**
 * A class to test the {@link com.graphql_java_generator.server.util.GraphqlServerUtils#classNameExtractor(Class)}
 * method
 * 
 * @author etienne-sf
 */
@GraphQLObjectType("TheObjectName")
public class AnObjectType {

	public AnEnumType enumField;

}
