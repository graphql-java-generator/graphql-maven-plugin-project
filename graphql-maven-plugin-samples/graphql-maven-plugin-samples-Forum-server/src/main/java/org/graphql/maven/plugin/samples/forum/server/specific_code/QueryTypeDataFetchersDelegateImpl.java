/**
 * 
 */
package org.graphql.maven.plugin.samples.forum.server.specific_code;

import java.util.List;

import javax.annotation.Resource;

import org.graphql.maven.plugin.samples.forum.server.Board;
import org.graphql.maven.plugin.samples.forum.server.GraphQLUtil;
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

	@Resource
	GraphQLUtil graphQLUtil;

	@Override
	public List<Board> boards(DataFetchingEnvironment dataFetchingEnvironment) {
		return graphQLUtil.iterableToList(boardRepository.findAll());
	}

	@Override
	public List<Topic> topics(DataFetchingEnvironment dataFetchingEnvironment, String boardName) {
		return topicRepository.findByBoardName(boardName);
	}

}
