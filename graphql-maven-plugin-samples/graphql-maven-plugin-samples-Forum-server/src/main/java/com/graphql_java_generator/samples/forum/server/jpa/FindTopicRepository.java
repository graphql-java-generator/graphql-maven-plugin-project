/**
 * 
 */
package com.graphql_java_generator.samples.forum.server.jpa;

import java.util.List;

import com.graphql_java_generator.samples.forum.server.Topic;

/**
 * @author EtienneSF
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
