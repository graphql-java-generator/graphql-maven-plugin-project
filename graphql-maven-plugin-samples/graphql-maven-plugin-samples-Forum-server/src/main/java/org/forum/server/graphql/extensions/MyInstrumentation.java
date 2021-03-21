package org.forum.server.graphql.extensions;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;

/**
 * This class has been copied from
 * <A HREF="https://stackoverflow.com/questions/54244791/how-to-respond-with-extensions-using-graphql-java">this
 * thread</A>, based on graphql-java's TracingInstrumentation.
 * 
 * @author Joe
 */
public class MyInstrumentation extends SimpleInstrumentation {

	public static class ExtensionValue {
		public String name;
		public String forname;
	}

	@Override
	public CompletableFuture<ExecutionResult> instrumentExecutionResult(ExecutionResult executionResult,
			InstrumentationExecutionParameters parameters) {
		Map<Object, Object> currentExt = executionResult.getExtensions();
		Map<Object, Object> newExtensionMap = new LinkedHashMap<>();
		newExtensionMap.putAll(currentExt == null ? Collections.emptyMap() : currentExt);
		//
		// We add a specific object into the extensions map. It will be returned into the GraphQL responses. This allows
		// to check on client side that the extensions field is properly managed.
		ExtensionValue value = new ExtensionValue();
		value.name = "The name";
		value.forname = "The forname";
		newExtensionMap.put("aValueToTestTheExtensionsField", value);

		return CompletableFuture.completedFuture(
				new ExecutionResultImpl(executionResult.getData(), executionResult.getErrors(), newExtensionMap));
	}

}
