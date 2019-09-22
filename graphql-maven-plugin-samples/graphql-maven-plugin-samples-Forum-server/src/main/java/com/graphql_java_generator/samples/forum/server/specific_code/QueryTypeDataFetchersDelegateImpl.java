/**
 * 
 */
package com.graphql_java_generator.samples.forum.server.specific_code;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.graphql_java_generator.samples.forum.server.Board;
import com.graphql_java_generator.samples.forum.server.GraphQLUtil;
import com.graphql_java_generator.samples.forum.server.QueryTypeDataFetchersDelegate;
import com.graphql_java_generator.samples.forum.server.Topic;
import com.graphql_java_generator.samples.forum.server.jpa.BoardRepository;
import com.graphql_java_generator.samples.forum.server.jpa.TopicRepository;

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
