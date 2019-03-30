/**
 * 
 */
package org.graphql.maven.plugin.samples.server.jpa;

import java.util.List;

import javax.annotation.Resource;

import org.graphql.maven.plugin.samples.server.generated.Character;
import org.graphql.maven.plugin.samples.server.generated.CharacterType;
import org.graphql.maven.plugin.samples.server.generated.Droid;
import org.graphql.maven.plugin.samples.server.generated.Episode;
import org.graphql.maven.plugin.samples.server.generated.Human;
import org.graphql.maven.plugin.samples.server.generated.QueryType;
import org.springframework.stereotype.Component;

/**
 * graphql-maven-plugin generates a lot of things. But there is still some work for you! :)
 * 
 * @author EtienneSF
 */
@Component
public class QueryImpl extends QueryType {

	@Resource
	CharacterRepository characterRepo;

	@Resource
	HumanRepository humanRepo;

	@Override
	protected Character doHero(Episode episode) {
		List<Character> ret = characterRepo.findByAppearsIn(episode);
		if (ret.size() > 0) {
			return ret.get(0);
		} else {
			return null;
		}
	}

	@Override
	protected Human doHuman(String id) {
		Human ret = humanRepo.findByTypeAndId(CharacterType.HUMAN, id);
		if (ret != null) {
			logger.trace("Response to query doHuman(id:{}) = {}", id, ret);
			return ret;
		} else
			return null;
	}

	@Override
	protected Droid doDroid(String primaryFunction) {
		return null;
	}

}
