package graphql.spring.web.servlet.components;

import java.util.concurrent.CompletableFuture;

import org.dataloader.DataLoaderRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.Internal;
import graphql.spring.web.servlet.ExecutionInputCustomizer;
import graphql.spring.web.servlet.GraphQLInvocation;
import graphql.spring.web.servlet.GraphQLInvocationData;
import graphql.spring.web.servlet.OnDemandDataLoaderRegistry;

@Component
@Internal
public class DefaultGraphQLInvocation implements GraphQLInvocation {

	/** The logger for this instance */
	protected Logger logger = LoggerFactory.getLogger(DefaultGraphQLInvocation.class);

	@Autowired
	GraphQL graphQL;

	@Autowired(required = false)
	DataLoaderRegistry dataLoaderRegistry;

	@Autowired(required = false)
	OnDemandDataLoaderRegistry onDemandDataLoaderRegistry;

	@Autowired
	ExecutionInputCustomizer executionInputCustomizer;

	@Override
	public CompletableFuture<ExecutionResult> invoke(GraphQLInvocationData invocationData, WebRequest webRequest) {
		ExecutionInput.Builder executionInputBuilder = ExecutionInput.newExecutionInput()
				.query(invocationData.getQuery()).operationName(invocationData.getOperationName())
				.variables(invocationData.getVariables());
		if (onDemandDataLoaderRegistry != null) {
			logger.debug("Using the provided onDemandDataLoaderRegistry");
			executionInputBuilder.dataLoaderRegistry(onDemandDataLoaderRegistry.getNewDataLoaderRegistry());
		} else if (dataLoaderRegistry != null) {
			logger.debug("Using the provided dataLoaderRegistry");
			executionInputBuilder.dataLoaderRegistry(dataLoaderRegistry);
		} else {
			logger.debug("No dataLoaderRegistry has been defined");
		}
		ExecutionInput executionInput = executionInputBuilder.build();
		CompletableFuture<ExecutionInput> customizedExecutionInput = executionInputCustomizer
				.customizeExecutionInput(executionInput, webRequest);
		return customizedExecutionInput.thenCompose(graphQL::executeAsync);
	}

}
