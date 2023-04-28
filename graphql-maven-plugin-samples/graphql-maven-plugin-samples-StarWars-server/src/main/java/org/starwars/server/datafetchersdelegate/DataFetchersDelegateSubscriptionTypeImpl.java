/**
 * 
 */
package org.starwars.server.datafetchersdelegate;

import java.util.Optional;

import javax.annotation.Resource;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.starwars.server.Character;
import org.starwars.server.util.DataFetchersDelegateSubscriptionType;

import graphql.schema.DataFetchingEnvironment;
import io.reactivex.subjects.Subject;

/**
 * @author etienne-sf
 *
 */
@Component
public class DataFetchersDelegateSubscriptionTypeImpl implements DataFetchersDelegateSubscriptionType {

	/** Logger for this class */
	Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * This {@link Subject} will be notified for each Human or Droid creation. This is the basis for the
	 * <I>newCharacter</I> subscription
	 */
	@Resource
	CharacterPublisher characterPublisher;

	@Override
	public Publisher<Optional<Character>> newCharacter(DataFetchingEnvironment dataFetchingEnvironment) {
		logger.debug("Received a 'newCharacter' subscription");
		return characterPublisher.getPublisher();
	}

}
