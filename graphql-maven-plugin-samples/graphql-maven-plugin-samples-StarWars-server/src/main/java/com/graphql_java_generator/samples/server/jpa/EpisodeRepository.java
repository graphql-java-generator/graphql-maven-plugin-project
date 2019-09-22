/**
 * 
 */
package com.graphql_java_generator.samples.server.jpa;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;

/**
 * Repository is commented: so it's no more found by Spring<BR/>
 * TODO remove if really useless
 * 
 * @author EtienneSF
 */
public interface EpisodeRepository {

	@Query(value = ""//
			+ " select e.label "//
			+ " from droid_appears_in dai, episode e " //
			+ " where  dai.droid_id = ?1 " //
			+ " and    dai.episode_id = e.id " //
			+ "UNION ALL "//
			+ " select e.label "//
			+ " from   human_appears_in hai, episode e " //
			+ " where  hai.human_id = ?1 " //
			+ " and    hai.episode_id = e.id " //
			, nativeQuery = true)
	List<String> findAppearsIn(UUID id);
}
