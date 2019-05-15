/**
 * 
 */
package org.graphql.maven.plugin.samples.forum.server.jpa;

import java.util.List;

import org.graphql.maven.plugin.samples.forum.server.Topic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * @author EtienneSF
 */
public interface TopicRepository extends CrudRepository<Topic, String> {

	@Query(value = "select t from Topic t where t.boardId= ?1")
	List<Topic> findByBoardId(String boardId);

	@Query(value = "select t from Topic t where t.boardId= ?1 and t.date >= ?2")
	List<Topic> findByBoardIdAndSince(String boardId, String since);

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
