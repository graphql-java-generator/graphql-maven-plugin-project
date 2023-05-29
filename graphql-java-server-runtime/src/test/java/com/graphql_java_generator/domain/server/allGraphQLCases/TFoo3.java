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

@GraphQLObjectType("TFoo3")
@SuppressWarnings("unused")
public class TFoo3 
	implements IFoo3, IFoo1, IFoo2{


	public TFoo3(){
		// No action
	}

	
	
	@GraphQLScalar(fieldName = "id", graphQLTypeSimpleName = "ID", javaClass = java.util.UUID.class)
	java.util.UUID id;


	
	@GraphQLNonScalar(fieldName = "bar", graphQLTypeSimpleName = "TBar12", javaClass = TBar12.class)
	TBar12 bar;




	/**
	 */
	@Override
	public void setId(java.util.UUID id) {
		if (id == null || id instanceof java.util.UUID) {
			this.id = (java.util.UUID) id;
		} else {
			throw new IllegalArgumentException("The given id should be an instance of java.util.UUID, but is an instance of "
					+ id.getClass().getName());
		}
	}
 

	/**
	 */
	@Override
	public java.util.UUID getId() {
		return id;
	}


	/**
	 */
	@Override
	public void setBar(IBar12 bar) {
		if (bar == null || bar instanceof TBar12) {
			this.bar = (TBar12) bar;
		} else {
			throw new IllegalArgumentException("The given bar should be an instance of TBar12, but is an instance of "
					+ bar.getClass().getName());
		}
	}

	/**
	 */
	@Override
	public void setBar(IBar2 bar) {
		if (bar == null || bar instanceof TBar12) {
			this.bar = (TBar12) bar;
		} else {
			throw new IllegalArgumentException("The given bar should be an instance of TBar12, but is an instance of "
					+ bar.getClass().getName());
		}
	}

	/**
	 */
	@Override
	public void setBar(IBar1 bar) {
		if (bar == null || bar instanceof TBar12) {
			this.bar = (TBar12) bar;
		} else {
			throw new IllegalArgumentException("The given bar should be an instance of TBar12, but is an instance of "
					+ bar.getClass().getName());
		}
	}
 

	/** 
	 * As the type declared in the class is not inherited from one of the implemented interfaces, we need a dedicated setter.
	 * 
	 * @param
	 */
	public void setBar(TBar12 bar) {
		this.bar = bar;
	}

	/**
	 */
	@Override
	public TBar12 getBar() {
		return bar;
	}

     public String toString() {
        return "TFoo3 {"
				+ "id: " + id
				+ ", "
				+ "bar: " + bar
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
		private TBar12 bar;

		public Builder withId(java.util.UUID id) {
			this.id = id;
			return this;
		}
		public Builder withBar(TBar12 bar) {
			this.bar = bar;
			return this;
		}

		public TFoo3 build() {
			TFoo3 _object = new TFoo3();
			_object.setId(id);
			_object.setBar(bar);
			return _object;
		}
	}
}