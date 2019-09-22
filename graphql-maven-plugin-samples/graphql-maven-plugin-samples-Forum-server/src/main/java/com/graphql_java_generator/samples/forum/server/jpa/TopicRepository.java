/**
 * 
 */
package com.graphql_java_generator.samples.forum.server.jpa;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.graphql_java_generator.samples.forum.server.Topic;

/**
 * @author EtienneSF
 */
public interface TopicRepository extends CrudRepository<Topic, UUID> {

	@Query(value = "select t from Topic t where t.boardId= ?1")
	List<Topic> findByBoardId(UUID boardId);

	@Query(value = "select t from Topic t where t.boardId= ?1 and t.date >= ?2")
	List<Topic> findByBoardIdAndSince(UUID boardId, String since);

	/**
	 * An example of a native query that could be used for some perticular case
	 * 
	 * @param name
	 * @return
	 */
	@Query(value = "" //
			+ " select * " //
			+ " from topic t "//
			+ " join board b on t.board_id = b.id " //
			+ " where b.name = ?1" //
			, nativeQuery = true)
	List<Topic> findByBoardName(String name);

}
