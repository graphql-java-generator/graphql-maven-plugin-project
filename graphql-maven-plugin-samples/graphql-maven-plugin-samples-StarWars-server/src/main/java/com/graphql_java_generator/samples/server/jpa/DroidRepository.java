/**
 * 
 */
package com.graphql_java_generator.samples.server.jpa;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.graphql_java_generator.samples.server.Droid;

/**
 * @author EtienneSF
 */
public interface DroidRepository extends CrudRepository<Droid, UUID> {

}
