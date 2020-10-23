/**
 * 
 */
package org.forum.server.jpa;

import java.util.List;

import org.forum.server.graphql.Board;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * 
 * @author etienne-sf
 */
public interface BoardRepository extends CrudRepository<Board, Long> {

	/** The query for the BatchLoader */
	@Query(value = "select b from Board b where b.id in ?1")
	List<Board> findByIds(List<Long> ids);

}
