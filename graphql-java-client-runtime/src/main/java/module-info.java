module com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_client_runtime {

	requires transitive com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_common_runtime;
	requires transitive com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;

	requires org.apache.commons.text;
	requires org.apache.commons.lang3;
	requires org.reactivestreams;
	requires org.slf4j;

	requires spring.beans;
	requires spring.boot;
	requires spring.boot.autoconfigure;
	requires spring.context;
	requires spring.core;
	requires spring.graphql;
	requires spring.security.oauth2.client;
	requires spring.web;
	requires spring.webflux;

	requires reactor.core;

	// Allow reflection on the client runtime
	opens com.graphql_java_generator.client to com.fasterxml.jackson.databind;
	opens com.graphql_java_generator.client.impl to spring.beans, spring.core, spring.context;
	opens com.graphql_java_generator.client.directive to spring.beans, spring.core, spring.context;
	opens com.graphql_java_generator.client.response to com.fasterxml.jackson.databind;
	opens com.graphql_java_generator.client.graphqlrepository to spring.beans, spring.core, spring.context;

	exports com.graphql_java_generator.client;
	exports com.graphql_java_generator.client.request;
	exports com.graphql_java_generator.client.response;
}