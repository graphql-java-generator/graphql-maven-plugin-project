package com.graphql_java_generator.samples.forum.client.graphql;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.graphqlrepository.BindParameter;
import com.graphql_java_generator.client.graphqlrepository.GraphQLReactiveRepository;
import com.graphql_java_generator.client.graphqlrepository.PartialRequest;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Board;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Member;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.MemberInput;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Post;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.PostInput;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Topic;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.TopicInput;

import reactor.core.publisher.Mono;

@GraphQLReactiveRepository
public interface GraphQLReactiveRepositoryPartialRequests {

	@PartialRequest(requestName = "boards", request = "")
	Mono<Optional<List<Board>>> boardsSimple()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException;

	@PartialRequest(requestName = "boards", request = "{id name publiclyAvailable topics(since:?since){id}}")
	Mono<Optional<List<Board>>> boardsAndTopicsWithFieldParameter(@BindParameter(name = "since") Date since)
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException;

	@PartialRequest(requestName = "topics", request = "{id date author{name email alias id type} nbPosts title content " //
			+ "posts(memberId:?memberId, memberName: ?memberName, since: &sinceParam){id date author{name email alias} title content}}")
	Mono<Optional<List<Topic>>> topicAuthorPostAuthor(String boardName, //
			@BindParameter(name = "sinceParam") Date since
	// The other Bind Variables won't be fed
	) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException;

	@PartialRequest(request = " {id date title content} ")
	Mono<Optional<List<Topic>>> findTopics(String boardName, List<String> keyword)
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException;

	@PartialRequest(request = "", requestType = RequestType.mutation)
	Mono<Optional<Board>> createBoard(String name, boolean publiclyAvailable)
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException;

	@PartialRequest(request = "", requestType = RequestType.mutation)
	Mono<Optional<Topic>> createTopic(TopicInput topicInput)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	@PartialRequest(request = "{id name alias email type}", requestType = RequestType.mutation)
	public Mono<Optional<Member>> createMember(MemberInput input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	@PartialRequest(request = "{id date author{id} title content publiclyAvailable}", requestType = RequestType.mutation)
	public Mono<Optional<Post>> createPost(PostInput input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	@PartialRequest(request = "{id date author{id} title content publiclyAvailable}", requestType = RequestType.mutation)
	public Mono<Optional<List<Post>>> createPosts(List<PostInput> input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

}
