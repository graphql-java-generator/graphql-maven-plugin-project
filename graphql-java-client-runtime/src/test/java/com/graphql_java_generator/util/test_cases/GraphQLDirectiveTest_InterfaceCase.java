package com.graphql_java_generator.util.test_cases;

import com.graphql_java_generator.annotation.GraphQLDirective;

/**
 * A test case, to test the {@link graphqlClientUtils#getDirectiveParameters(Object, String, String)} method
 */
@GraphQLDirective(name = "interface directive", parameterNames = { "paramInterface" }, parameterTypes = {
		"typeInterface" }, parameterValues = { "valueInterface" })
public interface GraphQLDirectiveTest_InterfaceCase {
	@GraphQLDirective(name = "interface method's annotation", parameterNames = {
			"paramInterfaceMethod" }, parameterTypes = {
					"typeInterfaceMethod" }, parameterValues = { "valueInterfaceMethod" })
	public void method(@GraphQLDirective(name = "interface param's annotation", parameterNames = {
			"paramInterfaceParam" }, parameterTypes = {
					"typeInterfaceParam" }, parameterValues = { "valueInterfaceParam" }) int i);
}
