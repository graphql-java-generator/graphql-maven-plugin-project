/**
 * 
 */
package com.graphql_java_generator.samples.forum.server.specific_code;

import javax.annotation.Resource;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.samples.forum.server.DataFetchersDelegateSubscriptionType;
import com.graphql_java_generator.samples.forum.server.Post;
import com.graphql_java_generator.samples.forum.server.jpa.BoardRepository;
import com.graphql_java_generator.samples.forum.server.jpa.PostRepository;
import com.graphql_java_generator.samples.forum.server.jpa.TopicRepository;

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
