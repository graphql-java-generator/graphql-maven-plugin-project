/**
 * 
 */
package com.graphql_java_generator.samples.forum.server.jpa;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.graphql_java_generator.samples.forum.server.Post;

/**
 * @author etienne-sf
 */
public interface PostRepository extends CrudRepository<Post, UUID> {

	@Query(value = "select p from Post p where p.topicId= ?1")
	List<Post> findByTopicId(UUID topicId);

	@Query(value = "select p from Post p where p.topicId= ?1 and p.date >= ?2")
	List<Post> findByTopicIdAndSince(UUID id, Date since);

	@Query(value = "select p from Post p where p.topicId= ?1 and p.authorId =?2 and p.date >= ?3")
	List<Post> findByTopicIdAndMemberIdAndSince(UUID id, UUID memberId, Date since);

	@Query(value = "" //
			+ " select p "//
			+ " from Post p "//
			+ " join Member m on m.id=p.authorId" //
			+ " where p.topicId= ?1 "//
			+ " and m.name =?2 "//
			+ " and p.date >= ?3")
	List<Post> findByTopicIdAndMemberNameAndSince(UUID id, String memberName, Date since);

	// It's actually a non sense request, as if you provide author_id, it's useless to provide his/her name. But, as
	// it's a technical possibility, the query must be defined
	@Query(value = "" //
			+ " select p "//
			+ " from Post p "//
			+ " join Member m on m.id=p.authorId" //
			+ " where p.topicId= ?1 "//
			+ " and p.authorId =?2 "//
			+ " and m.name = ?3 "//
			+ " and p.date >= ?4")
	List<Post> findByTopicIdAndMemberIdAndMemberNameAndSince(UUID id, UUID memberId, String memberName, Date since);

	/** The query for the BatchLoader */
	@Query(value = "select p from Post p where id in ?1")
	List<Post> findByIds(List<UUID> ids);
}
