/**
 * 
 */
package org.forum.server.specific_code;

import jakarta.annotation.Resource;

import org.forum.server.graphql.Board;
import org.forum.server.graphql.Post;
import org.forum.server.graphql.Topic;
import org.forum.server.jpa.BoardRepository;
import org.forum.server.jpa.TopicRepository;
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

	static int MESSAGE_BUFFER_SIZE = 100;

	/** The logger for this instance */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	TopicRepository topicRepository;
	@Resource
	BoardRepository boardRepository;

	// onBackpressureBuffer : autoCancel=false, so that the Sink doesn't fully shutdown (not publishing anymore) when
	// the last subscriber cancels. Otherwise it would not been possible to emit messages once one consumer has
	// subscribed then unsubscribed.
	Many<Post> sink = Sinks.many().multicast().directBestEffort();

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
	Flux<Post> getPublisher(String boardName) {
		logger.debug("Subscribing on sink for {}", boardName);

		Flux<Post> flux = sink.asFlux();

		if (boardName != null) {
			flux.filter((post) -> {
				Topic topic = topicRepository.findById(post.getTopicId()).get();
				Board board = boardRepository.findById(topic.getBoardId()).get();
				return board.getName().equals(boardName);
			});
		}

		return flux;
	}

}
