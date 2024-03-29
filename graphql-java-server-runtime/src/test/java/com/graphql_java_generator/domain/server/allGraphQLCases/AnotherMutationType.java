/** Generated by the default template from graphql-java-generator */
package com.graphql_java_generator.domain.server.allGraphQLCases;

import java.util.HashMap;
import java.util.Map;


import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLScalar;

/**
 *
 * @author generated by graphql-java-generator
 * @see <a href="https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLObjectType("AnotherMutationType")
@SuppressWarnings("unused")
public class AnotherMutationType 
{


	public AnotherMutationType(){
		// No action
	}

	@GraphQLNonScalar(fieldName = "createHuman", graphQLTypeSimpleName = "Human", javaClass = Human.class)
	Human createHuman;


	@GraphQLNonScalar(fieldName = "createAllFieldCases", graphQLTypeSimpleName = "AllFieldCases", javaClass = AllFieldCases.class)
	AllFieldCases createAllFieldCases;


	/**
	 *  Tests for issue 51
	 */
	@GraphQLScalar(fieldName = "deleteSnacks", graphQLTypeSimpleName = "Boolean", javaClass = java.lang.Boolean.class)
	java.lang.Boolean deleteSnacks;


	/**
	 *  test for issue #139 (use of java reserved keyword)
	 */
	@GraphQLScalar(fieldName = "if", graphQLTypeSimpleName = "String", javaClass = java.lang.String.class)
	java.lang.String _if;


	@GraphQLScalar(fieldName = "implements", graphQLTypeSimpleName = "String", javaClass = java.lang.String.class)
	java.lang.String _implements;



	public void setCreateHuman(Human createHuman) {
		this.createHuman = createHuman;
	}

	public Human getCreateHuman() {
		return createHuman;
	}
		

	public void setCreateAllFieldCases(AllFieldCases createAllFieldCases) {
		this.createAllFieldCases = createAllFieldCases;
	}

	public AllFieldCases getCreateAllFieldCases() {
		return createAllFieldCases;
	}
		

	/**
	 *  Tests for issue 51
	 */
	public void setDeleteSnacks(java.lang.Boolean deleteSnacks) {
		this.deleteSnacks = deleteSnacks;
	}

	/**
	 *  Tests for issue 51
	*/
	public java.lang.Boolean getDeleteSnacks() {
		return deleteSnacks;
	}
		

	/**
	 *  test for issue #139 (use of java reserved keyword)
	 */
	public void setIf(java.lang.String _if) {
		this._if = _if;
	}

	/**
	 *  test for issue #139 (use of java reserved keyword)
	*/
	public java.lang.String getIf() {
		return _if;
	}
		

	public void setImplements(java.lang.String _implements) {
		this._implements = _implements;
	}

	public java.lang.String getImplements() {
		return _implements;
	}
		

     public String toString() {
        return "AnotherMutationType {"
				+ "createHuman: " + createHuman
				+ ", "
				+ "createAllFieldCases: " + createAllFieldCases
				+ ", "
				+ "deleteSnacks: " + deleteSnacks
				+ ", "
				+ "_if: " + _if
				+ ", "
				+ "_implements: " + _implements
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
		private Human createHuman;
		private AllFieldCases createAllFieldCases;
		private java.lang.Boolean deleteSnacks;
		private java.lang.String _if;
		private java.lang.String _implements;

		public Builder withCreateHuman(Human createHuman) {
			this.createHuman = createHuman;
			return this;
		}
		public Builder withCreateAllFieldCases(AllFieldCases createAllFieldCases) {
			this.createAllFieldCases = createAllFieldCases;
			return this;
		}
		public Builder withDeleteSnacks(java.lang.Boolean deleteSnacks) {
			this.deleteSnacks = deleteSnacks;
			return this;
		}
		public Builder withIf(java.lang.String _if) {
			this._if = _if;
			return this;
		}
		public Builder withImplements(java.lang.String _implements) {
			this._implements = _implements;
			return this;
		}

		public AnotherMutationType build() {
			AnotherMutationType _object = new AnotherMutationType();
			_object.setCreateHuman(createHuman);
			_object.setCreateAllFieldCases(createAllFieldCases);
			_object.setDeleteSnacks(deleteSnacks);
			_object.setIf(_if);
			_object.setImplements(_implements);
			return _object;
		}
	}
}
