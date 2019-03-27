/**
 * 
 */
package org.graphql.maven.plugin.samples.server.jpa;

import java.util.Optional;

import javax.annotation.Resource;

import org.graphql.maven.plugin.samples.server.generated.Character;
import org.graphql.maven.plugin.samples.server.generated.Droid;
import org.graphql.maven.plugin.samples.server.generated.Episode;
import org.graphql.maven.plugin.samples.server.generated.Human;
import org.graphql.maven.plugin.samples.server.generated.QueryType;

/**
 * graphql-maven-plugin generates a lot of things. But there is still some work for you! :)
 * 
 * @author EtienneSF
 */
public class QueryImpl extends QueryType {

	@Resource
	HumanRepository humanRepo;

	@Override
	protected Character doHero(Episode episode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Human doHuman(String id) {
		Optional<org.graphql.maven.plugin.samples.server.generated.Human> ret = humanRepo.findById(id);
		if (ret.isPresent())
			return ret.get();
		else
			return null;
	}

	@Override
	protected Droid doDroid(String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
