/**
 * 
 */
package org.graphql.maven.plugin.samples.server;

import javax.annotation.Resource;

import org.graphql.maven.plugin.samples.server.jpa.HumanRepository;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author EtienneSF
 */
@Component
public class MutationTypeDataFetchersDelegateImpl implements MutationTypeDataFetchersDelegate {

	@Resource
	HumanRepository humanRepository;

	@Override
	public Human createHuman(DataFetchingEnvironment dataFetchingEnvironment, String id, String name,
			String homePlanet) {
		Human human = new Human();
		human.setId(id);
		human.setName(name);
		human.setHomePlanet(homePlanet);
		humanRepository.save(human);
		return human;
	}

}
