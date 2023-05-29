/** Generated by the default template from graphql-java-generator */
package com.graphql_java_generator.domain.server.allGraphQLCases;

import java.util.HashMap;
import java.util.Map;


import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLScalar;

/**
 *
 * @author generated by graphql-java-generator
 * @see <a href="https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInputType("AllFieldCasesWithIdSubtypeInput")
@SuppressWarnings("unused")
public class AllFieldCasesWithIdSubtypeInput 
{


	public AllFieldCasesWithIdSubtypeInput(){
		// No action
	}

	@GraphQLScalar(fieldName = "id", graphQLTypeSimpleName = "ID", javaClass = java.util.UUID.class)
	java.util.UUID id;


	@GraphQLScalar(fieldName = "name", graphQLTypeSimpleName = "String", javaClass = java.lang.String.class)
	java.lang.String name;



	public void setId(java.util.UUID id) {
		this.id = id;
	}

	public java.util.UUID getId() {
		return id;
	}
		

	public void setName(java.lang.String name) {
		this.name = name;
	}

	public java.lang.String getName() {
		return name;
	}
		

     public String toString() {
        return "AllFieldCasesWithIdSubtypeInput {"
				+ "id: " + id
				+ ", "
				+ "name: " + name
        		+ "}";
    }

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
	 * {@link #builder()}
	 */
	public static class Builder {
		private java.util.UUID id;
		private java.lang.String name;

		public Builder withId(java.util.UUID id) {
			this.id = id;
			return this;
		}
		public Builder withName(java.lang.String name) {
			this.name = name;
			return this;
		}

		public AllFieldCasesWithIdSubtypeInput build() {
			AllFieldCasesWithIdSubtypeInput _object = new AllFieldCasesWithIdSubtypeInput();
			_object.setId(id);
			_object.setName(name);
			return _object;
		}
	}
}