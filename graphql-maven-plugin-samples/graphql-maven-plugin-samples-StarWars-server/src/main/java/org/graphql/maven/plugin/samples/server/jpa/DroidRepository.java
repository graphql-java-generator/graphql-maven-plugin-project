/**
 * 
 */
package org.graphql.maven.plugin.samples.server.jpa;

import java.util.List;

import org.graphql.maven.plugin.samples.server.generated.Droid;

/**
 * @author EtienneSF
 */
public interface DroidRepository /* extends CrudRepository<Droid, String> */ {

	List<Droid> findByEpisode(String episode);

}
