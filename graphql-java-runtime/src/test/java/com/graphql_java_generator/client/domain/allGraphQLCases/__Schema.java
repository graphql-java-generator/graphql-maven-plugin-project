/** Generated by the default template from graphql-java-generator */
package com.graphql_java_generator.client.domain.allGraphQLCases;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphql_java_generator.GraphQLField;
import com.graphql_java_generator.annotation.GraphQLInputParameters;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLScalar;
import java.util.List;

/**
 * @author generated by graphql-java-generator
 * @see <a href="https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLObjectType("__Schema")
public class __Schema  {

	public __Schema(){
		// No action
	}

	@JsonProperty("description")
	@GraphQLScalar( fieldName = "description", graphQLTypeSimpleName = "String", javaClass = String.class)
	String description;


	@JsonDeserialize(contentAs = __Type.class)
	@JsonProperty("types")
	@GraphQLNonScalar( fieldName = "types", graphQLTypeSimpleName = "__Type", javaClass = __Type.class)
	List<__Type> types;


	@JsonProperty("queryType")
	@GraphQLNonScalar( fieldName = "queryType", graphQLTypeSimpleName = "__Type", javaClass = __Type.class)
	__Type queryType;


	@JsonProperty("mutationType")
	@GraphQLNonScalar( fieldName = "mutationType", graphQLTypeSimpleName = "__Type", javaClass = __Type.class)
	__Type mutationType;


	@JsonProperty("subscriptionType")
	@GraphQLNonScalar( fieldName = "subscriptionType", graphQLTypeSimpleName = "__Type", javaClass = __Type.class)
	__Type subscriptionType;


	@JsonDeserialize(contentAs = __Directive.class)
	@JsonProperty("directives")
	@GraphQLNonScalar( fieldName = "directives", graphQLTypeSimpleName = "__Directive", javaClass = __Directive.class)
	List<__Directive> directives;


	@JsonProperty("__typename")
	@GraphQLScalar( fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class)
	String __typename;



	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setTypes(List<__Type> types) {
		this.types = types;
	}

	public List<__Type> getTypes() {
		return types;
	}

	public void setQueryType(__Type queryType) {
		this.queryType = queryType;
	}

	public __Type getQueryType() {
		return queryType;
	}

	public void setMutationType(__Type mutationType) {
		this.mutationType = mutationType;
	}

	public __Type getMutationType() {
		return mutationType;
	}

	public void setSubscriptionType(__Type subscriptionType) {
		this.subscriptionType = subscriptionType;
	}

	public __Type getSubscriptionType() {
		return subscriptionType;
	}

	public void setDirectives(List<__Directive> directives) {
		this.directives = directives;
	}

	public List<__Directive> getDirectives() {
		return directives;
	}

	public void set__typename(String __typename) {
		this.__typename = __typename;
	}

	public String get__typename() {
		return __typename;
	}

    public String toString() {
        return "__Schema {"
				+ "description: " + description
				+ ", "
				+ "types: " + types
				+ ", "
				+ "queryType: " + queryType
				+ ", "
				+ "mutationType: " + mutationType
				+ ", "
				+ "subscriptionType: " + subscriptionType
				+ ", "
				+ "directives: " + directives
				+ ", "
				+ "__typename: " + __typename
        		+ "}";
    }

    /**
	 * Enum of field names
	 */
	 public static enum Field implements GraphQLField {
		Description("description"),
		Types("types"),
		QueryType("queryType"),
		MutationType("mutationType"),
		SubscriptionType("subscriptionType"),
		Directives("directives"),
		__typename("__typename");

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
		private String description;
		private List<__Type> types;
		private __Type queryType;
		private __Type mutationType;
		private __Type subscriptionType;
		private List<__Directive> directives;


		public Builder withDescription(String description) {
			this.description = description;
			return this;
		}
		public Builder withTypes(List<__Type> types) {
			this.types = types;
			return this;
		}
		public Builder withQueryType(__Type queryType) {
			this.queryType = queryType;
			return this;
		}
		public Builder withMutationType(__Type mutationType) {
			this.mutationType = mutationType;
			return this;
		}
		public Builder withSubscriptionType(__Type subscriptionType) {
			this.subscriptionType = subscriptionType;
			return this;
		}
		public Builder withDirectives(List<__Directive> directives) {
			this.directives = directives;
			return this;
		}

		public __Schema build() {
			__Schema _object = new __Schema();
			_object.setDescription(description);
			_object.setTypes(types);
			_object.setQueryType(queryType);
			_object.setMutationType(mutationType);
			_object.setSubscriptionType(subscriptionType);
			_object.setDirectives(directives);
			_object.set__typename("__Schema");
			return _object;
		}
	}
}
