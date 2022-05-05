package com.spring_graphql_test_client;

import java.util.Date;
import java.util.List;

import org.forum.generated.Board;
import org.forum.generated.Mutation;
import org.forum.generated.Post;
import org.forum.generated.PostInput;
import org.forum.generated.Query;
import org.forum.generated.Topic;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.graphqlrepository.BindParameter;
import com.graphql_java_generator.client.graphqlrepository.FullRequest;
import com.graphql_java_generator.client.graphqlrepository.GraphQLRepository;
import com.graphql_java_generator.client.graphqlrepository.PartialRequest;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

@GraphQLRepository
public interface MyGraphQLRepository {

	/**
	 * Execution of the boards query, with this GraphQL query: {id name publiclyAvailable topics {id title date
	 * nbPosts}}
	 * 
	 * @return The GraphQL server's response, mapped into the POJO generated from the GraphQL schema
	 * @throws GraphQLRequestExecutionException
	 *             Whenever an exception occurs during request execution
	 */
	@PartialRequest(request = "{id name publiclyAvailable topics {id title date nbPosts}}")
	public List<Board> boards() throws GraphQLRequestExecutionException;

	/**
	 * Execution of the topics query, which has one parameter defined in the GraphQL schema: boardName, that is a
	 * String. If this query had more then one parameter, all its parameters should be parameters of this method, in the
	 * same order a defined in the GraphQL schema, and with the relevant Java type.
	 * 
	 * @param aBoardName
	 * @return
	 * @throws GraphQLRequestExecutionException
	 *             Whenever an exception occurs during request execution
	 */
	@PartialRequest(request = "{id date author {id name} nbPosts title content}")
	public List<Topic> topics(String aBoardName) throws GraphQLRequestExecutionException;

	/**
	 * 
	 * @param boardName
	 *            The parameter of the topics query, as defined in the GraphQL schema
	 * @param memberId
	 *            The bind parameter 'memberId', as defined in this query. It's an optional parameter, as this parameter
	 *            is marked by a '?' character in this query. That is: the provided value may be null, at execution
	 *            time.
	 * @param memberName
	 *            The bind parameter 'memberName', as defined in this query. It's an optional parameter, as this
	 *            parameter is marked by a '?' character in this query. That is: the provided value may be null, at
	 *            execution time.
	 * @param since
	 *            The bind parameter 'since', as defined in this query. It's a mandatory parameter, as this parameter is
	 *            marked by a '&' character in this query. That is: the provided value may NOT be null, at execution
	 *            time.
	 * @throws GraphQLRequestExecutionException
	 *             Whenever an exception occurs during request execution
	 * @return
	 */
	@PartialRequest(requestName = "topics", // The requestName defines the GraphQL defined query. It is mandatory here,
											// as the method has a different name than the query's name in the GraphQL
											// schema
			request = "{id date author {id name} nbPosts title content posts(memberId: ?memberId, memberName: ?memberName, since: &since)  {id date title}}")
	public List<Topic> topicsSince(String boardName, //
			@BindParameter(name = "memberId") String memberId, //
			@BindParameter(name = "memberName") String memberName, //
			@BindParameter(name = "since") Date since) throws GraphQLRequestExecutionException;

	/**
	 * A full Request returns the Query or the Mutation type, as it is defined in the GraphQL schema. You'll have then
	 * to use the relevant getter(s) to retrieve the request's result
	 * 
	 * @return
	 */
	@FullRequest(request = "fragment topicFields on Topic {id title date} "
			+ "query{boards{id name publiclyAvailable topics {...topicFields nbPosts}}}")
	public Query fullQueryWithFragment() throws GraphQLRequestExecutionException;

	/**
	 * A query with inline fragments
	 * 
	 * @return
	 * @throws GraphQLRequestExecutionException
	 */
	@PartialRequest(requestName = "boards", request = "{id name publiclyAvailable topics {... on Topic {id title date} nbPosts}}")
	List<Board> boardsWithInlineFragment() throws GraphQLRequestExecutionException;

	/**
	 * A mutation sample, within a Partial Request
	 * 
	 * @param postInput
	 *            The post to be created
	 * 
	 * @return The created Post
	 */
	@PartialRequest(request = "{ id date author{id name} title content}", //
			requestType = RequestType.mutation/* default request type for Partial Query is query */)
	Post createPost(PostInput postInput) throws GraphQLRequestExecutionException;

	/**
	 * A mutation sample, within a Full Request
	 * 
	 * @param postInput
	 *            The post to be created
	 * @return The {@link Mutation} type. Calling the {@link Mutation#getCreatePost()} allows to retrieve the return for
	 *         the createPost mutation, that is: the created post
	 */
	@FullRequest(request = "mutation {createPost(post: &postInput) { id date author{id name} title content}}", //
			requestType = RequestType.mutation/* default request type for Partial Query is query */)
	Mutation createPostFullRequest(@BindParameter(name = "postInput") PostInput postInput)
			throws GraphQLRequestExecutionException;
}
