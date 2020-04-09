/**
 * 
 */
package com.graphql_java_generator.samples.server;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author EtienneSF
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
