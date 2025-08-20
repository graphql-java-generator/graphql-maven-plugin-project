/**
 * 
 */
package org.starwars.server.datafetchersdelegate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.starwars.server.Character;
import org.starwars.server.Droid;
import org.starwars.server.Episode;
import org.starwars.server.jpa.DroidRepository;
import org.starwars.server.util.DataFetchersDelegateDroid;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author etienne-sf
 */
@Component
public class DataFetchersDelegateDroidImpl implements DataFetchersDelegateDroid {

	/** The logger for this instance */
	protected Logger logger = LoggerFactory.getLogger(DataFetchersDelegateDroidImpl.class);

	@Resource
	DroidRepository droidRepository;
	@Resource
	CharacterHelper characterHelper;

	@Override
	public List<Character> friends(DataFetchingEnvironment environment, Droid source) {
		logger.debug("Executing droid.friends, with this droid: {}", source.getId().toString());
		return characterHelper.friends(source.getId());
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
	public List<Droid> batchLoader(List<UUID> keys) {
		if (logger.isTraceEnabled())
			logger.trace("Executing droidBatchLoader, with {} keys: {}", keys.size(), keys);
		else if (logger.isDebugEnabled())
			logger.debug("Executing droidBatchLoader, with {} keys", keys.size());

		List<Droid> ret = droidRepository.batchLoader(keys);
		return ret;
	}

}
