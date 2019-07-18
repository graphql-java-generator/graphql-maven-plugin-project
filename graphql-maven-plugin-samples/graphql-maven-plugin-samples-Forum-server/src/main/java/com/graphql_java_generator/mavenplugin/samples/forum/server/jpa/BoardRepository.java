/**
 * 
 */
package com.graphql_java_generator.mavenplugin.samples.forum.server.jpa;

import java.util.UUID;

import com.graphql_java_generator.mavenplugin.samples.forum.server.Board;
import org.springframework.data.repository.CrudRepository;

/**
 * @author EtienneSF
 */
public interface BoardRepository extends CrudRepository<Board, UUID> {

}
