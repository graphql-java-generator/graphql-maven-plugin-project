package com.graphql_java_generator.samples.forum.client.graphql;

import java.util.Date;
import java.util.List;

import com.graphql_java_generator.client.GraphQLConfiguration;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.Main;
import com.graphql_java_generator.samples.forum.client.Queries;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Board;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.GraphQLRequest;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Member;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.MemberInput;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.MutationType;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Post;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.PostInput;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.QueryType;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Topic;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.TopicInput;

/**
 * This class implements the away to call GraphQl queries, where all queries are prepared before execution.<BR/>
 * The advantages are:
 * <UL>
 * <LI>Performance: this avoid to build an {@link ObjectResponse} for each response. This {@link ObjectResponse} is
 * useful, to help control at runtime if a field has been queried or not. It allows to throw an exception when your code
 * tries to use a field that was not queried</LI>
 * <LI>Security: as all request have been prepared at startup, this make sure at startup that your queries are
 * valid.</LI>
 * </UL>
 * 
 * @author etienne-sf
 */
public class PartialPreparedRequests implements Queries {

	QueryType queryType = new QueryType(Main.GRAPHQL_ENDPOINT_URL);
	MutationType mutationType = new MutationType(Main.GRAPHQL_ENDPOINT_URL);

	// Below are the ObjectResponses, that are created at initialization time.
	GraphQLRequest boardsSimpleRequest;
	GraphQLRequest boardsAndTopicsRequest;
	GraphQLRequest topicAuthorPostAuthorRequest;
	GraphQLRequest findTopicIdDateTitleContentRequest;
	GraphQLRequest createBoardRequest;
	GraphQLRequest createTopicRequest;
	GraphQLRequest createMemberRequest;
	GraphQLRequest createPostRequest;
	GraphQLRequest createPostsRequest;

	public PartialPreparedRequests() throws GraphQLRequestPreparationException {

		// We have only one GraphQL server. So we just set the default configuration.
		GraphQLRequest.setStaticConfiguration(new GraphQLConfiguration(Main.GRAPHQL_ENDPOINT_URL));

		// No field specified: all scalar fields of the root type will be queried
		boardsSimpleRequest = queryType.getBoardsGraphQLRequest(null);

		boardsAndTopicsRequest = queryType
				.getBoardsGraphQLRequest("{id name publiclyAvailable topics(since:?since){id}}");

		topicAuthorPostAuthorRequest = queryType
				.getTopicsGraphQLRequest("{id date author{name email alias id type} nbPosts title content " //
						+ "posts(memberId:?memberId, memberName: ?memberName, since: &sinceParam){id date author{name email alias} title content}}");

		findTopicIdDateTitleContentRequest = queryType.getFindTopicsGraphQLRequest(" {id date title content} ");

		// No field defined, so all scalar fields are returned
		createBoardRequest = mutationType.getCreateBoardGraphQLRequest(null);

		// No field defined, so all scalar fields are returned
		createTopicRequest = mutationType.getCreateTopicGraphQLRequest(null);

		// {id name alias email type}
		createMemberRequest = mutationType.getCreateMemberGraphQLRequest("{id name alias email type}");

		// "{id date author{id} title content publiclyAvailable}"
		createPostRequest = mutationType
				.getCreatePostGraphQLRequest("{id date author{id} title content publiclyAvailable}");

		// "{id date author{id} title content publiclyAvailable}"
		createPostsRequest = mutationType
				.getCreatePostsGraphQLRequest("{id date author{id} title content publiclyAvailable}");
	}

	@Override
	public List<Board> boardsSimple() throws GraphQLRequestExecutionException {
		// boardsAndTopicsRequest has been create with this query string:
		// (empty) which means that all scalars are returned
		return queryType.boards(boardsSimpleRequest);
	}

	@Override
	public List<Board> boardsAndTopicsWithFieldParameter(Date since)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// boardsAndTopicsRequest has been create with this query string:
		// {id name publiclyAvailable topics(since:?since){id}}
		return queryType.boards(boardsAndTopicsRequest, "since", since);
	}

	@Override
	public List<Topic> topicAuthorPostAuthor(String boardName, Date since) throws GraphQLRequestExecutionException {
		// topicAuthorPostAuthorRequest has been create with this query string:
		// {id date author{name email alias id type} nbPosts title content posts(memberId:?memberId, memberName:
		// ?memberName, since: &sinceParam){id date author{name email alias} title content}}
		//
		// Here, the memberId and memberName are not used in the below method call: these parameters are not sent to the
		// GraphQL server
		return queryType.topics(topicAuthorPostAuthorRequest, boardName, "sinceParam", since);
	}

	@Override
	public List<Topic> findTopics(String boardName, List<String> keyword)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// findTopicIdDateTitleContentRequest has been create with this query string:
		// {id date title content}
		return queryType.findTopics(findTopicIdDateTitleContentRequest, boardName, keyword);
	}

	@Override
	public Board createBoard(String name, boolean publiclyAvailable)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// createBoardRequest has been create with this query string:
		// (empty) which means that all scalars are returned
		return mutationType.createBoard(createBoardRequest, name, publiclyAvailable);
	}

	@Override
	public Topic createTopic(TopicInput input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// createTopicRequest has been create with this query string:
		// (empty) which means that all scalars are returned
		return mutationType.createTopic(createTopicRequest, input);
	}

	@Override
	public Member createMember(MemberInput input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// createMember(input: &member) {id name alias email type}
		return mutationType.createMember(createMemberRequest, input);
	}

	@Override
	public Post createPost(PostInput input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// createPostRequest has been create with this query string:
		// "{id date author{id} title content publiclyAvailable}"
		return mutationType.createPost(createPostRequest, input);
	}

	@Override
	public List<Post> createPosts(List<PostInput> input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// createPostsRequest has been create with this query string:
		// {id date author{id} title content publiclyAvailable}
		return mutationType.createPosts(createPostsRequest, input);
	}
}