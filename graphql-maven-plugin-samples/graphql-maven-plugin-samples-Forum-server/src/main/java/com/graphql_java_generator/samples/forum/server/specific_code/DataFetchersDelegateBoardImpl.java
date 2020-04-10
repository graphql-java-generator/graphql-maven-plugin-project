/**
 * 
 */
package com.graphql_java_generator.samples.forum.server.specific_code;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.samples.forum.server.Board;
import com.graphql_java_generator.samples.forum.server.DataFetchersDelegateBoard;
import com.graphql_java_generator.samples.forum.server.Topic;
import com.graphql_java_generator.samples.forum.server.jpa.BoardRepository;
import com.graphql_java_generator.samples.forum.server.jpa.TopicRepository;

import graphql.schema.DataFetchingEnvironment;

/**
 * This class implements the access to the database : there are so many ways to do this, that the developper has still
 * work to do. But all the GraphQL boilerplate is generated.<BR/>
 * The {@link GraphQLDataFetchersDelegate} interface is generated from the given schema
 * 
 * @author etienne-sf
 */
@Component
public class DataFetchersDelegateBoardImpl implements DataFetchersDelegateBoard {

	/** The logger for this instance */
	protected Logger logger = LoggerFactory.getLogger(DataFetchersDelegateBoardImpl.class);

	@Resource
	TopicRepository topicRepository;
	@Resource
	BoardRepository boardRepository;

	@Override
	public List<Topic> topics(DataFetchingEnvironment dataFetchingEnvironment, Board source, Date since) {
		if (since == null)
			return topicRepository.findByBoardId(source.getId());
		else
			return topicRepository.findByBoardIdAndSince(source.getId(), since);
	}

	@Override
	public List<Board> batchLoader(List<UUID> keys) {
		logger.debug("Batch loading {} topics", keys.size());
		return boardRepository.findByIds(keys);
	}
}
