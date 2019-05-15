/**
 * 
 */
package graphql.java.client.domain.forum;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * This class is the json target for the QueryType.boards query
 * 
 * @author graphql-generator
 */
public class QueryTypeBoards {

	@JsonDeserialize(contentAs = Board.class)
	public List<Board> boards;

}
