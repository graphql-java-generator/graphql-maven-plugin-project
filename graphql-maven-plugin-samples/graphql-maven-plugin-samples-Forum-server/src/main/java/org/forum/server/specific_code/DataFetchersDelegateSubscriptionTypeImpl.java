/**
 * 
 */
package org.forum.server.specific_code;

import javax.annotation.Resource;

import org.forum.server.graphql.DataFetchersDelegateSubscriptionType;
import org.forum.server.graphql.Post;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;
import io.reactivex.subjects.Subject;

/**
 * @author etienne-sf
 */
@Component
public class DataFetchersDelegateSubscriptionTypeImpl implements DataFetchersDelegateSubscriptionType {

	/** The logger for this instance */
	static Logger logger = LoggerFactory.getLogger(DataFetchersDelegateSubscriptionTypeImpl.class);

	/**
	 * This {@link Subject} will be notified for each Post creation. This is the basis for the <I>subscribeToNewPost</I>
	 * subscription
	 */
	@Resource
	PostPublisher postPublisher;

	@Override
	public Publisher<Post> subscribeToNewPost(DataFetchingEnvironment dataFetchingEnvironment, String boardName) {
		logger.debug("Received a Subscription for {}", boardName);
		Publisher<Post> publisher = postPublisher.getPublisher(boardName);
		logger.trace("The publisher has been acquired for the Subscription for {}", boardName);
		return publisher;
	}
}
