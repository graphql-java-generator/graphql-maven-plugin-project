/**
 * 
 */
package org.allGraphQLCases.server.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.language.SourceLocation;
import graphql.schema.DataFetchingEnvironment;

/**
 * A Spring bean that register {@link DataFetcherExceptionResolverAdapter} to configure the way Java exceptions are
 * mapped to GraphQL errors.<br/>
 * This class is both a sample, and the support for an IT test that checks proper error management on client side, when
 * an error occurs on server side.
 * 
 * @author etienne-sf
 */
@Component
public class MyDataFetcherExceptionResolverAdapter extends DataFetcherExceptionResolverAdapter {

	@Override
	protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {

		if (!(ex instanceof GraphQlException)) {
			return null;
		} else if (ex.getMessage().contains("add an extension")) {
			Map<String, Object> extensions = new HashMap<>();
			extensions.put("An error extension", "An error extension's value (MyInstrumentation)");
			extensions.put("Another error extension", "Another error extension's value (MyInstrumentation)");

			return GraphqlErrorBuilder.newError()//
					.errorType(ErrorType.INTERNAL_ERROR)//
					.message(ex.getMessage())//
					.extensions(extensions)//
					.build();

		} else if (ex.getMessage().contains("add a SourceLocation")) {
			return GraphqlErrorBuilder.newError()//
					.errorType(ErrorType.INTERNAL_ERROR)//
					.message(ex.getMessage())//
					.locations(Arrays.asList(//
							new SourceLocation(11, 111, "A source name"), // The sourceName will be cleared somewhere
							new SourceLocation(22, 222, "Another source name")))// The sourceName will be cleared
																				// somewhere
					.build();
		} else {
			return (GraphQlException) ex;
		}
	}

}
