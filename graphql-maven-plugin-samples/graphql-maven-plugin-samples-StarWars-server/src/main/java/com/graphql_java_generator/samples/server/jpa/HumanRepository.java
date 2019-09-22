/**
 * 
 */
package com.graphql_java_generator.samples.server.jpa;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.graphql_java_generator.samples.server.Human;

/**
 * @author EtienneSF
 */
public interface HumanRepository extends CrudRepository<Human, UUID> {

}
