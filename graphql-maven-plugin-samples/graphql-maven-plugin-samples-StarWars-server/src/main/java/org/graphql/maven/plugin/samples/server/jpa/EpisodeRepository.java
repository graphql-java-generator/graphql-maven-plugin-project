/**
 * 
 */
package org.graphql.maven.plugin.samples.server.jpa;

import java.util.List;

import org.graphql.maven.plugin.samples.server.graphql.Episode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

/**
 * @author EtienneSF
 */
public interface EpisodeRepository extends Repository<Episode, Integer> {

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
	List<String> findAppearsIn(String id);
}
