/**
 * 
 */
package org.graphql.maven.plugin.samples.forum.server.specific_code;

import java.util.ArrayList;
import java.util.List;

import org.graphql.maven.plugin.samples.forum.server.Board;
import org.graphql.maven.plugin.samples.forum.server.BoardDataFetchersDelegate;
import org.graphql.maven.plugin.samples.forum.server.Topic;
import org.springframework.stereotype.Component;

/**
 * This class implements the access to the database : there are so many ways to do this, that the developper has still
 * work to do. But all the GraphQL boilerplate is generated.<BR/>
 * The {@link GraphQLDataFetchersDelegate} interface is generated from the given schema
 * 
 * @author EtienneSF
 */
@Component
public class BoardDataFetchersDelegateImpl implements BoardDataFetchersDelegate {

	public List<Board> QueryTypeBoards() {
		List<Board> ret = new ArrayList<>();
		return ret;
	}

	@Override
	public List<Topic> boardTopics(String boardId, String since) {
		List<Topic> ret = new ArrayList<>();
		return ret;
	}
}
