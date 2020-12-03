package com.graphql_java_generator.samples.forum.client.graphql;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.Queries;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Board;
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
@Component
public class PartialPreparedRequestsDeprecated implements Queries {

	final static String GRAPHQL_ENDPOINT_URL = "http://localhost:8180/graphql";

	QueryType queryType = new QueryType(GRAPHQL_ENDPOINT_URL);
	MutationType mutationType = new MutationType(GRAPHQL_ENDPOINT_URL);

	// Below are the ObjectResponses, that are created at initialization time.
	ObjectResponse boardsSimpleResponse;
	ObjectResponse boardsAndTopicsResponse;
	ObjectResponse topicAuthorPostAuthorResponse;
	ObjectResponse findTopicIdDateTitleContentResponse;
	ObjectResponse createBoardResponse;
	ObjectResponse createTopicResponse;
	ObjectResponse createMemberResponse;
	ObjectResponse createPostResponse;
	ObjectResponse createPostsResponse;

	public PartialPreparedRequestsDeprecated() throws GraphQLRequestPreparationException {
		// No field specified: all scalar fields of the root type will be queried
		boardsSimpleResponse = queryType.getBoardsResponseBuilder().build();

		boardsAndTopicsResponse = queryType.getBoardsResponseBuilder()
				.withQueryResponseDef("{id name publiclyAvailable topics(since:?since){id}}").build();

		topicAuthorPostAuthorResponse = queryType.getTopicsResponseBuilder()
				.withQueryResponseDef("{id date author{name email alias id type} nbPosts title content " //
						+ "posts(memberId:?memberId, memberName: ?memberName, since: &sinceParam){id date author{name email alias} title content}}")
				.build();

		findTopicIdDateTitleContentResponse = queryType.getFindTopicsResponseBuilder()
				.withQueryResponseDef(" {id date title content} ").build();

		// No field defined, so all scalar fields are returned
		createBoardResponse = mutationType.getCreateBoardResponseBuilder().build();

		// No field defined, so all scalar fields are returned
		createTopicResponse = mutationType.getCreateTopicResponseBuilder().build();

		// createMember(input: &member) {id name alias email type}
		createMemberResponse = mutationType.getCreateMemberResponseBuilder()
				.withQueryResponseDef("{id name alias email type}").build();

		// "{id date author{id} title content publiclyAvailable}"
		createPostResponse = mutationType.getCreatePostResponseBuilder()
				.withQueryResponseDef("{id date author{id} title content publiclyAvailable}").build();

		// "{id date author{id} title content publiclyAvailable}"
		createPostsResponse = mutationType.getCreatePostsResponseBuilder()
				.withQueryResponseDef("{id date author{id} title content publiclyAvailable}").build();
	}

	@Override
	public List<Board> boardsSimple() throws GraphQLRequestExecutionException {
		// boardsAndTopicsResponse has been create with this query string:
		// (empty) which means that all scalars are returned
		return queryType.boards(boardsSimpleResponse);
	}

	@Override
	public List<Board> boardsAndTopicsWithFieldParameter(Date since)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// boardsAndTopicsResponse has been create with this query string:
		// {id name publiclyAvailable topics(since:?since){id}}
		return queryType.boards(boardsAndTopicsResponse, "since", since);
	}

	@Override
	public List<Topic> topicAuthorPostAuthor(String boardName, Date since) throws GraphQLRequestExecutionException {
		// topicAuthorPostAuthorResponse has been create with this query string:
		// {id date author{name email alias id type} nbPosts title content posts(memberId:?memberId, memberName:
		// ?memberName, since: &sinceParam){id date author{name email alias} title content}}
		//
		// Here, the memberId and memberName are not used in the below method call: these parameters are not sent to the
		// GraphQL server
		return queryType.topics(topicAuthorPostAuthorResponse, boardName, "sinceParam", since);
	}

	@Override
	public List<Topic> findTopics(String boardName, List<String> keyword)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// findTopicIdDateTitleContentResponse has been create with this query string:
		// {id date title content}
		return queryType.findTopics(findTopicIdDateTitleContentResponse, boardName, keyword);
	}

	@Override
	public Board createBoard(String name, boolean publiclyAvailable)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// createBoardResponse has been create with this query string:
		// (empty) which means that all scalars are returned
		return mutationType.createBoard(createBoardResponse, name, publiclyAvailable);
	}

	@Override
	public Topic createTopic(TopicInput input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// createTopicResponse has been create with this query string:
		// (empty) which means that all scalars are returned
		return mutationType.createTopic(createTopicResponse, input);
	}

	@Override
	public Member createMember(MemberInput input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// createMember(input: &member) {id name alias email type}
		return mutationType.createMember(createMemberResponse, input);
	}

	@Override
	public Post createPost(PostInput input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// createPostResponse has been create with this query string:
		// "{id date author{id} title content publiclyAvailable}"
		return mutationType.createPost(createPostResponse, input);
	}

	@Override
	public List<Post> createPosts(List<PostInput> input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// createPostsRequest has been create with this query string:
		// {id date author{id} title content publiclyAvailable}
		return mutationType.createPosts(createPostsResponse, input);
	}

}