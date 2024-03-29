/** Generated by the default template from graphql-java-generator */
package com.graphql_java_generator.domain.server.allGraphQLCases;

import com.graphql_java_generator.annotation.GraphQLInterfaceType;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLScalar;
import java.util.List;




/**
 * @author generated by graphql-java-generator
 * @see <a href="https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInterfaceType("Character")
@SuppressWarnings("unused")
public interface Character  {

	
	
	@GraphQLScalar(fieldName = "id", graphQLTypeSimpleName = "ID", javaClass = java.util.UUID.class)
	public void setId(java.util.UUID id);

	
	
	@GraphQLScalar(fieldName = "id", graphQLTypeSimpleName = "ID", javaClass = java.util.UUID.class)
	public java.util.UUID getId();

	@GraphQLScalar(fieldName = "name", graphQLTypeSimpleName = "String", javaClass = java.lang.String.class)
	public void setName(java.lang.String name);

	@GraphQLScalar(fieldName = "name", graphQLTypeSimpleName = "String", javaClass = java.lang.String.class)
	public java.lang.String getName();

	
	@GraphQLNonScalar(fieldName = "friends", graphQLTypeSimpleName = "Character", javaClass = Character.class)
	public void setFriends(List<Character> friends);

	
	@GraphQLNonScalar(fieldName = "friends", graphQLTypeSimpleName = "Character", javaClass = Character.class)
	public List<Character> getFriends();

	
	@GraphQLScalar(fieldName = "appearsIn", graphQLTypeSimpleName = "Episode", javaClass = Episode.class)
	public void setAppearsIn(List<Episode> appearsIn);

	
	@GraphQLScalar(fieldName = "appearsIn", graphQLTypeSimpleName = "Episode", javaClass = Episode.class)
	public List<Episode> getAppearsIn();
}
