package org.allGraphQLCases.subscription.graphqlrepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.allGraphQLCases.SpringTestConfig;
import org.allGraphQLCases.client.CEP_EnumWithReservedJavaKeywordAsValues_CES;
import org.allGraphQLCases.subscription.SubscriptionCallbackGeneric;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

//Adding "webEnvironment = SpringBootTest.WebEnvironment.NONE" avoid this error:
//"No qualifying bean of type 'ReactiveClientRegistrationRepository' available"
//More details here: https://stackoverflow.com/questions/62558552/error-when-using-enablewebfluxsecurity-in-springboot
@SpringBootTest(classes = SpringTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Execution(ExecutionMode.CONCURRENT)
public class GraphQLRepositorySubscriptionIT {

	@Autowired
	SubscriptionGraphQLRepository graphQLRepo;

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_SubscribeToAList() throws GraphQLRequestExecutionException, InterruptedException {
		// Preparation
		SubscriptionCallbackGeneric<List<Integer>> callback = new SubscriptionCallbackGeneric<>(
				"FullRequestSubscriptionIT.test_SubscribeToAList");

		// Go, go, go
		SubscriptionClient sub = this.graphQLRepo.subscribeToAList(callback);

		// Verification
		// Let's wait a max of 20 second, until we receive some notifications
		callback.latchForMessageReception.await(20, TimeUnit.SECONDS);

		// Let's disconnect from the subscription
		sub.unsubscribe();

		// We should have received a notification from the subscription
		assertNull(callback.lastExceptionReceived);
		assertNotNull(callback.lastReceivedMessage);
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_PartialRequest_SubscribeToANullEnum() throws GraphQLRequestExecutionException, InterruptedException {
		// Preparation
		SubscriptionCallbackGeneric<CEP_EnumWithReservedJavaKeywordAsValues_CES> callback = new SubscriptionCallbackGeneric<>(
				"test_PartialRequest_SubscribeToANullEnum");

		// Go, go, go
		SubscriptionClient sub = this.graphQLRepo//
				.returnEnum(callback);

		// Verification
		// Let's wait a max of 20 second, until we receive some notifications
		callback.latchForMessageReception.await(20, TimeUnit.SECONDS);

		// Let's disconnect from the subscription
		sub.unsubscribe();

		// We should have received a notification from the subscription
		assertNull(callback.lastExceptionReceived);
		assertNull(callback.lastReceivedMessage, "This subscription only sends null values");
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_PartialRequest_SubscribeToAMandatoryEnum() throws GraphQLRequestExecutionException, InterruptedException {
		// Preparation
		SubscriptionCallbackGeneric<CEP_EnumWithReservedJavaKeywordAsValues_CES> callback = new SubscriptionCallbackGeneric<>(
				"test_PartialRequest_SubscribeToAMandatoryEnum");

		// Go, go, go
		SubscriptionClient sub = this.graphQLRepo//
				.returnMandatoryEnum(callback, CEP_EnumWithReservedJavaKeywordAsValues_CES._volatile);

		// Verification
		// Let's wait a max of 20 second, until we receive some notifications
		callback.latchForMessageReception.await(20, TimeUnit.SECONDS);

		// Let's disconnect from the subscription
		sub.unsubscribe();

		// We should have received a notification from the subscription
		assertNull(callback.lastExceptionReceived);
		assertEquals(CEP_EnumWithReservedJavaKeywordAsValues_CES._volatile, callback.lastReceivedMessage);
	}
}
