/**
 * 
 */
package org.forum.server.specific_code;

import org.forum.server.graphql.Post;
import org.forum.server.graphql.util.DataFetchersDelegateSubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;
import jakarta.annotation.Resource;
import reactor.core.publisher.Flux;

/**
 * @author etienne-sf
 */
@Component
public class DataFetchersDelegateSubscriptionImpl implements DataFetchersDelegateSubscription {

	/** The logger for this instance */
	static Logger logger = LoggerFactory.getLogger(DataFetchersDelegateSubscriptionImpl.class);

	/**
	 * This {@link PostPublisher} will be notified for each Post creation. This is the basis for the
	 * <I>subscribeToNewPost</I> subscription
	 */
	@Resource
	PostPublisher postPublisher;

	@Override
	public Flux<Post> subscribeToNewPost(DataFetchingEnvironment dataFetchingEnvironment, String boardName) {
		logger.debug("Received a Subscription for {}", boardName);
		Flux<Post> publisher = postPublisher.getPublisher(boardName);
		logger.trace("The publisher has been acquired for the Subscription for {}", boardName);
		return publisher;
	}
}
