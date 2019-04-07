package org.graphql.maven.plugin.samples.server;

import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphql.maven.plugin.samples.server.generated.Character;
import org.graphql.maven.plugin.samples.server.generated.CharacterImpl;
import org.graphql.maven.plugin.samples.server.generated.CharacterType;
import org.graphql.maven.plugin.samples.server.generated.Droid;
import org.graphql.maven.plugin.samples.server.generated.Episode;
import org.graphql.maven.plugin.samples.server.generated.Human;
import org.graphql.maven.plugin.samples.server.jpa.CharacterRepository;
import org.graphql.maven.plugin.samples.server.jpa.DroidRepository;
import org.graphql.maven.plugin.samples.server.jpa.HumanRepository;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;

@Component
public class GraphQLDataFetchers {

	/** The logger for this instance */
	protected Logger logger = LogManager.getLogger();

	@Resource
	CharacterRepository characterRepository;
	@Resource
	HumanRepository humanRepository;
	@Resource
	DroidRepository droidRepository;

	public DataFetcher<Character> hero() {
		return dataFetchingEnvironment -> {
			List<CharacterImpl> ret;
			if (dataFetchingEnvironment.getArgument("episode") == null) {
				ret = characterRepository.findAll();
			} else {
				Episode episode = Episode.valueOf(dataFetchingEnvironment.getArgument("episode"));
				ret = characterRepository.findByAppearsIn(episode);
			}
			logger.debug("'hero' query: {} found rows (the first one is returned, or null if no row)", ret.size());
			return (ret.size() > 0) ? ret.get(0) : null;
		};

	}

	public DataFetcher<List<CharacterImpl>> characters() {
		return dataFetchingEnvironment -> {
			Episode episode = Episode.valueOf(dataFetchingEnvironment.getArgument("episode"));
			List<CharacterImpl> ret = characterRepository.findByAppearsIn(episode);
			logger.debug("'hero' query: {} rows returned", ret.size());
			return ret;
		};
	}

	public DataFetcher<Human> human() {
		return dataFetchingEnvironment -> {
			String id = dataFetchingEnvironment.getArgument("id");
			Human human = humanRepository.findByTypeAndId(CharacterType.HUMAN, id);
			logger.debug("'human' query returned: {}", human);
			return human;
		};
	}

	public DataFetcher<Droid> droid() {
		return dataFetchingEnvironment -> {
			String id = dataFetchingEnvironment.getArgument("id");
			Droid droid = droidRepository.findByTypeAndId(CharacterType.DROID, id);
			logger.debug("'droid' query returned: {}", droid);
			return droid;
		};
	}

	public DataFetcher<List<CharacterImpl>> friends() {
		return dataFetchingEnvironment -> {
			Character character = dataFetchingEnvironment.getSource();
			List<CharacterImpl> ret = characterRepository.findFriends(character.getId());
			logger.debug("'friends' subquery: {} rows returned", ret.size());
			return ret;
		};
	}
}
