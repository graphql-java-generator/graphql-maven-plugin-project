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

import com.graphql_java_generator.Human;

/**
 * @author EtienneSF
 */
public interface HumanRepository extends CrudRepository<Human, UUID> {

	@Override
	@Query(value = "select d.id, d.name from human d", nativeQuery = true)
	List<Human> findAll();

	@Query(value = "" //
			+ " select e.label " //
			+ " from human_appears_in dai, episode e "//
			+ " where dai.episode_id=e.id"//
			+ " and  dai.human_id = ?1"//
			, nativeQuery = true)
	List<String> findAppearsInById(UUID id);

	@Query(value = "" //
			+ " select d.id, d.name " //
			+ " from human d, human_appears_in dai, episode e "//
			+ " where e.label = ?1 "//
			+ " and dai.episode_id=e.id"//
			+ " and  dai.human_id=d.id"//
			, nativeQuery = true)
	List<Human> findByAppearsIn(String episode);

	@Query(value = ""//
			+ " select d.id, d.name "//
			+ " from human d, character_friends f " //
			+ " where  f.character_id = ?1 " //
			+ " and    f.friend_id = d.id " //
			, nativeQuery = true)
	List<Human> findFriends(UUID id);

	@Modifying
	@Transactional
	@Query(value = "insert into character_friends (character_id, friend_id) values (?1, ?2)", nativeQuery = true)
	void addFriend(UUID idCharacter, UUID idFriend);

	/**
	 * As in this implementation, we have separate tables for the concrete classes of the Character interface, we use a
	 * nativeQuery. Another option is to use a CharacterImpl view.
	 */
	@Override
	@Query(value = "select id, name from human where id = ?1", nativeQuery = true)
	Optional<Human> findById(UUID id);

	@Query(value = "select id, name from human where id = ?1", nativeQuery = true)
	List<Human> batchLoader(List<String> keys);

}
