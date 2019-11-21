/**
 * 
 */
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
import com.graphql_java_generator.Episode;
import com.graphql_java_generator.GraphQLUtil;
import com.graphql_java_generator.Human;
import com.graphql_java_generator.HumanDataFetchersDelegate;
import com.graphql_java_generator.samples.server.jpa.CharacterRepository;
import com.graphql_java_generator.samples.server.jpa.HumanRepository;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author EtienneSF
 */
@Component
public class HumanDataFetchersDelegateImpl implements HumanDataFetchersDelegate {

	/** The logger for this instance */
	protected Logger logger = LogManager.getLogger();

	@Resource
	CharacterRepository characterRepository;

	@Resource
	HumanRepository humanRepository;

	@Resource
	GraphQLUtil graphQLUtil;

	@Override
	public CompletableFuture<List<CharacterImpl>> friends(DataFetchingEnvironment environment, Human source) {
		logger.debug("Executing human.friends, with this human: {}", source.getId().toString());
		List<UUID> friendIds = graphQLUtil
				.convertListByteArrayToListUUID(characterRepository.findFriendsId(source.getId()));
		DataLoader<UUID, CharacterImpl> dataLoader = environment.getDataLoader("Character");
		return dataLoader.loadMany(friendIds);
	}

	@Override
	public List<Episode> appearsIn(DataFetchingEnvironment dataFetchingEnvironment, Human source) {
		logger.debug("Executing human.appearsIn, with this human: ", source.getId());
		List<String> episodeStr = humanRepository.findAppearsInById(source.getId());
		List<Episode> ret = new ArrayList<>(episodeStr.size());
		for (String s : episodeStr) {
			ret.add(Episode.valueOf(s));
		} // for
		return ret;
	}

	@Override
	public List<Human> humanBatchLoader(List<UUID> keys) {
		if (logger.isTraceEnabled())
			logger.trace("Executing humanBatchLoader, with {} keys: {}", keys.size(), keys);
		else if (logger.isDebugEnabled())
			logger.debug("Executing humanBatchLoader, with {} keys", keys.size());

		List<Human> ret = humanRepository.batchLoader(keys);
		return ret;
	}

}
