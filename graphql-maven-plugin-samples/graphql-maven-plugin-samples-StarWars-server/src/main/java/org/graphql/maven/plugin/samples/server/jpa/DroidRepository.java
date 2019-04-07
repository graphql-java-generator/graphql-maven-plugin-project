/**
 * 
 */
package org.graphql.maven.plugin.samples.server.jpa;

import org.graphql.maven.plugin.samples.server.generated.CharacterType;
import org.graphql.maven.plugin.samples.server.generated.Droid;
import org.springframework.data.repository.CrudRepository;

/**
 * @author EtienneSF
 */
public interface DroidRepository extends CrudRepository<Droid, String> {

	Droid findByTypeAndId(CharacterType type, String id);

}
