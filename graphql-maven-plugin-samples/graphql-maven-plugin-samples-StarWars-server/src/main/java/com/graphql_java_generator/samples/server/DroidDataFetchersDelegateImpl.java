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
import com.graphql_java_generator.Droid;
import com.graphql_java_generator.DroidDataFetchersDelegate;
import com.graphql_java_generator.Episode;
import com.graphql_java_generator.GraphQLUtil;
import com.graphql_java_generator.samples.server.jpa.CharacterRepository;
import com.graphql_java_generator.samples.server.jpa.DroidRepository;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author EtienneSF
 */
@Component
public class DroidDataFetchersDelegateImpl implements DroidDataFetchersDelegate {

	/** The logger for this instance */
	protected Logger logger = LogManager.getLogger();

	@Resource
	CharacterRepository characterRepository;

	@Resource
	DroidRepository droidRepository;

	@Resource
	GraphQLUtil graphQLUtil;

	@Override
	public CompletableFuture<List<CharacterImpl>> friends(DataFetchingEnvironment environment, Droid source) {
		logger.debug("Executing droid.friends, with this droid: {}", source.getId().toString());
		List<UUID> friendIds = graphQLUtil
				.convertListByteArrayToListUUID(characterRepository.findFriendsId(source.getId()));
		DataLoader<UUID, CharacterImpl> dataLoader = environment.getDataLoader("Character");
		return dataLoader.loadMany(friendIds);
	}

	@Override
	public List<Episode> appearsIn(DataFetchingEnvironment dataFetchingEnvironment, Droid source) {
		logger.debug("Executing droid.appearsIn, with this droid: ", source.getId());
		List<String> episodeStr = droidRepository.findAppearsInById(source.getId());
		List<Episode> ret = new ArrayList<>(episodeStr.size());
		for (String s : episodeStr) {
			ret.add(Episode.valueOf(s));
		} // for
		return ret;
	}

	@Override
	public List<Droid> droidBatchLoader(List<UUID> keys) {
		if (logger.isTraceEnabled())
			logger.trace("Executing droidBatchLoader, with {} keys: {}", keys.size(), keys);
		else if (logger.isDebugEnabled())
			logger.debug("Executing droidBatchLoader, with {} keys", keys.size());

		List<Droid> ret = droidRepository.batchLoader(keys);
		return ret;
	}

}
