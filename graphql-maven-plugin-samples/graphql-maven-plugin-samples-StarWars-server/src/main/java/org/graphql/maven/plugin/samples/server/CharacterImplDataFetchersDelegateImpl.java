package org.graphql.maven.plugin.samples.server;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.graphql.maven.plugin.samples.server.Character;
import org.graphql.maven.plugin.samples.server.CharacterImpl;
import org.graphql.maven.plugin.samples.server.CharacterImplDataFetchersDelegate;
import org.graphql.maven.plugin.samples.server.Episode;
import org.graphql.maven.plugin.samples.server.jpa.CharacterRepository;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class CharacterImplDataFetchersDelegateImpl implements CharacterImplDataFetchersDelegate {

	@Resource
	CharacterRepository characterRepository;

	@Resource
	GraphQLUtil graphQLUtil;

	@Override
	public List<Character> characterFriends(DataFetchingEnvironment dataFetchingEnvironment, CharacterImpl source) {
		return graphQLUtil.iterableConcreteClassToListInterface(characterRepository.findFriends(source.getId()));
	}

	@Override
	public List<Episode> characterAppearsIn(DataFetchingEnvironment dataFetchingEnvironment, CharacterImpl source) {
		List<String> episodeStr = characterRepository.findAppearsInById(source.getId());
		List<Episode> ret = new ArrayList<>(episodeStr.size());
		for (String s : episodeStr) {
			ret.add(Episode.valueOf(s));
		} // for
		return ret;
	}

}
