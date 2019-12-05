/**
 * 
 */
package com.graphql_java_generator.samples.forum.server.jpa;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.graphql_java_generator.samples.forum.server.Post;

/**
 * @author EtienneSF
 */
public interface PostRepository extends CrudRepository<Post, UUID> {

	@Query(value = "select p from Post p where p.topicId= ?1")
	Iterable<Post> findByTopicId(UUID topicId);

	@Query(value = "select p from Post p where p.topicId= ?1 and p.date >= ?2")
	Iterable<Post> findByTopicIdAndSince(UUID id, String since);

	/** The query for the BatchLoader */
	@Query(value = "select p from Post p where id in ?1")
	List<Post> findByIds(List<UUID> ids);
}
