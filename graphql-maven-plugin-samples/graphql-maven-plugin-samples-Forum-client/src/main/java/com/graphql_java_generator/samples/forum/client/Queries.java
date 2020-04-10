package com.graphql_java_generator.samples.forum.client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Board;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Member;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Post;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.PostInput;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Topic;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.TopicInput;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.TopicPostInput;

/**
 * This interface contains the queries that will be defined in this sample. The queries are defined in the package
 * com.graphql_java_generator.samples.forum.client.graphql, by three classes. These three classes show the three ways to
 * build and execute GraphQL Request with graphql-java-generator
 * 
 * @author etienne-sf
 *
 */
public interface Queries {

	static final String DATE_FORMAT = "yyyy-MM-dd";
	static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

	List<Board> boardsSimple() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException;

	List<Board> boardsAndTopicsWithFieldParameter(Date since)
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException;

	List<Topic> topicAuthorPostAuthor(String boardName, Date since)
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException;

	List<Topic> findTopics(String boardName, List<String> keyword)
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException;

	Board createBoard(String name, boolean publiclyAvailable)
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException;

	Topic createTopic(TopicInput topicInput)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	public Post createPost(PostInput input) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	public List<Post> createPosts(List<PostInput> input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	/** A utility method to create the TopicPostInput Type, from its values */
	default TopicPostInput getTopicPostInput(Member author, Date date, boolean publiclyAvailable, String title,
			String content) {
		TopicPostInput input = new TopicPostInput();
		input.setAuthorId(author.getId());
		input.setDate(date);
		input.setPubliclyAvailable(publiclyAvailable);
		input.setTitle(title);
		input.setContent(content);
		return input;
	}

	/** A utility method to create the TopicInput Type, from its values */
	default TopicInput getTopicInput(Board board, Member author, Date date, boolean publiclyAvailable, String title,
			String content) {
		TopicInput input = new TopicInput();
		input.setBoardId(board.getId());
		input.setInput(getTopicPostInput(author, date, publiclyAvailable, title, content));
		return input;
	}

	/** A utility method to create the PostInput Type, from its values */
	default PostInput getPostInput(Topic topic, Member author, Date date, boolean publiclyAvailable, String title,
			String content) {
		PostInput input = new PostInput();
		input.setTopicId(topic.getId());
		input.setInput(getTopicPostInput(author, date, publiclyAvailable, title, content));
		return input;
	}

}