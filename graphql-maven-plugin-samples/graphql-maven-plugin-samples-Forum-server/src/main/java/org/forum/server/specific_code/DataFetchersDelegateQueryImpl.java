/**
 * 
 */
package org.forum.server.specific_code;

import java.util.List;

import org.forum.server.graphql.Board;
import org.forum.server.graphql.Topic;
import org.forum.server.graphql.util.DataFetchersDelegateQuery;
import org.forum.server.jpa.BoardRepository;
import org.forum.server.jpa.TopicRepository;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.util.GraphqlUtils;

import graphql.schema.DataFetchingEnvironment;
import jakarta.annotation.Resource;

/**
 * @author etienne-sf
 */
@Component
public class DataFetchersDelegateQueryImpl implements DataFetchersDelegateQuery {

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
