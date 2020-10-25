/** Generated by the default template from graphql-java-generator */
package com.graphql_java_generator.client.domain.allGraphQLCases;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graphql_java_generator.GraphQLField;
import com.graphql_java_generator.annotation.GraphQLInputParameters;
import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLScalar;

/**
 * @author generated by graphql-java-generator
 * @see <a href="https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInputType("class")
public class _class  {

	public _class(){
		// No action
	}

	@JsonProperty("default")
	@GraphQLScalar( fieldName = "default", graphQLTypeSimpleName = "String", javaClass = String.class)
	String _default;



	public void setDefault(String _default) {
		this._default = _default;
	}

	public String getDefault() {
		return _default;
	}

    public String toString() {
        return "_class {"
				+ "_default: " + _default
        		+ "}";
    }

    /**
	 * Enum of field names
	 */
	 public static enum Field implements GraphQLField {
		Default("default");

		private String fieldName;

		Field(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getFieldName() {
			return fieldName;
		}

		public Class<?> getGraphQLType() {
			return this.getClass().getDeclaringClass();
		}

	}

	public static Builder builder() {
			return new Builder();
		}



	/**
	 * Builder
	 */
	public static class Builder {
		private String _default;


		public Builder withDefault(String _default) {
			this._default = _default;
			return this;
		}

		public _class build() {
			_class _object = new _class();
			_object.setDefault(_default);
			return _object;
		}
	}
}
