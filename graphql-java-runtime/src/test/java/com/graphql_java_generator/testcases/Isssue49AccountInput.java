/**
 * 
 */
package com.graphql_java_generator.testcases;

import com.graphql_java_generator.annotation.GraphQLScalar;

/**
 * Input Type created to test the
 * <A HREF="https://github.com/graphql-java-generator/graphql-maven-plugin-project/issues/49">Issue 49</A>
 * 
 * @author etienne-sf
 */
public class Isssue49AccountInput {

	@GraphQLScalar(list = false, fieldName = "title", graphQLTypeName = "Issue49Title", javaClass = Issue49Title.class)
	private Issue49Title title;

	public Issue49Title getTitle() {
		return title;
	}

	public void setTitle(Issue49Title title) {
		this.title = title;
	}

}
