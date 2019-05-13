/**
 * 
 */
package org.graphql.maven.plugin.samples.server;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.graphql.maven.plugin.samples.server.generated.Character;
import org.graphql.maven.plugin.samples.server.generated.Episode;
import org.graphql.maven.plugin.samples.server.generated.Human;
import org.graphql.maven.plugin.samples.server.generated.HumanDataFetchersDelegate;
import org.graphql.maven.plugin.samples.server.jpa.CharacterRepository;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author EtienneSF
 */
@Component
public class HumanDataFetchersDelegateImpl implements HumanDataFetchersDelegate {

	@Resource
	CharacterRepository characterRepository;

	@Resource
	GraphQLUtil graphQLUtil;

	@Override
	public List<Character> humanFriends(DataFetchingEnvironment dataFetchingEnvironment, Human source) {
		return graphQLUtil.iterableConcreteClassToListInterface(characterRepository.findFriends(source.getId()));
	}

	@Override
	public List<Episode> humanAppearsIn(DataFetchingEnvironment dataFetchingEnvironment, Human source) {
		List<String> episodeStr = characterRepository.findAppearsInById(source.getId());
		List<Episode> ret = new ArrayList<>(episodeStr.size());
		for (String s : episodeStr) {
			ret.add(Episode.valueOf(s));
		} // for
		return ret;
	}

}
