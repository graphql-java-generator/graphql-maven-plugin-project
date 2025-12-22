module com.graphql_java_generator.graphql_maven_plugin_project.graphql_maven_plugin_samples_StarWars_client {

	// ////////////////////////////////////////////////////////////////////////////////
	// [Start] Dependencies for the generated code (as the "copyRuntimeSources" plugin parameter is true)
	requires com.fasterxml.jackson.annotation;
	requires tools.jackson.databind;
	requires com.graphqljava;
	requires org.apache.commons.text;
	requires org.apache.commons.lang3;
	requires org.reactivestreams;
	requires org.slf4j;
	requires reactor.core;
	requires reactor.netty.core;
	requires spring.beans;
	requires spring.boot;
	requires spring.boot.autoconfigure;
	requires spring.core;
	requires spring.graphql;
	requires spring.context;
	requires spring.security.oauth2.client;
	requires spring.web;
	requires spring.webflux;

	// opens directives for the runtime code
	opens com.graphql_java_generator.client
			to com.fasterxml.jackson.databind, spring.beans, spring.core, spring.context;
	opens com.graphql_java_generator.client.directive to spring.beans, spring.core, spring.context;
	opens com.graphql_java_generator.client.response to com.fasterxml.jackson.databind;
	opens com.graphql_java_generator.client.graphqlrepository to spring.beans, spring.core, spring.context;

	// opens directives for the generative code
	opens com.generated.graphql to com.fasterxml.jackson.databind, spring.beans, spring.context, spring.core,
			com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_client_runtime,
			com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_common_runtime;
	opens com.generated.graphql.util to com.fasterxml.jackson.databind, spring.beans, spring.context, spring.core,
			com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_client_runtime,
			com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_common_runtime;
	opens com.generated.graphql_spring_autoconfiguration
			to com.fasterxml.jackson.databind, spring.beans, spring.context, spring.core;

	// [End] Dependencies for the generated code
	// ////////////////////////////////////////////////////////////////////////////////

	// Other dependencies for the project
	requires reactor.netty.http;
	requires io.netty.handler;
	requires jakarta.annotation;

	opens com.graphql_java_generator.samples.simple.client
			to com.fasterxml.jackson.databind, spring.beans, spring.context, spring.core,
			com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_client_runtime,
			com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_common_runtime;
	opens com.graphql_java_generator.samples.simple.client.graphql
			to com.fasterxml.jackson.databind, spring.beans, spring.context, spring.core,
			com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_client_runtime,
			com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_common_runtime;

}