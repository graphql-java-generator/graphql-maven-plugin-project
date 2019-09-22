/**
 * 
 */
package com.graphql_java_generator.samples.forum.server.jpa;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.graphql_java_generator.samples.forum.server.Member;

/**
 * @author EtienneSF
 */
public interface MemberRepository extends CrudRepository<Member, UUID> {

}
