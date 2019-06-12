package com.generated.graphql.specific;

import org.springframework.stereotype.Component;

import com.generated.graphql.QueryDataFetchersDelegate;

import graphql.schema.DataFetchingEnvironment;

@Component
public class QueryDataFetchersDelegateImpl implements QueryDataFetchersDelegate {

	@Override
	public String hello(DataFetchingEnvironment dataFetchingEnvironment, String name) {
		return "Hello" + ((name == null) ? "" : " " + name);
	}

}
