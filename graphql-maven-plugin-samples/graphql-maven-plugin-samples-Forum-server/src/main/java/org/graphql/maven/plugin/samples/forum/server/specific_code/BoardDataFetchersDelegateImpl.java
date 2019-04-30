/**
 * 
 */
package org.graphql.maven.plugin.samples.forum.server.specific_code;

import javax.annotation.Resource;

import org.graphql.maven.plugin.samples.forum.server.Board;
import org.graphql.maven.plugin.samples.forum.server.BoardDataFetchersDelegate;
import org.graphql.maven.plugin.samples.forum.server.Topic;
import org.graphql.maven.plugin.samples.forum.server.jpa.TopicRepository;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * This class implements the access to the database : there are so many ways to do this, that the developper has still
 * work to do. But all the GraphQL boilerplate is generated.<BR/>
 * The {@link GraphQLDataFetchersDelegate} interface is generated from the given schema
 * 
 * @author EtienneSF
 */
@Component
public class BoardDataFetchersDelegateImpl implements BoardDataFetchersDelegate {

	@Resource
	TopicRepository topicRepository;

	@Override
	public Iterable<Topic> boardTopics(DataFetchingEnvironment dataFetchingEnvironment, Board board, String since) {
		return topicRepository.findByBoardId(board.getId());
	}
}
