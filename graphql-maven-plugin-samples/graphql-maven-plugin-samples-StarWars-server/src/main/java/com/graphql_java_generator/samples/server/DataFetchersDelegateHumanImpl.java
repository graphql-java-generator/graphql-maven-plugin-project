/**
 * 
 */
package com.graphql_java_generator.samples.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.samples.server.jpa.HumanRepository;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author EtienneSF
 */
@Component
public class DataFetchersDelegateHumanImpl implements DataFetchersDelegateHuman {

	/** The logger for this instance */
	protected Logger logger = LoggerFactory.getLogger(DataFetchersDelegateHumanImpl.class);

	@Resource
	HumanRepository humanRepository;
	@Resource
	CharacterHelper characterHelper;

	@Resource
	GraphQLUtil graphQLUtil;

	@Override
	public List<Character> friends(DataFetchingEnvironment environment, Human source) {
		logger.debug("Executing human.friends, with this human: {}", source.getId().toString());
		return characterHelper.friends(source.getId());
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
	public List<Human> batchLoader(List<UUID> keys) {
		if (logger.isTraceEnabled())
			logger.trace("Executing humanBatchLoader, with {} keys: {}", keys.size(), keys);
		else if (logger.isDebugEnabled())
			logger.debug("Executing humanBatchLoader, with {} keys", keys.size());

		List<Human> ret = humanRepository.batchLoader(keys);
		return ret;
	}

}
