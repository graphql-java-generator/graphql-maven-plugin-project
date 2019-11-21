package com.graphql_java_generator.samples.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

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
	public CompletableFuture<List<CharacterImpl>> friends(DataFetchingEnvironment environment, CharacterImpl source) {
		logger.debug("Executing characterImpl.friends, with this character: {}", source.getId().toString());
		List<UUID> friendIds = graphQLUtil
				.convertListByteArrayToListUUID(characterRepository.findFriendsId(source.getId()));
		DataLoader<UUID, CharacterImpl> dataLoader = environment.getDataLoader("Character");
		return dataLoader.loadMany(friendIds);
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
	public List<CharacterImpl> characterImplBatchLoader(List<UUID> keys) {
		if (logger.isTraceEnabled())
			logger.trace("Executing characterImplBatchLoader, with {} keys: {}", keys.size(), keys);
		else if (logger.isDebugEnabled())
			logger.debug("Executing characterImplBatchLoader, with {} keys", keys.size());

		List<CharacterImpl> ret = characterRepository.batchLoader(keys);
		return ret;
	}

}
