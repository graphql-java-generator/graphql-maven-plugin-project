/**
 * 
 */
package org.graphql.maven.plugin.samples.server.jpa;

import java.util.List;

import org.graphql.maven.plugin.samples.server.generated.Character;
import org.graphql.maven.plugin.samples.server.generated.CharacterImpl;
import org.graphql.maven.plugin.samples.server.generated.Episode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

/**
 * @author EtienneSF
 */
public interface CharacterRepository extends Repository<CharacterImpl, String> {

	// @Query("select c from Character c join character_appears_in cai on c.character_id=cai.character_id where
	// cai.episode=:episode")
	@Query("select c from Character c where :episode MEMBER OF c.appearsIn")
	// select p from Product p where: color MEMBER OF p.availableColors
	List<Character> findByAppearsIn(Episode episode);

}
