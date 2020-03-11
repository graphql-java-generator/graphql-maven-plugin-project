package ${pluginConfiguration.packageName};

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.client.response.Error;

public class ${object.javaName}RootResponse {

	@JsonProperty("query")
	@GraphQLNonScalar(graphQLTypeName = "${object.javaName}", javaClass = ${object.javaName}Response.class)
	${object.javaName}Response query;

	@JsonProperty("errors")
	@JsonDeserialize(contentAs = Error.class)
	public List<Error> errors;

	public ${object.javaName}Response getQuery() {
		return query;
	}

	public void setQuery(${object.javaName}Response query) {
		this.query = query;
	}

	public List<Error> getErrors() {
		return errors;
	}

	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}

}
