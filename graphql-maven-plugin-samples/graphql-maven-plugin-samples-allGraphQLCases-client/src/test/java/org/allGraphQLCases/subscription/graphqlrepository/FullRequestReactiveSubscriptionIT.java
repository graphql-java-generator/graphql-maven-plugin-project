package org.allGraphQLCases.subscription.graphqlrepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.concurrent.TimeUnit;

import org.allGraphQLCases.ReactiveQueriesIT.ReceivedFromSubscription;
import org.allGraphQLCases.SpringTestConfig;
import org.allGraphQLCases.client.CTP_TheSubscriptionType_CTS;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

import reactor.core.Disposable;

//Adding "webEnvironment = SpringBootTest.WebEnvironment.NONE" avoid this error:
//"No qualifying bean of type 'ReactiveClientRegistrationRepository' available"
//More details here: https://stackoverflow.com/questions/62558552/error-when-using-enablewebfluxsecurity-in-springboot
@SpringBootTest(classes = SpringTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Execution(ExecutionMode.CONCURRENT)
public class FullRequestReactiveSubscriptionIT {

	@Autowired
	FullRequestSubscriptionGraphQLReactiveRepository graphQLRepo;

	ReceivedFromSubscription<CTP_TheSubscriptionType_CTS> receivedFromFullQuery = new ReceivedFromSubscription<>();

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_SubscribeToAList() throws GraphQLRequestExecutionException, InterruptedException {
		// Preparation

		// Go, go, go
		Disposable d = this.graphQLRepo//
				.subscribeToAList()//
				.doOnEach(o -> {
					switch (o.getType()) {
					case ON_NEXT:
						this.receivedFromFullQuery.lastReceivedMessage = o.get();
						this.receivedFromFullQuery.hasReceveivedAMessage = true;
						this.receivedFromFullQuery.latchForMessageReception.countDown();
						break;
					case ON_ERROR:
						this.receivedFromFullQuery.lastReceivedError = o.getThrowable();
						this.receivedFromFullQuery.latchForMessageReception.countDown();
						break;
					default:
						// No action
					}
				})//
				.subscribe();

		// Verification

		// Let's wait a max of 20 seconds, until we receive a notification
		// (20s will never occur... unless using the debugger to debug some stuff)
		this.receivedFromFullQuery.latchForMessageReception.await(20, TimeUnit.SECONDS);
		// Let's release the used resources
		d.dispose();

		// We should have received no error
		assertNull(this.receivedFromFullQuery.lastReceivedError,
				(this.receivedFromFullQuery.lastReceivedError == null) ? ""
						: "Expected null, but is: " + this.receivedFromFullQuery.lastReceivedError.getClass().getName()
								+ ": " + this.receivedFromFullQuery.lastReceivedError.getMessage());
		// We should have received a notification from the subscription
		assertNotNull(this.receivedFromFullQuery.lastReceivedMessage);
		assertNotNull(this.receivedFromFullQuery.lastReceivedMessage.getSubscribeToAList());
	}

}
