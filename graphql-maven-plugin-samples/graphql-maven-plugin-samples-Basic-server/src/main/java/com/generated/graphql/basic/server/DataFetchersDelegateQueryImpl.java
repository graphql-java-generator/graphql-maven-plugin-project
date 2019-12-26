package com.generated.graphql.basic.server;

import org.springframework.stereotype.Component;

import com.generated.graphql.DataFetchersDelegateQuery;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateQueryImpl implements DataFetchersDelegateQuery {

	@Override
	public String hello(DataFetchingEnvironment dataFetchingEnvironment, String name) {
		return "Hello" + ((name == null) ? "" : " " + name);
	}

	/**
	 * Error will always thrown an error. This very interesting query is used to check that errors are properly managed,
	 * in integration tests.
	 */
	@Override
	public String error(DataFetchingEnvironment dataFetchingEnvironment) {
		throw new RuntimeException("This is an expected error");
	}

}
