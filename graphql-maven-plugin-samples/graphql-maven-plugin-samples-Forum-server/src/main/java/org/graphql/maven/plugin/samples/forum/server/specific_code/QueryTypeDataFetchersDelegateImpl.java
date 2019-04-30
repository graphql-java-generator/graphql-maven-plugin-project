/**
 * 
 */
package org.graphql.maven.plugin.samples.forum.server.specific_code;

import javax.annotation.Resource;

import org.graphql.maven.plugin.samples.forum.server.Board;
import org.graphql.maven.plugin.samples.forum.server.QueryTypeDataFetchersDelegate;
import org.graphql.maven.plugin.samples.forum.server.Topic;
import org.graphql.maven.plugin.samples.forum.server.jpa.BoardRepository;
import org.graphql.maven.plugin.samples.forum.server.jpa.TopicRepository;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author EtienneSF
 */
@Component
public class QueryTypeDataFetchersDelegateImpl implements QueryTypeDataFetchersDelegate {

	@Resource
	BoardRepository boardRepository;
	@Resource
	TopicRepository topicRepository;

	@Override
	public Iterable<Board> queryTypeBoards(DataFetchingEnvironment dataFetchingEnvironment) {
		return boardRepository.findAll();
	}

	@Override
	public Iterable<Topic> queryTypeTopics(DataFetchingEnvironment dataFetchingEnvironment, String boardName) {
		return topicRepository.findByBoardName(boardName);
	}

}
