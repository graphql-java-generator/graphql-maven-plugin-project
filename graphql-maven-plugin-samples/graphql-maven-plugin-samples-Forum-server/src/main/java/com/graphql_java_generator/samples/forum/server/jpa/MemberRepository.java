/**
 * 
 */
package com.graphql_java_generator.samples.forum.server.jpa;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.graphql_java_generator.samples.forum.server.Member;

/**
 * @author etienne-sf
 */
public interface MemberRepository extends CrudRepository<Member, UUID> {

	/** The query for the BatchLoader */
	@Query(value = "select m from Member m where id in ?1")
	List<Member> findByIds(List<UUID> ids);
}
