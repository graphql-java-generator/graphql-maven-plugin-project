package com.graphql_java_generator.samples.forum.client.graphql;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.Queries;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Board;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.GraphQLRequest;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Member;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.MemberInput;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.MutationExecutor;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Post;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.PostInput;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.QueryExecutor;
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
public class PartialPreparedRequests implements Queries {

	@Autowired
	QueryExecutor queryTypeExecutor;
	@Autowired
	MutationExecutor mutationTypeExecutor;

	// Below are the GraphQLRequest, that are created at initialization time. They contain the prepared requests
	GraphQLRequest boardsSimpleRequest;
	GraphQLRequest boardsAndTopicsRequest;
	GraphQLRequest topicAuthorPostAuthorRequest;
	GraphQLRequest findTopicIdDateTitleContentRequest;
	GraphQLRequest createBoardRequest;
	GraphQLRequest createTopicRequest;
	GraphQLRequest createMemberRequest;
	GraphQLRequest createPostRequest;
	GraphQLRequest createPostsRequest;

	@PostConstruct
	public void init() throws GraphQLRequestPreparationException {

		// No field specified: all scalar fields of the root type will be queried
		boardsSimpleRequest = queryTypeExecutor.getBoardsGraphQLRequest(null);

		boardsAndTopicsRequest = queryTypeExecutor
				.getBoardsGraphQLRequest("{id name publiclyAvailable topics(since:?since){id}}");

		topicAuthorPostAuthorRequest = queryTypeExecutor
				.getTopicsGraphQLRequest("{id date author{name email alias id type} nbPosts title content " //
						+ "posts(memberId:?memberId, memberName: ?memberName, since: &sinceParam){id date author{name email alias} title content}}");

		findTopicIdDateTitleContentRequest = queryTypeExecutor.getFindTopicsGraphQLRequest(" {id date title content} ");

		// No field defined, so all scalar fields are returned
		createBoardRequest = mutationTypeExecutor.getCreateBoardGraphQLRequest(null);

		// No field defined, so all scalar fields are returned
		createTopicRequest = mutationTypeExecutor.getCreateTopicGraphQLRequest(null);

		// {id name alias email type}
		createMemberRequest = mutationTypeExecutor.getCreateMemberGraphQLRequest("{id name alias email type}");

		// "{id date author{id} title content publiclyAvailable}"
		createPostRequest = mutationTypeExecutor
				.getCreatePostGraphQLRequest("{id date author{id} title content publiclyAvailable}");

		// "{id date author{id} title content publiclyAvailable}"
		createPostsRequest = mutationTypeExecutor
				.getCreatePostsGraphQLRequest("{id date author{id} title content publiclyAvailable}");
	}

	@Override
	public List<Board> boardsSimple() throws GraphQLRequestExecutionException {
		// boardsAndTopicsRequest has been create with this query string:
		// (empty) which means that all scalars are returned
		return queryTypeExecutor.boards(boardsSimpleRequest);
	}

	@Override
	public List<Board> boardsAndTopicsWithFieldParameter(Date since)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// boardsAndTopicsRequest has been create with this query string:
		// {id name publiclyAvailable topics(since:?since){id}}
		return queryTypeExecutor.boards(boardsAndTopicsRequest, "since", since);
	}

	@Override
	public List<Topic> topicAuthorPostAuthor(String boardName, Date since) throws GraphQLRequestExecutionException {
		// topicAuthorPostAuthorRequest has been create with this query string:
		// {id date author{name email alias id type} nbPosts title content posts(memberId:?memberId, memberName:
		// ?memberName, since: &sinceParam){id date author{name email alias} title content}}
		//
		// Here, the memberId and memberName are not used in the below method call: these parameters are not sent to the
		// GraphQL server
		return queryTypeExecutor.topics(topicAuthorPostAuthorRequest, boardName, "sinceParam", since);
	}

	@Override
	public List<Topic> findTopics(String boardName, List<String> keyword)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// findTopicIdDateTitleContentRequest has been create with this query string:
		// {id date title content}
		return queryTypeExecutor.findTopics(findTopicIdDateTitleContentRequest, boardName, keyword);
	}

	@Override
	public Board createBoard(String name, boolean publiclyAvailable)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// createBoardRequest has been create with this query string:
		// (empty) which means that all scalars are returned
		return mutationTypeExecutor.createBoard(createBoardRequest, name, publiclyAvailable);
	}

	@Override
	public Topic createTopic(TopicInput input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// createTopicRequest has been create with this query string:
		// (empty) which means that all scalars are returned
		return mutationTypeExecutor.createTopic(createTopicRequest, input);
	}

	@Override
	public Member createMember(MemberInput input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// createMember(input: &member) {id name alias email type}
		return mutationTypeExecutor.createMember(createMemberRequest, input);
	}

	@Override
	public Post createPost(PostInput input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// createPostRequest has been create with this query string:
		// "{id date author{id} title content publiclyAvailable}"
		return mutationTypeExecutor.createPost(createPostRequest, input);
	}

	@Override
	public List<Post> createPosts(List<PostInput> input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// createPostsRequest has been create with this query string:
		// {id date author{id} title content publiclyAvailable}
		return mutationTypeExecutor.createPosts(createPostsRequest, input);
	}
}