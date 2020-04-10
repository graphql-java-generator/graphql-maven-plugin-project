/**
 * 
 */
package com.graphql_java_generator.samples.forum.server.jpa;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.graphql_java_generator.samples.forum.server.Board;

/**
 * @author etienne-sf
 */
public interface BoardRepository extends CrudRepository<Board, UUID> {

	/** The query for the BatchLoader */
	@Query(value = "select b from Board b where b.id in ?1")
	List<Board> findByIds(List<UUID> ids);

}
