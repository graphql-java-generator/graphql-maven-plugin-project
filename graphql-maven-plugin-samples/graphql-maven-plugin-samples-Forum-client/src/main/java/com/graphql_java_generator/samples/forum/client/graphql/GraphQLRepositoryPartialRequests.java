package com.graphql_java_generator.samples.forum.client.graphql;

import java.util.Date;
import java.util.List;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.graphqlrepository.BindParameter;
import com.graphql_java_generator.client.graphqlrepository.GraphQLRepository;
import com.graphql_java_generator.client.graphqlrepository.PartialRequest;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.Queries;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Board;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Member;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.MemberInput;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Post;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.PostInput;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Topic;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.TopicInput;

@GraphQLRepository
public interface GraphQLRepositoryPartialRequests extends Queries {

	@Override
	@PartialRequest(requestName = "boards", request = "")
	List<Board> boardsSimple() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException;

	@Override
	@PartialRequest(requestName = "boards", request = "{id name publiclyAvailable topics(since:?since){id}}")
	List<Board> boardsAndTopicsWithFieldParameter(@BindParameter(name = "since") Date since)
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException;

	@Override
	@PartialRequest(requestName = "topics", request = "{id date author{name email alias id type} nbPosts title content " //
			+ "posts(memberId:?memberId, memberName: ?memberName, since: &sinceParam){id date author{name email alias} title content}}")
	List<Topic> topicAuthorPostAuthor(String boardName, //
			@BindParameter(name = "sinceParam") Date since
	// The other Bind Variables won't be fed
	) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException;

	@Override
	@PartialRequest(request = " {id date title content} ")
	List<Topic> findTopics(String boardName, List<String> keyword)
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException;

	@Override
	@PartialRequest(request = "", requestType = RequestType.mutation)
	Board createBoard(String name, boolean publiclyAvailable)
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException;

	@Override
	@PartialRequest(request = "", requestType = RequestType.mutation)
	Topic createTopic(TopicInput topicInput)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	@Override
	@PartialRequest(request = "{id name alias email type}", requestType = RequestType.mutation)
	public Member createMember(MemberInput input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	@Override
	@PartialRequest(request = "{id date author{id} title content publiclyAvailable}", requestType = RequestType.mutation)
	public Post createPost(PostInput input) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	@Override
	@PartialRequest(request = "{id date author{id} title content publiclyAvailable}", requestType = RequestType.mutation)
	public List<Post> createPosts(List<PostInput> input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

}
