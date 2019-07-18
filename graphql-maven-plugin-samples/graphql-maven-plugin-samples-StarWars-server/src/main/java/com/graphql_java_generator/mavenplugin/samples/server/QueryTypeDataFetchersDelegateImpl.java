package com.graphql_java_generator.mavenplugin.samples.server;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import com.graphql_java_generator.mavenplugin.samples.server.Character;
import com.graphql_java_generator.mavenplugin.samples.server.CharacterImpl;
import com.graphql_java_generator.mavenplugin.samples.server.Droid;
import com.graphql_java_generator.mavenplugin.samples.server.Episode;
import com.graphql_java_generator.mavenplugin.samples.server.GraphQLUtil;
import com.graphql_java_generator.mavenplugin.samples.server.Human;
import com.graphql_java_generator.mavenplugin.samples.server.QueryTypeDataFetchersDelegate;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.mavenplugin.samples.server.jpa.CharacterRepository;
import com.graphql_java_generator.mavenplugin.samples.server.jpa.DroidRepository;
import com.graphql_java_generator.mavenplugin.samples.server.jpa.HumanRepository;

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
	public Character hero(DataFetchingEnvironment dataFetchingEnvironment, Episode episode) {
		List<CharacterImpl> characters;

		// episode may be null
		if (episode == null) {
			characters = characterRepository.findAll();
		} else {
			characters = characterRepository.findByAppearsIn(episode.toString());
		}

		// For an unknown reason to me, the sample returns one item.
		if (characters.size() == 0) {
			return null;
		} else {
			return characters.get(0);
		}
	}

	@Override
	public List<Character> characters(DataFetchingEnvironment dataFetchingEnvironment, Episode episode) {
		// episode may be null
		if (episode == null) {
			return graphQLUtil.iterableConcreteClassToListInterface(characterRepository.findAll());
		} else {
			return graphQLUtil
					.iterableConcreteClassToListInterface(characterRepository.findByAppearsIn(episode.toString()));
		}
	}

	@Override
	public Human human(DataFetchingEnvironment dataFetchingEnvironment, UUID id) {
		return graphQLUtil.optionnalToObject(humanRepository.findById(id));
	}

	@Override
	public Droid droid(DataFetchingEnvironment dataFetchingEnvironment, UUID id) {
		return graphQLUtil.optionnalToObject(droidRepository.findById(id));
	}

}
