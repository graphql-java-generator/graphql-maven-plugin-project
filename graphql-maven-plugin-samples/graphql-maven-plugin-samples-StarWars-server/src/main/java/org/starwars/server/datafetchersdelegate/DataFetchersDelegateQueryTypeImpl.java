package org.starwars.server.datafetchersdelegate;

import java.util.List;
import java.util.UUID;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;
import org.starwars.server.Character;
import org.starwars.server.Droid;
import org.starwars.server.Episode;
import org.starwars.server.Human;
import org.starwars.server.jpa.DroidRepository;
import org.starwars.server.jpa.HumanRepository;
import org.starwars.server.util.DataFetchersDelegateQueryType;

import com.graphql_java_generator.util.GraphqlUtils;

import graphql.schema.DataFetchingEnvironment;

/**
 * 
 * @author etienne-sf
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
	GraphqlUtils graphqlUtils;

	@Override
	public Character hero(DataFetchingEnvironment dataFetchingEnvironment, Episode episode) {
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
		return graphqlUtils.optionalToObject(humanRepository.findById(id));
	}

	@Override
	public Droid droid(DataFetchingEnvironment dataFetchingEnvironment, UUID id) {
		return graphqlUtils.optionalToObject(droidRepository.findById(id));
	}

}
