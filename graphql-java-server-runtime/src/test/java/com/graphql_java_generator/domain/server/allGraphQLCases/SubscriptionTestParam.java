/** Generated by the default template from graphql-java-generator */
package com.graphql_java_generator.domain.server.allGraphQLCases;

import java.util.HashMap;
import java.util.Map;


import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLScalar;
import java.util.List;

/**
 *
 * @author generated by graphql-java-generator
 * @see <a href="https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInputType("SubscriptionTestParam")
@SuppressWarnings("unused")
public class SubscriptionTestParam 
{


	public SubscriptionTestParam(){
		// No action
	}

	@GraphQLScalar(fieldName = "errorOnSubscription", graphQLTypeSimpleName = "Boolean", javaClass = java.lang.Boolean.class)
	java.lang.Boolean errorOnSubscription;


	@GraphQLScalar(fieldName = "errorOnNext", graphQLTypeSimpleName = "Boolean", javaClass = java.lang.Boolean.class)
	java.lang.Boolean errorOnNext;


	@GraphQLScalar(fieldName = "completeAfterFirstNotification", graphQLTypeSimpleName = "Boolean", javaClass = java.lang.Boolean.class)
	java.lang.Boolean completeAfterFirstNotification;


	@GraphQLScalar(fieldName = "closeWebSocketBeforeFirstNotification", graphQLTypeSimpleName = "Boolean", javaClass = java.lang.Boolean.class)
	java.lang.Boolean closeWebSocketBeforeFirstNotification;


	@GraphQLScalar(fieldName = "messages", graphQLTypeSimpleName = "String", javaClass = java.lang.String.class)
	List<java.lang.String> messages;



	public void setErrorOnSubscription(java.lang.Boolean errorOnSubscription) {
		this.errorOnSubscription = errorOnSubscription;
	}

	public java.lang.Boolean getErrorOnSubscription() {
		return errorOnSubscription;
	}
		

	public void setErrorOnNext(java.lang.Boolean errorOnNext) {
		this.errorOnNext = errorOnNext;
	}

	public java.lang.Boolean getErrorOnNext() {
		return errorOnNext;
	}
		

	public void setCompleteAfterFirstNotification(java.lang.Boolean completeAfterFirstNotification) {
		this.completeAfterFirstNotification = completeAfterFirstNotification;
	}

	public java.lang.Boolean getCompleteAfterFirstNotification() {
		return completeAfterFirstNotification;
	}
		

	public void setCloseWebSocketBeforeFirstNotification(java.lang.Boolean closeWebSocketBeforeFirstNotification) {
		this.closeWebSocketBeforeFirstNotification = closeWebSocketBeforeFirstNotification;
	}

	public java.lang.Boolean getCloseWebSocketBeforeFirstNotification() {
		return closeWebSocketBeforeFirstNotification;
	}
		

	public void setMessages(List<java.lang.String> messages) {
		this.messages = messages;
	}

	public List<java.lang.String> getMessages() {
		return messages;
	}
		

     public String toString() {
        return "SubscriptionTestParam {"
				+ "errorOnSubscription: " + errorOnSubscription
				+ ", "
				+ "errorOnNext: " + errorOnNext
				+ ", "
				+ "completeAfterFirstNotification: " + completeAfterFirstNotification
				+ ", "
				+ "closeWebSocketBeforeFirstNotification: " + closeWebSocketBeforeFirstNotification
				+ ", "
				+ "messages: " + messages
        		+ "}";
    }

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
	 * {@link #builder()}
	 */
	public static class Builder {
		private java.lang.Boolean errorOnSubscription;
		private java.lang.Boolean errorOnNext;
		private java.lang.Boolean completeAfterFirstNotification;
		private java.lang.Boolean closeWebSocketBeforeFirstNotification;
		private List<java.lang.String> messages;

		public Builder withErrorOnSubscription(java.lang.Boolean errorOnSubscription) {
			this.errorOnSubscription = errorOnSubscription;
			return this;
		}
		public Builder withErrorOnNext(java.lang.Boolean errorOnNext) {
			this.errorOnNext = errorOnNext;
			return this;
		}
		public Builder withCompleteAfterFirstNotification(java.lang.Boolean completeAfterFirstNotification) {
			this.completeAfterFirstNotification = completeAfterFirstNotification;
			return this;
		}
		public Builder withCloseWebSocketBeforeFirstNotification(java.lang.Boolean closeWebSocketBeforeFirstNotification) {
			this.closeWebSocketBeforeFirstNotification = closeWebSocketBeforeFirstNotification;
			return this;
		}
		public Builder withMessages(List<java.lang.String> messages) {
			this.messages = messages;
			return this;
		}

		public SubscriptionTestParam build() {
			SubscriptionTestParam _object = new SubscriptionTestParam();
			_object.setErrorOnSubscription(errorOnSubscription);
			_object.setErrorOnNext(errorOnNext);
			_object.setCompleteAfterFirstNotification(completeAfterFirstNotification);
			_object.setCloseWebSocketBeforeFirstNotification(closeWebSocketBeforeFirstNotification);
			_object.setMessages(messages);
			return _object;
		}
	}
}
