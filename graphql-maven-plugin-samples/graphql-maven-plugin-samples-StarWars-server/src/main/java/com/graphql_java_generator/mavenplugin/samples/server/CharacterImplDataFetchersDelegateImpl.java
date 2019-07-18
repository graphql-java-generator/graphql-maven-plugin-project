package com.graphql_java_generator.mavenplugin.samples.server;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.graphql_java_generator.mavenplugin.samples.server.Character;
import com.graphql_java_generator.mavenplugin.samples.server.CharacterImpl;
import com.graphql_java_generator.mavenplugin.samples.server.CharacterImplDataFetchersDelegate;
import com.graphql_java_generator.mavenplugin.samples.server.Episode;
import com.graphql_java_generator.mavenplugin.samples.server.GraphQLUtil;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.mavenplugin.samples.server.jpa.CharacterRepository;

import graphql.schema.DataFetchingEnvironment;

@Component
public class CharacterImplDataFetchersDelegateImpl implements CharacterImplDataFetchersDelegate {

	@Resource
	CharacterRepository characterRepository;

	@Resource
	GraphQLUtil graphQLUtil;

	@Override
	public List<Character> friends(DataFetchingEnvironment dataFetchingEnvironment, CharacterImpl source) {
		return graphQLUtil.iterableConcreteClassToListInterface(characterRepository.findFriends(source.getId()));
	}

	@Override
	public List<Episode> appearsIn(DataFetchingEnvironment dataFetchingEnvironment, CharacterImpl source) {
		List<String> episodeStr = characterRepository.findAppearsInById(source.getId());
		List<Episode> ret = new ArrayList<>(episodeStr.size());
		for (String s : episodeStr) {
			ret.add(Episode.valueOf(s));
		} // for
		return ret;
	}

}
