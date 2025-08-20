/**
 * 
 */
package org.starwars.server.datafetchersdelegate;

import java.util.List;
import java.util.UUID;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.starwars.server.Character;
import org.starwars.server.Episode;
import org.starwars.server.util.DataFetchersDelegateCharacter;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author etienne-sf
 */
@Component
public class DataFetchersDelegateCharacterImpl implements DataFetchersDelegateCharacter {

	/** The logger for this instance */
	protected Logger logger = LoggerFactory.getLogger(DataFetchersDelegateCharacterImpl.class);

	@Resource
	CharacterHelper characterHelper;

	@Override
	public List<Character> friends(DataFetchingEnvironment dataFetchingEnvironment, Character source) {
		logger.debug("Executing character.friends, with this character: {}", source.getId().toString());
		return characterHelper.friends(source.getId());
	}

	@Override
	public List<Episode> appearsIn(DataFetchingEnvironment dataFetchingEnvironment, Character source) {
		logger.debug("Executing character.appearsIn, with this character: ", source.getId());
		return characterHelper.findAppearsInById(source.getId());
	}

	@Override
	public List<Character> batchLoader(List<UUID> keys) {
		logger.debug("Batch loding {} characters", keys.size());
		return characterHelper.batchLoader(keys);
	}
}
