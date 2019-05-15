package graphql.java.client.domain.forum;

import java.util.List;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import graphql.java.client.annotation.GraphQLNonScalar;
import graphql.java.client.annotation.GraphQLScalar;

/**
 * @author generated by graphql-maven-plugin
 * @See https://github.com/graphql-java-generator/graphql-java-generator
 */

public class Member  {

	@GraphQLScalar(graphqlType = String.class)
	String id;
	
	@GraphQLScalar(graphqlType = String.class)
	String name;
	
	@GraphQLScalar(graphqlType = String.class)
	String alias;
	
	@GraphQLScalar(graphqlType = String.class)
	String email;
	
	@GraphQLScalar(graphqlType = MemberType.class)
	MemberType type;
	

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}
	
	public void setType(MemberType type) {
		this.type = type;
	}

	public MemberType getType() {
		return type;
	}
	
    public String toString() {
        return "Member {"
				+ "id: " + id
				+ ", "
				+ "name: " + name
				+ ", "
				+ "alias: " + alias
				+ ", "
				+ "email: " + email
				+ ", "
				+ "type: " + type
        		+ "}";
    }
}
