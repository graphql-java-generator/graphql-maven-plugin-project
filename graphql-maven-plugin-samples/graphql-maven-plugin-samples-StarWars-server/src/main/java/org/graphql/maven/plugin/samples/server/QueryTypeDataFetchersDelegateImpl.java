package org.graphql.maven.plugin.samples.server;

import java.util.List;

import javax.annotation.Resource;

import org.graphql.maven.plugin.samples.server.generated.Character;
import org.graphql.maven.plugin.samples.server.generated.CharacterImpl;
import org.graphql.maven.plugin.samples.server.generated.Droid;
import org.graphql.maven.plugin.samples.server.generated.Episode;
import org.graphql.maven.plugin.samples.server.generated.Human;
import org.graphql.maven.plugin.samples.server.generated.QueryTypeDataFetchersDelegate;
import org.graphql.maven.plugin.samples.server.jpa.CharacterRepository;
import org.graphql.maven.plugin.samples.server.jpa.DroidRepository;
import org.graphql.maven.plugin.samples.server.jpa.HumanRepository;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * 
 * @author EtienneSF
 */
@Component
public class QueryTypeDataFetchersDelegateImpl implements QueryTypeDataFetchersDelegate {

	@Resource
	CharacterRepository characterRepository;
	@Resource
	HumanRepository humanRepository;
	@Resource
	DroidRepository droidRepository;

	@Resource
	GraphQLUtil graphQLUtil;

	@Override
	public Character queryTypeHero(DataFetchingEnvironment dataFetchingEnvironment, Episode episode) {
		List<CharacterImpl> characters = characterRepository.findByAppearsIn(episode.toString());

		// For an unknown reason to me, the sample returns one item.
		if (characters.size() == 0) {
			return null;
		} else {
			return characters.get(0);
		}
	}

	@Override
	public List<Character> queryTypeCharacters(DataFetchingEnvironment dataFetchingEnvironment, Episode episode) {
		return graphQLUtil
				.iterableConcreteClassToListInterface(characterRepository.findByAppearsIn(episode.toString()));
	}

	@Override
	public Human queryTypeHuman(DataFetchingEnvironment dataFetchingEnvironment, String id) {
		return graphQLUtil.optionnalToObject(humanRepository.findById(id));
	}

	@Override
	public Droid queryTypeDroid(DataFetchingEnvironment dataFetchingEnvironment, String id) {
		return graphQLUtil.optionnalToObject(droidRepository.findById(id));
	}

}
