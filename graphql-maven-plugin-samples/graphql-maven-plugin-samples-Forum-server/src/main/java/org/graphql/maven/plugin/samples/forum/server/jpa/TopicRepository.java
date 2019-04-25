/**
 * 
 */
package org.graphql.maven.plugin.samples.forum.server.jpa;

import org.graphql.maven.plugin.samples.forum.server.Topic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * @author EtienneSF
 */
public interface TopicRepository extends CrudRepository<Topic, String> {

	@Query(value = "" //
			+ " select * " //
			+ " from topic t "//
			+ " join board b on t.boardId = b.id " //
			+ " where b.name = ?1" //
			, nativeQuery = true)
	Iterable<Topic> findByBoardName(String name);

}
