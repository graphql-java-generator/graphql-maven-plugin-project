package graphql.java.client.domain.forum;

import java.util.List;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import graphql.java.client.annotation.GraphQLNonScalar;
import graphql.java.client.annotation.GraphQLScalar;

/**
 * @author generated by graphql-maven-plugin
 * @See https://github.com/graphql-java-generator/graphql-java-generator
 */

public class Post  {

	@GraphQLScalar(graphqlType = String.class)
	String id;
	
	@GraphQLScalar(graphqlType = String.class)
	String date;
	
	@GraphQLNonScalar(graphqlType = Member.class)
	Member author;
	
	@GraphQLScalar(graphqlType = Boolean.class)
	Boolean publiclyAvailable;
	
	@GraphQLScalar(graphqlType = String.class)
	String title;
	
	@GraphQLScalar(graphqlType = String.class)
	String content;
	

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	public void setDate(String date) {
		this.date = date;
	}

	public String getDate() {
		return date;
	}
	
	public void setAuthor(Member author) {
		this.author = author;
	}

	public Member getAuthor() {
		return author;
	}
	
	public void setPubliclyAvailable(Boolean publiclyAvailable) {
		this.publiclyAvailable = publiclyAvailable;
	}

	public Boolean getPubliclyAvailable() {
		return publiclyAvailable;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}
	
    public String toString() {
        return "Post {"
				+ "id: " + id
				+ ", "
				+ "date: " + date
				+ ", "
				+ "author: " + author
				+ ", "
				+ "publiclyAvailable: " + publiclyAvailable
				+ ", "
				+ "title: " + title
				+ ", "
				+ "content: " + content
        		+ "}";
    }
}