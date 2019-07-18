/**
 * 
 */
package com.graphql_java_generator.mavenplugin.samples.forum.server.jpa;

import java.util.UUID;

import com.graphql_java_generator.mavenplugin.samples.forum.server.Member;
import org.springframework.data.repository.CrudRepository;

/**
 * @author EtienneSF
 */
public interface MemberRepository extends CrudRepository<Member, UUID> {

}
