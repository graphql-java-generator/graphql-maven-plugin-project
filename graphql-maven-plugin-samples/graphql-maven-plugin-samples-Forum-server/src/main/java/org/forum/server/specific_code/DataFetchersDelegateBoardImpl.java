/**
 * 
 */
package org.forum.server.specific_code;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.forum.server.graphql.Board;
import org.forum.server.graphql.Topic;
import org.forum.server.graphql.util.DataFetchersDelegateBoard;
import org.forum.server.jpa.BoardRepository;
import org.forum.server.jpa.TopicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import jakarta.annotation.Resource;
import reactor.core.publisher.Flux;

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
	
	public List<Topic> topics(DataFetchingEnvironment dataFetchingEnvironment, Board source, Date since) {
		if (since == null)
			return this.topicRepository.findByBoardId(source.getId());
		else
			return this.topicRepository.findByBoardIdAndSince(source.getId(), since);
	}

	@Override
	public Object topics(
			DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<java.lang.Long, org.forum.server.graphql.Topic> dataLoader,
			org.forum.server.graphql.Board origin,
			java.util.Date since) {
		// When the data is modeled this way (that is: in a relational database), using Data Loader is not an
		// optimization.
		// But this is used here for integration tests
		List<Long> ids = new ArrayList<>();
		for (Topic topic : topics(dataFetchingEnvironment, origin, since)) {
			ids.add(topic.getId());
		}
		return dataLoader.loadMany(ids);

	}

}
