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
public class QueryTypeTopics {

	@JsonDeserialize(contentAs = Topic.class)
	public List<Topic> topics;

}
