package com.graphql_java_generator.samples.forum.client.graphql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.graphql_java_generator.samples.forum.SpringTestConfig;

/**
 * As it is suffixed by "IT", this is an integration test. Thus, it allows us to start the GraphQL Forum server, see
 * the pom.xml file for details.
 * 
 * @author etienne-sf
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SpringTestConfig.class })
@TestPropertySource("classpath:application.properties")
@Execution(ExecutionMode.CONCURRENT)
class PartialPreparedRequestsDeprecatedIT extends AbstractIT {

	@BeforeEach
	void setUp() throws Exception {
		queries = new PartialPreparedRequestsDeprecated();
	}

}
