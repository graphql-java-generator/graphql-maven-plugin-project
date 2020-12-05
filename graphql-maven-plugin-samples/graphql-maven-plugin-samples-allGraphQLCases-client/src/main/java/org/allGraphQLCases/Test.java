package org.allGraphQLCases;

import java.util.List;

public class Test {

	public class Callback<T> {
		public void onMessage(T t) {
			// Do something useful with the message
		}
	}

	public <T> void test(Callback<T> subscriptionCallback, Class<T> messageType) {
		T t = null;// In the real app, this is a value mapped from an incoming JSON, in a separate thread
		subscriptionCallback.onMessage(t);
	}

	public void run() {
		Callback<Integer> callbackInteger = new Callback<>();
		Callback<List<Integer>> callbackListInteger = new Callback<>();

		// The next line is Ok
		test(callbackInteger, Integer.class);

		// The next line doesn't compile
		Object c1 = callbackListInteger;
		test((Callback<List>) c1, List.class);
	}

}
