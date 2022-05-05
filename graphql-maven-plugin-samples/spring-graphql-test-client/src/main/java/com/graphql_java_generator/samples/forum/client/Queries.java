package com.graphql_java_generator.samples.forum.client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.forum.generated.Board;
import org.forum.generated.Member;
import org.forum.generated.MemberInput;
import org.forum.generated.Post;
import org.forum.generated.PostInput;
import org.forum.generated.Topic;
import org.forum.generated.TopicInput;
import org.forum.generated.TopicPostInput;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

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

	public Member createMember(MemberInput input)
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