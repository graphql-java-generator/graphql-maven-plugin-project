module com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_client_runtime {
	//
	requires transitive com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_common_runtime;
	requires transitive tools.jackson.core;
	requires transitive tools.jackson.databind;
	requires transitive jakarta.annotation;
	requires transitive spring.beans;
	requires transitive spring.boot;
	requires transitive spring.boot.autoconfigure;
	requires transitive spring.context;
	// requires transitive spring.jcl;
	requires transitive spring.security.oauth2.client;
	requires transitive spring.web;
	requires transitive spring.webflux;

	requires org.apache.commons.text;
	requires org.apache.commons.lang3;
	requires org.reactivestreams;
	requires reactor.core;
	requires spring.graphql;

	// Allow reflection on the client runtime
	opens com.graphql_java_generator.client to tools.jackson.databind, spring.beans, spring.core, spring.context;
	opens com.graphql_java_generator.client.directive to spring.beans, spring.core, spring.context;
	opens com.graphql_java_generator.client.response to tools.jackson.databind;
	opens com.graphql_java_generator.client.graphqlrepository to spring.beans, spring.core, spring.context;

	exports com.graphql_java_generator.client;
	exports com.graphql_java_generator.client.directive;
	exports com.graphql_java_generator.client.request;
	exports com.graphql_java_generator.client.response;
	exports com.graphql_java_generator.client.graphqlrepository;
	exports com.graphql_java_generator.exception;
}