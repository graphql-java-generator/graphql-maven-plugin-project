/**
 * 
 */
package org.graphql.maven.plugin.samples.server.jpa;

import java.util.UUID;

import org.graphql.maven.plugin.samples.server.Droid;
import org.springframework.data.repository.CrudRepository;

/**
 * @author EtienneSF
 */
public interface DroidRepository extends CrudRepository<Droid, UUID> {

}
