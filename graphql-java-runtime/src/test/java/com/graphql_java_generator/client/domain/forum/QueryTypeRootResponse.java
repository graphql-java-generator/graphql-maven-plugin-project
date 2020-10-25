package com.graphql_java_generator.client.domain.forum;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.client.response.Error;

public class QueryTypeRootResponse {

	@JsonProperty("query")
	@GraphQLNonScalar( fieldName = "QueryType", graphQLTypeSimpleName = "QueryType", javaClass = QueryTypeResponse.class)
	QueryTypeResponse query;

	@JsonProperty("errors")
	@JsonDeserialize(contentAs = Error.class)
	public List<Error> errors;

	public QueryTypeResponse getQueryPascalCase() {
		return query;
	}

	public void setQuery(QueryTypeResponse query) {
		this.query = query;
	}

	public List<Error> getErrors() {
		return errors;
	}

	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}

}
