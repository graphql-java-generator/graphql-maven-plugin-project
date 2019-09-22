package com.graphql_java_generator.samples.basic.server;

import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class QueryDataFetchersDelegateImpl implements QueryDataFetchersDelegate {

	@Override
	public String hello(DataFetchingEnvironment dataFetchingEnvironment, String name) {
		return "Hello" + ((name == null) ? "" : " " + name);
	}

}
