/**
 * 
 */
package org.graphql.maven.plugin.samples.server.jpa;

import java.util.List;

import org.graphql.maven.plugin.samples.server.graphql.CharacterImpl;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

/**
 * @author EtienneSF
 */
public interface CharacterRepository extends Repository<CharacterImpl, String> {

	@Query(value = "select d.id, d.name from droid d UNION ALL select h.id, h.name from human h", nativeQuery = true)
	List<CharacterImpl> findAll();

	@Query(value = "" //
			+ " select d.id, d.name " //
			+ " from droid d, droid_appears_in dai, episode e "//
			+ " where e.label = ?1 "//
			+ " and dai.episode_id=e.id"//
			+ " and  dai.droid_id=d.id"//
			+ " UNION ALL" //
			+ " select h.id, h.name " //
			+ " from human h, human_appears_in hai, episode e "//
			+ " where e.label = ?1 "//
			+ " and   hai.episode_id = e.id"//
			+ " and   hai.human_id = h.id" //
			, nativeQuery = true)
	List<CharacterImpl> findByAppearsIn(String episode);

	@Query(value = ""//
			+ " select d.id, d.name "//
			+ " from droid d, character_friends f " //
			+ " where  f.character_id = ?1 " //
			+ " and    f.friend_id = d.id " //
			+ "UNION ALL "//
			+ " select h.id, h.name "//
			+ " from human h, character_friends f " //
			+ " where  f.character_id = ?1 " //
			+ " and    f.friend_id = h.id " //
			, nativeQuery = true)
	List<CharacterImpl> findFriends(String id);

}