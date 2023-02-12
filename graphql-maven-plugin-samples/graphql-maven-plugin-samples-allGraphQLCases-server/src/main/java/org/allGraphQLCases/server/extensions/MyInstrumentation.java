package org.allGraphQLCases.server.extensions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import graphql.ExceptionWhileDataFetching;
import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.GraphQLError;
import graphql.GraphqlErrorException;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.language.SourceLocation;

/**
 * This class has been copied from
 * <A HREF="https://stackoverflow.com/questions/54244791/how-to-respond-with-extensions-using-graphql-java">this
 * thread</A>, based on graphql-java's TracingInstrumentation.
 * 
 * @author Joe
 */
public class MyInstrumentation extends SimpleInstrumentation {

	public static class MyExtensionValue {
		public String name;
		public String forname;
	}

	@Override
	public CompletableFuture<ExecutionResult> instrumentExecutionResult(ExecutionResult executionResult,
			InstrumentationExecutionParameters parameters) {

		// An extension is added to each result, to allow, on client side, to check the proper reading of the extension
		// field
		Map<Object, Object> currentExt = executionResult.getExtensions();
		Map<Object, Object> newExtensionMap = new LinkedHashMap<>();
		newExtensionMap.putAll(currentExt == null ? Collections.emptyMap() : currentExt);
		//
		// We add a specific object into the extensions map. It will be returned into the GraphQL responses. This allows
		// to check on client side that the extensions field is properly managed.
		MyExtensionValue value = new MyExtensionValue();
		value.name = "The name";
		value.forname = "The forname";
		newExtensionMap.put("aValueToTestTheExtensionsField", value);

		boolean letExceptionDataPass = false;

		if (letExceptionDataPass) {
			return CompletableFuture.completedFuture(
					new ExecutionResultImpl(executionResult.getData(), executionResult.getErrors(), newExtensionMap));
		} else {
			// Same for extension on errors. If an error is to be sent, let's add an extension here to check that it is
			// properly read on client side
			List<GraphQLError> errors = executionResult.getErrors();
			List<GraphQLError> errorsToBeReturned = new ArrayList<>();

			for (GraphQLError error : errors) {
				String errorMessage = error.getMessage();

				// Let's also add a sourceLocation, if there is none already
				if (errorMessage.contains("add a SourceLocation")) {
					ExceptionWhileDataFetching ex = (ExceptionWhileDataFetching) error;
					List<SourceLocation> locations = new ArrayList<>();
					locations.add(new SourceLocation(11, 111, "Some source line 11"));
					locations.add(new SourceLocation(22, 222));

					errorsToBeReturned.add(GraphqlErrorException.newErrorException()//
							.message(error.getMessage())//
							.sourceLocations(locations)//
							.build());
				} else if (errorMessage.contains("add an extension")) {
					Map<String, Object> extensions = new HashMap<>();
					extensions.put("An error extension", "An error extension's value (MyInstrumentation)");
					extensions.put("Another error extension", "Another error extension's value (MyInstrumentation)");

					errorsToBeReturned.add(GraphqlErrorException.newErrorException()//
							.message(error.getMessage())//
							.extensions(extensions)//
							.build());
				} else {
					errorsToBeReturned.add(error);
				}
			}
			return CompletableFuture.completedFuture(
					new ExecutionResultImpl(executionResult.getData(), errorsToBeReturned, newExtensionMap));
		}
	}

}
