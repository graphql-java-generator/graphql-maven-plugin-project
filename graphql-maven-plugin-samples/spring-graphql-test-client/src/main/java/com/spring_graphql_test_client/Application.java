package com.spring_graphql_test_client;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.forum.generated.Board;
import org.forum.generated.Mutation;
import org.forum.generated.Post;
import org.forum.generated.PostInput;
import org.forum.generated.Topic;
import org.forum.generated.TopicPostInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * This class contains the functional code that is executed. It uses the GraphQLXxx Spring beans for that. It is started
 * by Spring, once the application context is initialized, as it implements {@link CommandLineRunner}.<br/>
 * It demonstrates how to define and execute queries, with <a href=
 * "https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/client_graphql_repository">GraphQL
 * Repositories</a>. GraphQL Repositories are available since release 1.17
 * 
 * @author etienne-sf
 */
@Component // This annotation marks this class as a Spring bean (prerequisite to make @Autowire annotation work)
public class Application implements CommandLineRunner {

	/** The logger for this class */
	static protected Logger logger = LoggerFactory.getLogger(Application.class);

	@Autowired
	private MyGraphQLRepository myGraphQLRepository;

	@Override
	public void run(String... args) throws GraphQLRequestExecutionException {
		logger.info("===========================================================================================");
		logger.info("====================  Executing Partial Requests  =========================================");
		logger.info("===========================================================================================");
		List<Board> boards = myGraphQLRepository.boards();
		logger.info("Boards read: {}", boards);

		logger.info("===========================================================================================");
		logger.info("====================  Executing Partial Requests, with parameters  ========================");
		logger.info("===========================================================================================");
		List<Topic> topics = myGraphQLRepository.topics("Board name 2");
		logger.info("Topics read: {}", topics);

		logger.info("===========================================================================================");
		logger.info("====================  Executing Partial Requests, with parameters and bind parameters =====");
		logger.info("===========================================================================================");
		String memberId = null; // may be null, as it's optional
		String memberName = null; // may be null, as it's optional
		Date since = new Calendar.Builder().setDate(2022, 02 - 1 /* february */, 01).build().getTime();
		List<Topic> topicsSince = myGraphQLRepository.topicsSince("Board name 2", memberId, memberName, since);
		logger.info("Topics read: {}", topicsSince);

		logger.info("===========================================================================================");
		logger.info("==================== Executing direct full request, with a fragment =====================");
		logger.info("===========================================================================================");
		boards = myGraphQLRepository.fullQueryWithFragment().getBoards();
		logger.info("Boards read: {}", boards);

		logger.info("===========================================================================================");
		logger.info("====================  Executing Partial Requests, with inline Fragment ====================");
		logger.info("===========================================================================================");
		List<Board> boardsWithInlineFragment = myGraphQLRepository.boardsWithInlineFragment();
		logger.info("Boards read: {}", boardsWithInlineFragment);

		TopicPostInput topicInput = new TopicPostInput.Builder().withAuthorId("1").withPubliclyAvailable(true)
				.withDate(new GregorianCalendar(2019, 4 - 1, 30).getTime()).withTitle("a title")
				.withContent("Some content").build();
		PostInput postInput = new PostInput.Builder().withFrom(new GregorianCalendar(2018, 3 - 1, 2).getTime())
				.withInput(topicInput).withTopicId("2").build();

		logger.info("===========================================================================================");
		logger.info("==================== Executing mutation in a Partial Request ==============================");
		logger.info("===========================================================================================");
		Post postFromPartialRequest = myGraphQLRepository.createPost(postInput);
		logger.info("Post created: {}", postFromPartialRequest);

		logger.info("===========================================================================================");
		logger.info("==================== Executing mutation in a Full Request =================================");
		logger.info("===========================================================================================");
		Mutation mutation = myGraphQLRepository.createPostFullRequest(postInput);
		Post postFromFullRequest = mutation.getCreatePost();
		logger.info("Post created: {}", postFromFullRequest);

		logger.info("Normal end of execution");
	}

}
