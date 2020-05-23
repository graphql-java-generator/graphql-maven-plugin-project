/**
 * 
 */
package org.forum.server.specific_code;

import javax.annotation.Resource;

import org.forum.server.graphql.DataFetchersDelegateSubscriptionType;
import org.forum.server.graphql.Post;
import org.forum.server.jpa.BoardRepository;
import org.forum.server.jpa.PostRepository;
import org.forum.server.jpa.TopicRepository;
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
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	BoardRepository boardRepository;
	@Resource
	TopicRepository topicRepository;
	@Resource
	PostRepository postRepository;

	/**
	 * This {@link Subject} will be notified for each Human or Droid creation. This is the basis for the
	 * <I>subscribeToNewPost</I> subscription
	 */
	@Resource
	PostPublisher postPublisher;

	@Override
	public Publisher<Post> subscribeToNewPost(DataFetchingEnvironment dataFetchingEnvironment, String boardName) {
		logger.debug("Received a Suscription for {}", boardName);
		return postPublisher.getPublisher(boardName);
	}
}
