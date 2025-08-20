package org.allGraphQLCases.server.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.jspecify.annotations.NonNull;

import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimplePerformantInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;

/**
 * This class was original copied from
 * <A HREF="https://stackoverflow.com/questions/54244791/how-to-respond-with-extensions-using-graphql-java">this
 * thread</A>, based on graphql-java's TracingInstrumentation.<br/>
 * Then changed to {@link SimplePerformantInstrumentation}, as
 * {@link graphql.execution.instrumentation.SimpleInstrumentation} is now deprecated.
 * 
 * @author Joe
 */
public class MyInstrumentation extends SimplePerformantInstrumentation {

	public static class ExtensionValue {
		public String name;
		public String forname;
	}

	@Override
	public @NonNull CompletableFuture<ExecutionResult> instrumentExecutionResult(ExecutionResult executionResult,
			InstrumentationExecutionParameters parameters, InstrumentationState state) {
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
