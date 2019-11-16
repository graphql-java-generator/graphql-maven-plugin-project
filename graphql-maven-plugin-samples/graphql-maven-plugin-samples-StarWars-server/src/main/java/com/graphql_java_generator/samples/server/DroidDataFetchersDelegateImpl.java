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
import com.graphql_java_generator.Droid;
import com.graphql_java_generator.DroidDataFetchersDelegate;
import com.graphql_java_generator.Episode;
import com.graphql_java_generator.GraphQLUtil;
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
	DroidRepository droidRepository;

	@Resource
	GraphQLUtil graphQLUtil;

	@Override
	public List<Character> friends(DataFetchingEnvironment dataFetchingEnvironment, Droid source) {
		logger.debug("Executing droid.friends, with this droid: ", source.getId());
		return graphQLUtil.iterableConcreteClassToListInterface(droidRepository.findFriends(source.getId()));
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
	public List<Droid> droidBatchLoader(List<String> keys) {
		if (logger.isDebugEnabled()) {
			logger.debug("Executing droidBatchLoader, with this list of keys: ", String.join(", ", keys));
		}
		return droidRepository.batchLoader(keys);
	}

}
