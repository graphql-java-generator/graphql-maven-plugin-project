/**
 * 
 */
package org.forum.generated.util;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author etienne-sf
 *
 */
@Controller
public class SubscriptionController {

	@Autowired
	DataFetchersDelegateSubscription dataFetchersDelegateSubscription;

	/**
	 * This method loads the data for Subscription.subscribeToNewPost. <BR/>
	 * 
	 * @param dataFetchingEnvironment
	 *            The GraphQL {@link DataFetchingEnvironment}. It gives you access to the full GraphQL context for this
	 *            DataFetcher
	 * @param boardName
	 *            The input parameter sent in the query by the GraphQL consumer, as defined in the GraphQL schema.
	 * @throws NoSuchElementException
	 *             This method may return a {@link NoSuchElementException} exception. In this case, the exception is
	 *             trapped by the calling method, and the return is consider as null. This allows to use the
	 *             {@link Optional#get()} method directly, without caring of whether or not there is a value. The
	 *             generated code will take care of the {@link NoSuchElementException} exception.
	 */
	@SubscriptionMapping("subscribeToNewPost")
	public Publisher<org.forum.generated.Post> subscribeToNewPost(DataFetchingEnvironment dataFetchingEnvironment,
			@Argument("boardName") java.lang.String boardName) {
		return dataFetchersDelegateSubscription.subscribeToNewPost(dataFetchingEnvironment, boardName);
	}

}
