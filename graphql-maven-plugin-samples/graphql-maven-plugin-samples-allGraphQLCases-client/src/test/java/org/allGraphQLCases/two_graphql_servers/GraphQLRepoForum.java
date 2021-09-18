/**
 * 
 */
package org.allGraphQLCases.two_graphql_servers;

import java.util.Date;
import java.util.List;

import org.forum.client.Topic;
import org.forum.client.util.QueryTypeExecutorForum;

import com.graphql_java_generator.client.graphqlrepository.BindParameter;
import com.graphql_java_generator.client.graphqlrepository.GraphQLRepository;
import com.graphql_java_generator.client.graphqlrepository.PartialRequest;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * This is a GraphQL Repository that is based on the allGraphQLCases GraphQL schema, thanks to the
 * {@link QueryTypeExecutorForum} class provided to the {@link GraphQLRepository} annotation.
 * 
 * @author etienne-sf
 */
@GraphQLRepository(queryExecutor = QueryTypeExecutorForum.class)
public interface GraphQLRepoForum {

	@PartialRequest(requestName = "topics", request = "{id date author{name email alias id type} nbPosts title content " //
			+ "posts(since: &sinceParam){id date author{name email alias} title content}}")
	List<Topic> topicAuthorPostAuthor(String boardName, @BindParameter(name = "sinceParam") Date since)
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException;

}
