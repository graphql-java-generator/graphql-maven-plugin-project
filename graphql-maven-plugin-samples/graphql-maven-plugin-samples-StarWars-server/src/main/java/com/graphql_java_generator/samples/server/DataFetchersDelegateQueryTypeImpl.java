package com.graphql_java_generator.samples.server;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.graphql_java_generator.samples.server.jpa.DroidRepository;
import com.graphql_java_generator.samples.server.jpa.HumanRepository;

import graphql.schema.DataFetchingEnvironment;

/**
 * 
 * @author EtienneSF
 */
@Component
public class DataFetchersDelegateQueryTypeImpl implements DataFetchersDelegateQueryType {

	@Resource
	HumanRepository humanRepository;
	@Resource
	DroidRepository droidRepository;
	@Resource
	CharacterHelper characterHelper;

	@Resource
	GraphQLUtil graphQLUtil;

	@Override
	public Character hero(DataFetchingEnvironment dataFetchingEnvironment, Episode episode) {
		List<Character> characters;

		// episode may be null
		if (episode == null)
			// Let's say that the first of the list is the main hero
			return characterHelper.findAll().get(0);
		else
			// Let's say that the first of the list is the main hero
			return characterHelper.findByAppearsIn(episode.toString()).get(0);
	}

	@Override
	public List<Character> characters(DataFetchingEnvironment dataFetchingEnvironment, Episode episode) {
		// episode may be null
		if (episode == null)
			return characterHelper.findAll();
		else
			return characterHelper.findByAppearsIn(episode.toString());
	}

	@Override
	public Human human(DataFetchingEnvironment dataFetchingEnvironment, UUID id) {
		return graphQLUtil.optionalToObject(humanRepository.findById(id));
	}

	@Override
	public Droid droid(DataFetchingEnvironment dataFetchingEnvironment, UUID id) {
		return graphQLUtil.optionalToObject(droidRepository.findById(id));
	}

}
