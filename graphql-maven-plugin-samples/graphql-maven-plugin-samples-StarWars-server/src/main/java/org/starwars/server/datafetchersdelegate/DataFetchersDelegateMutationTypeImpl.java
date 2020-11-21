/**
 * 
 */
package org.starwars.server.datafetchersdelegate;

import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.starwars.server.Character;
import org.starwars.server.DataFetchersDelegateMutationType;
import org.starwars.server.Droid;
import org.starwars.server.Human;
import org.starwars.server.jpa.DroidRepository;
import org.starwars.server.jpa.HumanRepository;

import graphql.schema.DataFetchingEnvironment;
import io.reactivex.subjects.Subject;

/**
 * @author etienne-sf
 */
@Component
public class DataFetchersDelegateMutationTypeImpl implements DataFetchersDelegateMutationType {

	/** Logger for this class */
	Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	HumanRepository humanRepository;
	@Resource
	DroidRepository droidRepository;
	@Resource
	CharacterHelper characterHelper;

	/**
	 * This {@link Subject} will be notified for each Human or Droid creation. This is the basis for the
	 * <I>newCharacter</I> subscription
	 */
	@Resource
	CharacterPublisher characterPublisher;

	@Override
	public Human createHuman(DataFetchingEnvironment dataFetchingEnvironment, String name, String homePlanet) {
		logger.trace("Creating a 'Human' of name '{}' and homePlanet ''", name, homePlanet);

		Human human = new Human();
		human.setName(name);
		human.setHomePlanet(homePlanet);
		humanRepository.save(human);

		characterPublisher.onNext(human);

		return human;
	}

	@Override
	public Character addFriend(DataFetchingEnvironment dataFetchingEnvironment, String idCharacter, String idFriend) {
		// What kind of character is it?
		Character c = characterHelper.findById(UUID.fromString(idCharacter));

		if (c == null) {
			throw new RuntimeException(idCharacter + " is not an id of a an existing character");
		} else if (c instanceof Human) {
			humanRepository.addFriend(UUID.fromString(idCharacter), UUID.fromString(idFriend));
		} else if (c instanceof Droid) {
			droidRepository.addFriend(UUID.fromString(idCharacter), UUID.fromString(idFriend));
		} else {
			throw new RuntimeException(
					idCharacter + " is a character of an unknown type (" + c.getClass().getName() + ")");
		}

		// Then we return the new friend
		return characterHelper.findById(UUID.fromString(idCharacter));
	}

}
