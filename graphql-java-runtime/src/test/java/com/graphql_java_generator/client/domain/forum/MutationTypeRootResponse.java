package com.graphql_java_generator.client.domain.forum;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.client.response.Error;

public class MutationTypeRootResponse {

	@JsonProperty("mutation")
	@GraphQLNonScalar(fieldName = "MutationType", graphQLTypeName = "MutationType", javaClass = MutationTypeResponse.class)
	MutationTypeResponse mutation;

	@JsonProperty("errors")
	@JsonDeserialize(contentAs = Error.class)
	public List<Error> errors;

	public MutationTypeResponse getMutationPascalCase() {
		return mutation;
	}

	public void setMutation(MutationTypeResponse mutation) {
		this.mutation = mutation;
	}

	public List<Error> getErrors() {
		return errors;
	}

	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}

}
