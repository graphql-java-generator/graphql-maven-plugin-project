/** Generated by the default template from graphql-java-generator */
package com.graphql_java_generator.server.domain.forum;

import java.util.List;

import com.graphql_java_generator.GraphQLField;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLObjectType;

/**
 * @author generated by graphql-java-generator
 * @see <a href=
 *      "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLObjectType("MutationType")
public class MutationType {

	public MutationType() {
		// No action
	}

	@GraphQLNonScalar(fieldName = "createBoard", graphQLTypeSimpleName = "Board",  javaClass = Board.class)
	Board createBoard;

	@GraphQLNonScalar(fieldName = "createTopic", graphQLTypeSimpleName = "Topic",  javaClass = Topic.class)
	Topic createTopic;

	@GraphQLNonScalar(fieldName = "createPost", graphQLTypeSimpleName = "Post",  javaClass = Post.class)
	Post createPost;

	@GraphQLNonScalar(fieldName = "createPosts", graphQLTypeSimpleName = "Post",  javaClass = Post.class)
	List<Post> createPosts;

	public void setCreateBoard(Board createBoard) {
		this.createBoard = createBoard;
	}

	public Board getCreateBoard() {
		return createBoard;
	}

	public void setCreateTopic(Topic createTopic) {
		this.createTopic = createTopic;
	}

	public Topic getCreateTopic() {
		return createTopic;
	}

	public void setCreatePost(Post createPost) {
		this.createPost = createPost;
	}

	public Post getCreatePost() {
		return createPost;
	}

	public void setCreatePosts(List<Post> createPosts) {
		this.createPosts = createPosts;
	}

	public List<Post> getCreatePosts() {
		return createPosts;
	}

	@Override
	public String toString() {
		return "MutationType {" + "createBoard: " + createBoard + ", " + "createTopic: " + createTopic + ", "
				+ "createPost: " + createPost + ", " + "createPosts: " + createPosts + "}";
	}

	/**
	 * Enum of field names
	 */
	public static enum Field implements GraphQLField {
		CreateBoard("createBoard"), CreateTopic("createTopic"), CreatePost("createPost"), CreatePosts("createPosts");

		private String fieldName;

		Field(String fieldName) {
			this.fieldName = fieldName;
		}

		@Override
		public String getFieldName() {
			return fieldName;
		}

		@Override
		public Class<?> getGraphQLType() {
			return this.getClass().getDeclaringClass();
		}

	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder
	 */
	public static class Builder {
		private Board createBoard;
		private Topic createTopic;
		private Post createPost;
		private List<Post> createPosts;

		public Builder withCreateBoard(Board createBoard) {
			this.createBoard = createBoard;
			return this;
		}

		public Builder withCreateTopic(Topic createTopic) {
			this.createTopic = createTopic;
			return this;
		}

		public Builder withCreatePost(Post createPost) {
			this.createPost = createPost;
			return this;
		}

		public Builder withCreatePosts(List<Post> createPosts) {
			this.createPosts = createPosts;
			return this;
		}

		public MutationType build() {
			MutationType _object = new MutationType();
			_object.setCreateBoard(createBoard);
			_object.setCreateTopic(createTopic);
			_object.setCreatePost(createPost);
			_object.setCreatePosts(createPosts);
			return _object;
		}
	}
}
