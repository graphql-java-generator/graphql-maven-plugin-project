/**
 * 
 */
package com.graphql_java_generator.samples.server;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.Character;
import com.graphql_java_generator.Episode;
import com.graphql_java_generator.GraphQLUtil;
import com.graphql_java_generator.Human;
import com.graphql_java_generator.HumanDataFetchersDelegate;
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
	HumanRepository humanRepository;

	@Resource
	GraphQLUtil graphQLUtil;

	@Override
	public List<Character> friends(DataFetchingEnvironment dataFetchingEnvironment, Human source) {
		logger.debug("Executing droid.friends, with this human: ", source.getId());
		return graphQLUtil.iterableConcreteClassToListInterface(humanRepository.findFriends(source.getId()));
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
	public List<Human> humanBatchLoader(List<String> keys) {
		if (logger.isDebugEnabled()) {
			logger.debug("Executing humanBatchLoader, with this list of keys: ", String.join(", ", keys));
		}
		return humanRepository.batchLoader(keys);
	}

}
