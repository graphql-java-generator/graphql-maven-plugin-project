/**
 * 
 */
package org.forum.server.specific_code;

import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;

import graphql.execution.AsyncExecutionStrategy;

/**
 * A custom {@link DataFetcherExceptionResolver} to demonstrate how to manage errors on server side. <br/>
 * It's only role is to transmit the exception message, so that we can check it on server side (this is an integration
 * test, that check proper exception management for the client).
 * 
 * @author etienne-sf
 */
@Configuration
public class CustomExceptionHandlerRegistring {

	@Bean
	public GraphQlSourceBuilderCustomizer sourceBuilderCustomizer() {
		return (builder) -> {
			builder.configureGraphQl(graphQlBuilder -> graphQlBuilder
					.queryExecutionStrategy(new AsyncExecutionStrategy(new CustomExceptionHandler())));
			builder.configureGraphQl(graphQlBuilder -> graphQlBuilder
					.mutationExecutionStrategy(new AsyncExecutionStrategy(new CustomExceptionHandler())));
		};
	}
}
