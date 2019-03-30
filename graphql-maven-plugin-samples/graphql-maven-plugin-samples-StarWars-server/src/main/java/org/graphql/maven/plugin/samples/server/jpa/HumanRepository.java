/**
 * 
 */
package org.graphql.maven.plugin.samples.server.jpa;

import org.graphql.maven.plugin.samples.server.generated.CharacterType;
import org.graphql.maven.plugin.samples.server.generated.Human;
import org.springframework.data.repository.CrudRepository;

/**
 * @author EtienneSF
 */
public interface HumanRepository extends CrudRepository<Human, String> {

	Human findByTypeAndId(CharacterType type, String id);

}
