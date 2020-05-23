/**
 * 
 */
package org.starwars.server.datafetchersdelegate;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.starwars.server.Character;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

/**
 * This class is responsible for Publishing new Characters. This allows to send the notifications, when an application
 * subscribed to the <I>newCharacter</I> subscription.
 * 
 * @author etienne-sf
 */
@Component
public class CharacterPublisher {

	/** The logger for this instance */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	PublishSubject<Character> subject = PublishSubject.create();

	public CharacterPublisher() {
		// in debug mode, we'll log each new entry in this subject, to check that the subject properly received the
		// events, and that the subscribers to receive them
		if (logger.isDebugEnabled()) {
			subject.subscribe(new Observer<Character>() {

				@Override
				public void onSubscribe(Disposable d) {
					logger.debug("[Debug subscriber] onSubscribe");
				}

				@Override
				public void onNext(Character t) {
					logger.debug("[Debug subscriber] onNext: " + t);
				}

				@Override
				public void onError(Throwable e) {
					logger.debug("[Debug subscriber] onError: " + e);
				}

				@Override
				public void onComplete() {
					logger.debug("[Debug subscriber] onComplete");
				}
			});
		}
	}

	/**
	 * Let's emit this new {@link Character}
	 * 
	 * @param post
	 */
	void onNext(Character c) {
		logger.debug("Emitting suscription notification for {}", c);
		subject.onNext(c);
	}

	/**
	 * Let's get a new publisher, for the GraphQL subscription that just occurred
	 * 
	 * @return
	 */
	Publisher<Character> getPublisher() {
		logger.debug("Executing Suscription");
		return subject.toFlowable(BackpressureStrategy.BUFFER);
	}

}
