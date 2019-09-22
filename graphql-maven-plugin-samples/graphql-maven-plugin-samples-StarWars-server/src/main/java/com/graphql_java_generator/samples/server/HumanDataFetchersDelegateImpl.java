/**
 * 
 */
package com.graphql_java_generator.samples.server;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.graphql_java_generator.samples.server.jpa.CharacterRepository;

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
	public List<Character> friends(DataFetchingEnvironment dataFetchingEnvironment, Human source) {
		return graphQLUtil.iterableConcreteClassToListInterface(characterRepository.findFriends(source.getId()));
	}

	@Override
	public List<Episode> appearsIn(DataFetchingEnvironment dataFetchingEnvironment, Human source) {
		List<String> episodeStr = characterRepository.findAppearsInById(source.getId());
		List<Episode> ret = new ArrayList<>(episodeStr.size());
		for (String s : episodeStr) {
			ret.add(Episode.valueOf(s));
		} // for
		return ret;
	}

}
