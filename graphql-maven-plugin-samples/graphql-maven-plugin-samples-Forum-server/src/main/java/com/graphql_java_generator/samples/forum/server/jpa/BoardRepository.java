/**
 * 
 */
package com.graphql_java_generator.samples.forum.server.jpa;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.graphql_java_generator.samples.forum.server.Board;

/**
 * @author EtienneSF
 */
public interface BoardRepository extends CrudRepository<Board, UUID> {

}
