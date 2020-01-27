package com.graphql_java_generator.samples.forum.client.graphql;

import java.util.Date;
import java.util.List;

import com.graphql_java_generator.client.request.Builder;
import com.graphql_java_generator.client.request.InputParameter;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.client.response.GraphQLRequestExecutionException;
import com.graphql_java_generator.client.response.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.Main;
import com.graphql_java_generator.samples.forum.client.Queries;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Board;
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
 * @author EtienneSF
 */
public class WithBuilder implements Queries {

	QueryType queryType = new QueryType(Main.GRAPHQL_ENDPOINT_URL);
	MutationType mutationType = new MutationType(Main.GRAPHQL_ENDPOINT_URL);
	ObjectResponse boardsSimpleResponse;
	ObjectResponse boardsAndTopicsResponse;
	ObjectResponse topicAuthorPostAuthorResponse;
	ObjectResponse findTopicIdDateTitleContent;
	ObjectResponse createBoardResponse;
	ObjectResponse createTopicResponse;
	ObjectResponse createPostResponse;
	ObjectResponse createPostsResponse;

	public WithBuilder() throws GraphQLRequestPreparationException {
		// No field specified: all known scalar fields of the root type will be queried
		boardsSimpleResponse = queryType.getBoardsResponseBuilder().build();

		boardsAndTopicsResponse = queryType.getBoardsResponseBuilder().withField("id").withField("name")
				.withField("publiclyAvailable").withSubObject(new Builder(Board.class, "topics")
						.withInputParameter(InputParameter.newBindParameter("since", "since", true)).build())
				.build();

		// {id date author{name email alias id type} nbPosts title content posts{id date author{name email alias} title
		// content}}
		ObjectResponse author1 = new Builder(Topic.class, "author").withField("name").withField("email")
				.withField("alias").withField("id").withField("type").build();
		ObjectResponse author2 = new Builder(Post.class, "author").withField("name").withField("email")
				.withField("alias").build();
		ObjectResponse posts = new Builder(Topic.class, "posts")
				.withInputParameter(InputParameter.newBindParameter("since", "since", true)).withField("id")
				.withField("date").withSubObject(author2).withField("title").withField("content").build();
		topicAuthorPostAuthorResponse = queryType.getTopicsResponseBuilder().withField("id").withField("date")
				.withSubObject(author1).withField("nbPosts").withSubObject(posts).withField("title")
				.withField("content").build();

		// findTopics: {id date title content}
		findTopicIdDateTitleContent = queryType.getFindTopicsResponseBuilder().withField("id").withField("date")
				.withField("title").withField("content").build();

		// No field defined, so all field are returned
		createBoardResponse = mutationType.getCreateBoardResponseBuilder().build();
		// No field defined, so all field are returned
		createTopicResponse = mutationType.getCreateTopicResponseBuilder().build();
		// No field defined, so all field are returned
		createPostResponse = mutationType.getCreatePostResponseBuilder().build();
		// No field defined, so all field are returned
		createPostsResponse = mutationType.getCreatePostsResponseBuilder().build();
	}

	@Override
	public List<Board> boardsSimple() throws GraphQLRequestExecutionException {
		return queryType.boards(boardsSimpleResponse);
	}

	@Override
	public List<Board> boardsAndTopicsWithFieldParameter(Date since)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.boards(boardsAndTopicsResponse, "since", dateFormat.format(since));
	}

	@Override
	public List<Topic> topicAuthorPostAuthor(String boardName, Date since) throws GraphQLRequestExecutionException {
		return queryType.topics(topicAuthorPostAuthorResponse, "Board name 2", "since", dateFormat.format(since));
	}

	@Override
	public List<Topic> findTopics(String boardName, List<String> keyword)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.findTopics(findTopicIdDateTitleContent, boardName, keyword);
	}

	@Override
	public Board createBoard(String name, boolean publiclyAvailable)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationType.createBoard(createBoardResponse, name, publiclyAvailable);
	}

	@Override
	public Topic createTopic(TopicInput input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationType.createTopic(createTopicResponse, input);
	}

	@Override
	public Post createPost(PostInput input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationType.createPost(createPostResponse, input);
	}

	@Override
	public List<Post> createPosts(List<PostInput> input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationType.createPosts(createPostsResponse, input);
	}
}