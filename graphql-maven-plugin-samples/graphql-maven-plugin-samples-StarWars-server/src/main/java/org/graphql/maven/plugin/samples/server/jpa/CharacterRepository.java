/**
 * 
 */
package org.graphql.maven.plugin.samples.server.jpa;

import java.util.List;

import org.graphql.maven.plugin.samples.server.generated.CharacterImpl;
import org.graphql.maven.plugin.samples.server.generated.Episode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

/**
 * @author EtienneSF
 */
public interface CharacterRepository extends Repository<CharacterImpl, String> {

	// @Query("select c from Character c ")
	List<CharacterImpl> findAll();

	@Query("select c from Character c where :episode MEMBER OF c.appearsIn")
	List<CharacterImpl> findByAppearsIn(Episode episode);

	@Query("select c from Character c inner join c.friends f where f.id = :id")
	List<CharacterImpl> findFriends(String id);

}
