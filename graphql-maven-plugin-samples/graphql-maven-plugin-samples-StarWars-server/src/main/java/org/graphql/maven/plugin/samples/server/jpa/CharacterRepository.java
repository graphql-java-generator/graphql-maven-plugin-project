/**
 * 
 */
package org.graphql.maven.plugin.samples.server.jpa;

import java.util.List;

import org.graphql.maven.plugin.samples.server.generated.CharacterImpl;
import org.graphql.maven.plugin.samples.server.generated.Episode;
import org.springframework.data.repository.CrudRepository;

/**
 * @author EtienneSF
 */
public interface CharacterRepository extends CrudRepository<CharacterImpl, String> {

	List<CharacterImpl> findByFirstEpisode(Episode episode);

}
