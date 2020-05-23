/**
 * 
 */
package org.forum.server.jpa;

import java.util.List;

import org.forum.server.graphql.Topic;

/**
 * @author etienne-sf
 */
public interface FindTopicRepository {

	/**
	 * Search for {@link Topic}, based on
	 * 
	 * @param boardName
	 * @param keyword
	 * @return
	 */
	List<Topic> findByBoardNameAndKeywords(String boardName, List<String> keyword);

}
