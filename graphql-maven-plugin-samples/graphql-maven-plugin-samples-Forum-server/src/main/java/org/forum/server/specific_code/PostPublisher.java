/**
 * 
 */
package org.forum.server.specific_code;

import javax.annotation.Resource;

import org.forum.server.graphql.Board;
import org.forum.server.graphql.Post;
import org.forum.server.graphql.Topic;
import org.forum.server.jpa.BoardRepository;
import org.forum.server.jpa.TopicRepository;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitResult;
import reactor.core.publisher.Sinks.Many;

/**
 * This class is responsible for Publishing new Posts. This allows to send the notifications, when an application
 * subscribed to the <I>subscribeToNewPost</I> subscription.
 * 
 * @author etienne-sf
 */
@Component
public class PostPublisher {

	/** The logger for this instance */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	TopicRepository topicRepository;
	@Resource
	BoardRepository boardRepository;

	Many<Post> sink = Sinks.unsafe().many().replay().latest();

	/**
	 * Let's emit this new {@link Post}
	 * 
	 * @param post
	 */
	void onNext(Post post) {
		logger.trace("Emitting subscription notification for {}", post);
		EmitResult result = sink.tryEmitNext(post);
		if (result.isFailure()) {
			logger.error("Error while emitting subscription notification for {}", post);
		} else if (logger.isTraceEnabled()) {
			logger.trace("  Successful emitting of subscription notification for {}", post);
		}
	}

	/**
	 * Let's get a new publisher, for the GraphQL subscription that just occurred
	 * 
	 * @return
	 */
	Publisher<Post> getPublisher(String boardName) {
		logger.debug("Subscribing on skink for {}", boardName);

		Flux<Post> publisher = sink.asFlux();

		if (boardName != null) {
			publisher.filter((post) -> {
				Topic topic = topicRepository.findById(post.getTopicId()).get();
				Board board = boardRepository.findById(topic.getBoardId()).get();
				return board.getName().equals(boardName);
			});
		}

		return publisher;
	}

}
