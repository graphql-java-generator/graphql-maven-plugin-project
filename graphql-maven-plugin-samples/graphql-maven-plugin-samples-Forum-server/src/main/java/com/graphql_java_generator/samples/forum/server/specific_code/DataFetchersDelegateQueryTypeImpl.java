/**
 * 
 */
package com.graphql_java_generator.samples.forum.server.specific_code;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.samples.forum.server.Board;
import com.graphql_java_generator.samples.forum.server.DataFetchersDelegateQueryType;
import com.graphql_java_generator.samples.forum.server.Topic;
import com.graphql_java_generator.samples.forum.server.jpa.BoardRepository;
import com.graphql_java_generator.samples.forum.server.jpa.TopicRepository;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author etienne-sf
 */
@Component
public class DataFetchersDelegateQueryTypeImpl implements DataFetchersDelegateQueryType {

	@Resource
	BoardRepository boardRepository;
	@Resource
	TopicRepository topicRepository;

	@Resource
	GraphqlUtils graphqlUtils;

	@Override
	public List<Board> boards(DataFetchingEnvironment dataFetchingEnvironment) {
		return graphqlUtils.iterableToList(boardRepository.findAll());
	}

	@Override
	public Integer nbBoards(DataFetchingEnvironment dataFetchingEnvironment) {
		return (int) boardRepository.count();
	}

	@Override
	public List<Topic> topics(DataFetchingEnvironment dataFetchingEnvironment, String boardName) {
		return topicRepository.findByBoardName(boardName);
	}

	@Override
	public List<Topic> findTopics(DataFetchingEnvironment dataFetchingEnvironment, String boardName,
			List<String> keyword) {
		return topicRepository.findByBoardNameAndKeywords(boardName, keyword);
	}

}
