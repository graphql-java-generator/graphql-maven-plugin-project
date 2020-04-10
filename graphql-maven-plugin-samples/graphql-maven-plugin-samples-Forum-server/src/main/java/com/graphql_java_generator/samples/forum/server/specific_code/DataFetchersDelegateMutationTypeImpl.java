/**
 * 
 */
package com.graphql_java_generator.samples.forum.server.specific_code;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.graphql_java_generator.samples.forum.server.Board;
import com.graphql_java_generator.samples.forum.server.DataFetchersDelegateMutationType;
import com.graphql_java_generator.samples.forum.server.Post;
import com.graphql_java_generator.samples.forum.server.PostInput;
import com.graphql_java_generator.samples.forum.server.Topic;
import com.graphql_java_generator.samples.forum.server.TopicInput;
import com.graphql_java_generator.samples.forum.server.jpa.BoardRepository;
import com.graphql_java_generator.samples.forum.server.jpa.PostRepository;
import com.graphql_java_generator.samples.forum.server.jpa.TopicRepository;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author etienne-sf
 */
@Component
public class DataFetchersDelegateMutationTypeImpl implements DataFetchersDelegateMutationType {

	@Resource
	BoardRepository boardRepository;
	@Resource
	TopicRepository topicRepository;
	@Resource
	PostRepository postRepository;

	@Override
	public Board createBoard(DataFetchingEnvironment dataFetchingEnvironment, String name, Boolean publiclyAvailable) {
		Board board = new Board();
		board.setName(name);
		if (publiclyAvailable != null) {
			board.setPubliclyAvailable(publiclyAvailable);
		}
		boardRepository.save(board);
		return board;
	}

	@Override
	public Topic createTopic(DataFetchingEnvironment dataFetchingEnvironment, TopicInput topicInput) {
		Topic newTopic = new Topic();
		newTopic.setBoardId(topicInput.getBoardId());
		newTopic.setAuthorId(topicInput.getInput().getAuthorId());
		newTopic.setPubliclyAvailable(topicInput.getInput().getPubliclyAvailable());
		newTopic.setDate(topicInput.getInput().getDate());
		newTopic.setTitle(topicInput.getInput().getTitle());
		newTopic.setContent(topicInput.getInput().getContent());
		topicRepository.save(newTopic);
		return newTopic;
	}

	@Override
	public Post createPost(DataFetchingEnvironment dataFetchingEnvironment, PostInput postParam) {
		Post newPost = new Post();
		newPost.setTopicId(postParam.getTopicId());
		newPost.setAuthorId(postParam.getInput().getAuthorId());
		newPost.setPubliclyAvailable(postParam.getInput().getPubliclyAvailable());
		newPost.setDate(postParam.getInput().getDate());
		newPost.setTitle(postParam.getInput().getTitle());
		newPost.setContent(postParam.getInput().getContent());
		postRepository.save(newPost);
		return newPost;
	}

	@Override
	public List<Post> createPosts(DataFetchingEnvironment dataFetchingEnvironment, List<PostInput> spam) {
		// Actually, this mutation is for sample only. We don't want to implement it !
		// :)
		throw new RuntimeException("Spamming is forbidden");
	}

}
