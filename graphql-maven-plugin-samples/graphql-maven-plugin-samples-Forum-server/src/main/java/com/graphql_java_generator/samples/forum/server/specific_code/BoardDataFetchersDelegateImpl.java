/**
 * 
 */
package com.graphql_java_generator.samples.forum.server.specific_code;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.graphql_java_generator.samples.forum.server.Board;
import com.graphql_java_generator.samples.forum.server.BoardDataFetchersDelegate;
import com.graphql_java_generator.samples.forum.server.Topic;
import com.graphql_java_generator.samples.forum.server.jpa.TopicRepository;

import graphql.schema.DataFetchingEnvironment;

/**
 * This class implements the access to the database : there are so many ways to do this, that the developper has still
 * work to do. But all the GraphQL boilerplate is generated.<BR/>
 * The {@link GraphQLDataFetchersDelegate} interface is generated from the given schema
 * 
 * @author EtienneSF
 */
@Component
public class BoardDataFetchersDelegateImpl implements BoardDataFetchersDelegate {

	@Resource
	TopicRepository topicRepository;

	@Override
	public List<Topic> topics(DataFetchingEnvironment dataFetchingEnvironment, Board source, String since) {
		if (since == null)
			return topicRepository.findByBoardId(source.getId());
		else
			return topicRepository.findByBoardIdAndSince(source.getId(), since);
	}
}
