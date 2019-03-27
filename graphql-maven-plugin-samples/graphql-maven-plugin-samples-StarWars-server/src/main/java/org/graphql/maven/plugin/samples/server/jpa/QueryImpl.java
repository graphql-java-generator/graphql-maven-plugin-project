/**
 * 
 */
package org.graphql.maven.plugin.samples.server.jpa;

import java.util.Optional;

import javax.annotation.Resource;

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
	HumanRepository humanRepo;

	@Override
	protected Human doHuman(String id) {
		Optional<org.graphql.maven.plugin.samples.server.generated.Human> ret = humanRepo.findById(id);
		if (ret.isPresent()) {
			logger.trace("Response to query doHuman(id:{}) = {}", id, ret);
			return ret.get();
		} else
			return null;
	}

}
