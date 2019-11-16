package com.graphql_java_generator.samples.server;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.Character;
import com.graphql_java_generator.CharacterImpl;
import com.graphql_java_generator.CharacterImplDataFetchersDelegate;
import com.graphql_java_generator.Episode;
import com.graphql_java_generator.GraphQLUtil;
import com.graphql_java_generator.samples.server.jpa.CharacterRepository;

import graphql.schema.DataFetchingEnvironment;

@Component
public class CharacterImplDataFetchersDelegateImpl implements CharacterImplDataFetchersDelegate {

	/** The logger for this instance */
	protected Logger logger = LogManager.getLogger();

	@Resource
	CharacterRepository characterRepository;

	@Resource
	GraphQLUtil graphQLUtil;

	@Override
	public List<Character> friends(DataFetchingEnvironment dataFetchingEnvironment, CharacterImpl source) {
		logger.debug("Executing characterImpl.friends, with this character: ", source.getId());
		return graphQLUtil.iterableConcreteClassToListInterface(characterRepository.findFriends(source.getId()));
	}

	@Override
	public List<Episode> appearsIn(DataFetchingEnvironment dataFetchingEnvironment, CharacterImpl source) {
		logger.debug("Executing characterImpl.appearsIn, with this character: ", source.getId());
		List<String> episodeStr = characterRepository.findAppearsInById(source.getId());
		List<Episode> ret = new ArrayList<>(episodeStr.size());
		for (String s : episodeStr) {
			ret.add(Episode.valueOf(s));
		} // for
		return ret;
	}

	@Override
	public List<Character> characterImplBatchLoader(List<String> keys) {
		if (logger.isDebugEnabled()) {
			logger.debug("Executing droidBatchLoader, with this list of keys: ", String.join(", ", keys));
		}
		return characterRepository.batchLoader(keys);
	}

}
