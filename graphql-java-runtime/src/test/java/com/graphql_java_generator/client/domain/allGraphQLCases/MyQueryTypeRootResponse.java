package com.graphql_java_generator.client.domain.allGraphQLCases;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.client.response.Error;

public class MyQueryTypeRootResponse {

	@JsonProperty("query")
	@GraphQLNonScalar(graphQLTypeName = "MyQueryType", javaClass = MyQueryTypeResponse.class)
	MyQueryTypeResponse query;

	@JsonProperty("errors")
	@JsonDeserialize(contentAs = Error.class)
	public List<Error> errors;

	public MyQueryTypeResponse getQuery() {
		return query;
	}

	public void setQuery(MyQueryTypeResponse query) {
		this.query = query;
	}

	public List<Error> getErrors() {
		return errors;
	}

	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}

}
