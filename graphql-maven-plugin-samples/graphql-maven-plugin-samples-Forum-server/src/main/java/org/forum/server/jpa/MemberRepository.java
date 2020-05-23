/**
 * 
 */
package org.forum.server.jpa;

import java.util.List;
import java.util.UUID;

import org.forum.server.graphql.Member;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * @author etienne-sf
 */
public interface MemberRepository extends CrudRepository<Member, UUID> {

	/** The query for the BatchLoader */
	@Query(value = "select m from Member m where id in ?1")
	List<Member> findByIds(List<UUID> ids);
}
