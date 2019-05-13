/**
 * 
 */
package org.graphql.maven.plugin.samples.Forum.client;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLError;
import graphql.schema.GraphQLSchema;

/**
 * @author EtienneSF
 */
public class ManualTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GraphQLSchema schema = GraphQLSchema.newSchema().query(queryType).build();

		GraphQL graphQL = GraphQL.newGraphQL(schema).build();

		ExecutionInput executionInput = ExecutionInput.newExecutionInput().query("query { hero { name } }").build();

		ExecutionResult executionResult = graphQL.execute(executionInput);

		Object data = executionResult.getData();
		List<GraphQLError> errors = executionResult.getErrors();
	}

}
