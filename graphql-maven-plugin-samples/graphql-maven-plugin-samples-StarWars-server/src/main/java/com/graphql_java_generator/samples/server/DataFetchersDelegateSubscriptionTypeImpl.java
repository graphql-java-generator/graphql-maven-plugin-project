/**
 * 
 */
package com.graphql_java_generator.samples.server;

import javax.annotation.Resource;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;
import io.reactivex.subjects.Subject;

/**
 * @author etienne-sf
 *
 */
@Component
public class DataFetchersDelegateSubscriptionTypeImpl implements DataFetchersDelegateSubscriptionType {

	/**
	 * This {@link Subject} will be notified for each Human or Droid creation. This is the basis for the
	 * <I>newCharacter</I> subscription
	 */
	@Resource
	CharacterPublisher characterPublisher;

	@Override
	public Publisher<Character> newCharacter(DataFetchingEnvironment dataFetchingEnvironment) {
		return characterPublisher.getPublisher();
	}

}
