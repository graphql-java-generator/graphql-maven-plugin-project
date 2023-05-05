package com.graphql_java_generator.util.test_cases;

import com.graphql_java_generator.annotation.GraphQLDirective;

/**
 * A test case, to test the {@link graphqlClientUtils#getDirectiveParameters(Object, String, String)} method
 */
@GraphQLDirective(name = "noParameters")
@GraphQLDirective(name = "oneParameter", parameterNames = { "param" }, parameterTypes = { "type" }, parameterValues = {
		"value" })
@GraphQLDirective(name = "twoParameters", parameterNames = { "param1", "param2" }, parameterTypes = { "type1",
		"type2" }, parameterValues = { "value1", "value2" })
public class GraphQLDirectiveTest_ClassCase {
	@GraphQLDirective(name = "field's annotation", parameterNames = { "paramField" }, parameterTypes = {
			"typeField" }, parameterValues = { "valueField" })
	public int field;

	@GraphQLDirective(name = "method's annotation", parameterNames = { "paramMethod" }, parameterTypes = {
			"typeMethod" }, parameterValues = { "valueMethod" })
	public void method(@GraphQLDirective(name = "param's annotation", parameterNames = {
			"paramParam" }, parameterTypes = { "typeParam" }, parameterValues = { "valueParam" }) int i) {
		// No action
	}
}
