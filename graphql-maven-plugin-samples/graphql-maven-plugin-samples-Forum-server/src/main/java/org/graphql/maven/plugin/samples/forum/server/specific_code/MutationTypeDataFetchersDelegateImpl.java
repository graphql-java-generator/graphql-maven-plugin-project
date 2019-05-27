/**
 * 
 */
package org.graphql.maven.plugin.samples.forum.server.specific_code;

import java.util.UUID;

import javax.annotation.Resource;

import org.graphql.maven.plugin.samples.forum.server.Board;
import org.graphql.maven.plugin.samples.forum.server.MutationTypeDataFetchersDelegate;
import org.graphql.maven.plugin.samples.forum.server.Post;
import org.graphql.maven.plugin.samples.forum.server.Topic;
import org.graphql.maven.plugin.samples.forum.server.jpa.BoardRepository;
import org.graphql.maven.plugin.samples.forum.server.jpa.PostRepository;
import org.graphql.maven.plugin.samples.forum.server.jpa.TopicRepository;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author EtienneSF
 */
@Component
public class MutationTypeDataFetchersDelegateImpl implements MutationTypeDataFetchersDelegate {

	@Resource
	BoardRepository boardRepository;
	@Resource
	TopicRepository topicRepository;
	@Resource
	PostRepository postRepository;

	@Override
	public Board mutationTypeCreateBoard(DataFetchingEnvironment dataFetchingEnvironment, String name,
			Boolean publiclyAvailable) {
		Board board = new Board();
		board.setName(name);
		if (publiclyAvailable != null) {
			board.setPubliclyAvailable(publiclyAvailable);
		}
		boardRepository.save(board);
		return board;
	}

	@Override
	public Topic mutationTypeCreateTopic(DataFetchingEnvironment dataFetchingEnvironment, UUID authorId,
			Boolean publiclyAvailable, String title, String content) {
		Topic topic = new Topic();
		topic.setAuthorId(authorId);
		topic.setPubliclyAvailable(publiclyAvailable);
		topic.setTitle(title);
		topic.setContent(content);
		topicRepository.save(topic);
		return topic;
	}

	@Override
	public Post mutationTypeCreatePost(DataFetchingEnvironment dataFetchingEnvironment, UUID authorId,
			Boolean publiclyAvailable, String title, String content) {
		Post post = new Post();
		post.setAuthorId(authorId);
		post.setPubliclyAvailable(publiclyAvailable);
		post.setTitle(title);
		post.setContent(content);
		postRepository.save(post);
		return post;
	}

}
