/**
 * 
 */
package org.forum.server.jpa;

import java.util.Date;
import java.util.List;

import org.forum.server.graphql.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * @author etienne-sf
 */
public interface PostRepository extends CrudRepository<Post, Long> {

	@Query(value = "select p from Post p where p.topicId= ?1")
	List<Post> findByTopicId(Long topicId);

	@Query(value = "select p from Post p where p.topicId= ?1 and p.date >= ?2")
	List<Post> findByTopicIdAndSince(Long id, Date since);

	@Query(value = "select p from Post p where p.topicId= ?1 and p.authorId =?2 and p.date >= ?3")
	List<Post> findByTopicIdAndMemberIdAndSince(Long id, Long memberId, Date since);

	@Query(value = "" //
			+ " select p "//
			+ " from Post p "//
			+ " join Member m on m.id=p.authorId" //
			+ " where p.topicId= ?1 "//
			+ " and m.name =?2 "//
			+ " and p.date >= ?3")
	List<Post> findByTopicIdAndMemberNameAndSince(Long id, String memberName, Date since);

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
	List<Post> findByTopicIdAndMemberIdAndMemberNameAndSince(Long id, Long memberId, String memberName, Date since);

	/** The query for the BatchLoader */
	@Query(value = "select p from Post p where id in ?1")
	List<Post> findByIds(List<Long> ids);
}
