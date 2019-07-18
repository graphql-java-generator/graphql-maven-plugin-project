/**
 * 
 */
package com.graphql_java_generator.mavenplugin.samples.server.jpa;

import java.util.UUID;

import com.graphql_java_generator.mavenplugin.samples.server.Droid;
import org.springframework.data.repository.CrudRepository;

/**
 * @author EtienneSF
 */
public interface DroidRepository extends CrudRepository<Droid, UUID> {

}
