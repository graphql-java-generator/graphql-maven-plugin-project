/** Generated by the default template from graphql-java-generator */
package com.graphql_java_generator.domain.client.allGraphQLCases;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.graphql_java_generator.annotation.GraphQLUnionType;


/**
 * @author generated by graphql-java-generator
 * @see <a href="https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "__typename", visible = true)
@JsonSubTypes({ @Type(value = Human.class, name = "Human"), @Type(value = Droid.class, name = "Droid"), @Type(value = Pet.class, name = "Pet") })
@GraphQLUnionType("AnyCharacter")
@SuppressWarnings("unused")
public interface AnyCharacter {

}
