/** Generated by the default template from graphql-java-generator */
package com.graphql_java_generator.domain.server.allGraphQLCases;

import com.graphql_java_generator.annotation.GraphQLInterfaceType;
import com.graphql_java_generator.annotation.GraphQLScalar;

/**
 * ############################
 *  test for issue #130: test for all possible 'extend' use
 * <BR/>
 * 
 * @author generated by graphql-java-generator
 * @see <a href="https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInterfaceType("interfaceToTestExtendKeyword")
@SuppressWarnings("unused")
public interface interfaceToTestExtendKeyword  {

	@GraphQLScalar(fieldName = "extendedField", graphQLTypeSimpleName = "String", javaClass = java.lang.String.class)
	public void setExtendedField(java.lang.String extendedField);

	@GraphQLScalar(fieldName = "extendedField", graphQLTypeSimpleName = "String", javaClass = java.lang.String.class)
	public java.lang.String getExtendedField();
}
