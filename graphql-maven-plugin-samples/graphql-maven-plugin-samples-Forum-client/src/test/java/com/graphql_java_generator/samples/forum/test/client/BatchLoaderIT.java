package com.graphql_java_generator.samples.forum.test.client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.DirectQueriesWithFieldInputParameters;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Member;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Post;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Topic;
import com.graphql_java_generator.samples.forum.test.SpringTestConfig;

/**
 * Some samples (and tests) with direct queries having input parameters
 * 
 * @author etienne-sf
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SpringTestConfig.class })
@TestPropertySource("classpath:application.properties")
@Execution(ExecutionMode.CONCURRENT)
public class BatchLoaderIT {

	@Autowired
	DirectQueriesWithFieldInputParameters directQueriesWithFieldInputParameters;

	String boardName;
	Date since;

	@BeforeEach
	void beforeEach() {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, 9, 20);// Month is 0-based, so this date is 2018, october the 20th
		since = cal.getTime();

		boardName = "Board name 2";
	}

	@Test
	public void test_authorLoadedByBatchLoader()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		// Go, go, go
		List<Topic> topics = directQueriesWithFieldInputParameters.topics_since(boardName, since);

		assertTrue(topics.size() >= 5);
		for (Topic t : topics) {
			Member m = t.getAuthor();
			assertNotNull(m);
			checkNameLoadedByBatchLoader(m.getName());

			for (Post p : t.getPosts()) {
				assertNotNull(p.getAuthor());
				checkNameLoadedByBatchLoader(p.getAuthor().getName());
			}
		}
	}

	/**
	 * Verifications: all the author names should be retrieved by the BatchLoader implemented in the
	 * graphql-maven-plugin-samples-Forum-server project. So their name should start by one [BL] (not two, and no
	 * [SL])<BR/>
	 */
	private void checkNameLoadedByBatchLoader(String name) {
		assertTrue(name.startsWith("[BL] "));
		assertFalse(name.startsWith("[BL] [BL] "));
		assertFalse(name.startsWith("[SL] ")); // Ok, this one is quite useless! But it makes me satisfied... ;-)
	}
}
