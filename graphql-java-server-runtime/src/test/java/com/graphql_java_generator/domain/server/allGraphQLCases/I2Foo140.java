/** Generated by the default template from graphql-java-generator */
package com.graphql_java_generator.domain.server.allGraphQLCases;

import com.graphql_java_generator.annotation.GraphQLInterfaceType;
import com.graphql_java_generator.annotation.GraphQLNonScalar;

/**
 * @author generated by graphql-java-generator
 * @see <a href="https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInterfaceType("I2Foo140")
@SuppressWarnings("unused")
public interface I2Foo140 extends IFoo140 {

	@GraphQLNonScalar(fieldName = "bar", graphQLTypeSimpleName = "I2Bar140", javaClass = I2Bar140.class)
	public void setBar(I2Bar140 bar);

	@GraphQLNonScalar(fieldName = "bar", graphQLTypeSimpleName = "I2Bar140", javaClass = I2Bar140.class)
	public I2Bar140 getBar();
}