/**
 * 
 */
package com.graphql_java_generator.mavenplugin.samples.forum.server.jpa;

import java.util.UUID;

import com.graphql_java_generator.mavenplugin.samples.forum.server.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * @author EtienneSF
 */
public interface PostRepository extends CrudRepository<Post, UUID> {

	@Query(value = "select p from Post p where p.topicId= ?1")
	Iterable<Post> findByTopicId(UUID topicId);

	@Query(value = "select p from Post p where p.topicId= ?1 and p.date >= ?2")
	Iterable<Post> findByTopicIdAndSince(UUID id, String since);

}
