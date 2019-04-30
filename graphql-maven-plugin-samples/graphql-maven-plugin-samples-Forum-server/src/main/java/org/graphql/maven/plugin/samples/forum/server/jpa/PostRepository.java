/**
 * 
 */
package org.graphql.maven.plugin.samples.forum.server.jpa;

import org.graphql.maven.plugin.samples.forum.server.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * @author EtienneSF
 */
public interface PostRepository extends CrudRepository<Post, String> {

	@Query(value = "select p from Post p where p.topicId= ?1")
	Iterable<Post> findByTopicId(String topicId);

	@Query(value = "select p from Post p where p.topicId= ?1 and p.date >= ?2")
	Iterable<Post> findByTopicIdAndSince(String id, String since);

}
