/** This template is custom **/
package ${pluginConfiguration.packageName};

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.client.response.Error;

public class ${object.javaName}RootResponse {

	@JsonProperty("${object.requestType}")
	@GraphQLNonScalar(fieldName = "${object.name}", graphQLTypeName = "${object.javaName}", javaClass = ${object.javaName}Response.class)
	${object.javaName}Response ${object.requestType};

	@JsonProperty("errors")
	@JsonDeserialize(contentAs = Error.class)
	public List<Error> errors;

	public ${object.javaName}Response get${object.requestTypePascalCase}PascalCase() {
		return ${object.requestType};
	}

	public void set${object.requestTypePascalCase}(${object.javaName}Response ${object.requestType}) {
		this.${object.requestType} = ${object.requestType};
	}

	public List<Error> getErrors() {
		return errors;
	}

	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}

}
