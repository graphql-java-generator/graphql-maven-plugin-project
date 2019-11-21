/**
 * 
 */
package com.graphql_java_generator.samples.server.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.graphql_java_generator.Droid;

/**
 * @author EtienneSF
 */
public interface DroidRepository extends CrudRepository<Droid, UUID> {

	@Override
	@Query(value = "select d.id, d.name from droid d", nativeQuery = true)
	List<Droid> findAll();

	@Query(value = "" //
			+ " select e.label " //
			+ " from droid_appears_in dai, episode e "//
			+ " where dai.episode_id=e.id"//
			+ " and  dai.droid_id = ?1"//
			, nativeQuery = true)
	List<String> findAppearsInById(UUID id);

	@Query(value = "" //
			+ " select d.id, d.name " //
			+ " from droid d, droid_appears_in dai, episode e "//
			+ " where e.label = ?1 "//
			+ " and dai.episode_id=e.id"//
			+ " and  dai.droid_id=d.id"//
			, nativeQuery = true)
	List<Droid> findByAppearsIn(String episode);

	@Query(value = ""//
			+ " select d.id, d.name "//
			+ " from droid d, character_friends f " //
			+ " where  f.character_id = ?1 " //
			+ " and    f.friend_id = d.id " //
			, nativeQuery = true)
	List<Droid> findFriends(UUID id);

	@Modifying
	@Transactional
	@Query(value = "insert into character_friends (character_id, friend_id) values (?1, ?2)", nativeQuery = true)
	void addFriend(UUID idCharacter, UUID idFriend);

	/**
	 * As in this implementation, we have separate tables for the concrete classes of the Character interface, we use a
	 * nativeQuery. Another option is to use a CharacterImpl view.
	 */
	@Override
	@Query(value = "select id, name from droid where id = ?1", nativeQuery = true)
	Optional<Droid> findById(UUID id);

	@Query(value = "select id, name from droid where id = ?1", nativeQuery = true)
	List<Droid> batchLoader(List<UUID> keys);

}
