/**
 * 
 */
package org.forum.server.specific_code;

import java.util.List;

import org.dataloader.BatchLoaderEnvironment;
import org.forum.server.graphql.Board;
import org.forum.server.graphql.Topic;
import org.forum.server.graphql.util.DataFetchersDelegateBoard;
import org.forum.server.jpa.BoardRepository;
import org.forum.server.jpa.TopicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import graphql.GraphQLContext;
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

	@Override
	public Flux<List<Topic>> topics(//
			BatchLoaderEnvironment batchLoaderEnvironment, //
			GraphQLContext graphQLContext, //
			List<Board> boards //
	// @Argument("since") java.util.Date since//
	) {
		java.util.Date since = null;
		return Flux.fromIterable(boards).map(b -> {
			if (since == null)
				return this.topicRepository.findByBoardId(b.getId());
			else
				return this.topicRepository.findByBoardIdAndSince(b.getId(), since);
		});

		// Mono.create(callback -> {
		// this.logger.debug("Batch loading {} topics", boards.size());
		// Map<Board, List<Topic>> map = new HashMap<>();
		// for (Board b : boards) {
		// if (since == null)
		// map.put(b, this.topicRepository.findByBoardId(b.getId()));
		// else
		// map.put(b, this.topicRepository.findByBoardIdAndSince(b.getId(), since));
		// }
		// callback.success(map);
		// });
	}

}
