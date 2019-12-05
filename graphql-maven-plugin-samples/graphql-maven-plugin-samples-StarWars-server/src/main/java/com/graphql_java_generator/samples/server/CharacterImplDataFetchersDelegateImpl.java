package com.graphql_java_generator.samples.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.samples.server.jpa.CharacterRepository;

import graphql.schema.DataFetchingEnvironment;

@Component
public class CharacterImplDataFetchersDelegateImpl implements CharacterDataFetchersDelegate {

	/** The logger for this instance */
	protected Logger logger = LogManager.getLogger();

	@Resource
	CharacterRepository characterRepository;

	@Resource
	GraphQLUtil graphQLUtil;

	@Override
	public List<Character> friends(DataFetchingEnvironment environment, Character source) {
		logger.debug("Executing characterImpl.friends, with this character: {}", source.getId().toString());
		return new ArrayList<Character>(characterRepository.findFriends(source.getId()));
	}

	@Override
	public List<Episode> appearsIn(DataFetchingEnvironment dataFetchingEnvironment, Character source) {
		logger.debug("Executing characterImpl.appearsIn, with this character: ", source.getId());
		List<String> episodeStr = characterRepository.findAppearsInById(source.getId());
		List<Episode> ret = new ArrayList<>(episodeStr.size());
		for (String s : episodeStr) {
			ret.add(Episode.valueOf(s));
		} // for
		return ret;
	}

	@Override
	public List<Character> batchLoader(List<UUID> keys) {
		if (logger.isTraceEnabled())
			logger.trace("Executing characterImplBatchLoader, with {} keys: {}", keys.size(), keys);
		else if (logger.isDebugEnabled())
			logger.debug("Executing characterImplBatchLoader, with {} keys", keys.size());

		return new ArrayList<Character>(characterRepository.batchLoader(keys));
	}

}
