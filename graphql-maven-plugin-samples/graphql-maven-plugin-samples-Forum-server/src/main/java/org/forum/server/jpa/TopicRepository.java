/**
 * 
 */
package org.forum.server.jpa;

import java.util.Date;
import java.util.List;

import org.forum.server.graphql.Topic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * @author etienne-sf
 */
public interface TopicRepository extends CrudRepository<Topic, Long>, FindTopicRepository {

	@Query(value = "select t from Topic t where t.boardId= ?1")
	List<Topic> findByBoardId(Long boardId);

	@Query(value = "select t from Topic t where t.boardId= ?1 and t.date >= ?2")
	List<Topic> findByBoardIdAndSince(Long boardId, Date since);

	/**
	 * An example of a native query that could be used for some particular case
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

	/** The query for the BatchLoader */
	@Query(value = "select t from Topic t where id in ?1")
	List<Topic> findByIds(List<Long> ids);
}
