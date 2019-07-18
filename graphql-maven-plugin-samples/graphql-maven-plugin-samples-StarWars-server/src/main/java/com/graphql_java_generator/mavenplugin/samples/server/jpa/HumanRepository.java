/**
 * 
 */
package com.graphql_java_generator.mavenplugin.samples.server.jpa;

import java.util.UUID;

import com.graphql_java_generator.mavenplugin.samples.server.Human;
import org.springframework.data.repository.CrudRepository;

/**
 * @author EtienneSF
 */
public interface HumanRepository extends CrudRepository<Human, UUID> {

}
