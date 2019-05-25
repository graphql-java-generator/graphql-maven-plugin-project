/**
 * 
 */
package org.graphql.maven.plugin.samples.forum.server.jpa;

import java.util.UUID;

import org.graphql.maven.plugin.samples.forum.server.Member;
import org.springframework.data.repository.CrudRepository;

/**
 * @author EtienneSF
 */
public interface MemberRepository extends CrudRepository<Member, UUID> {

}
