module com.graphql_java_generator.graphql_maven_plugin_project.graphql_maven_plugin_samples_allGraphQLCases_client {

	// Dependency for the generated code
	requires com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_client_runtime;

	opens org.allGraphQLCases.client to com.fasterxml.jackson.databind, spring.beans, spring.context, spring.core,
			com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_client_runtime,
			com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_common_runtime;
	opens org.allGraphQLCases.client.util to com.fasterxml.jackson.databind, spring.beans, spring.context, spring.core,
			com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_client_runtime,
			com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_common_runtime;
	opens org.allGraphQLCases.client_spring_autoconfiguration
			to com.fasterxml.jackson.databind, spring.beans, spring.context, spring.core;
	opens org.allGraphQLCases.client2 to com.fasterxml.jackson.databind, spring.beans, spring.context, spring.core,
			com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_client_runtime,
			com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_common_runtime;
	opens org.allGraphQLCases.client2_spring_autoconfiguration to spring.beans, spring.context, spring.core;
	opens org.forum.client to com.fasterxml.jackson.databind, spring.beans, spring.context, spring.core,
			com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_client_runtime,
			com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_common_runtime;
	opens org.forum.client_spring_autoconfiguration to spring.beans, spring.context, spring.core;

	// Module required for the code specific to the project
	requires com.graphqljava.extendedscalars;
	requires spring.security.config;
	requires spring.security.web;
	requires spring.security.core;
	requires spring.graphql;

	opens org.allGraphQLCases.demo to spring.beans, spring.context, spring.core;
	opens org.allGraphQLCases.demo.impl to spring.beans, spring.context, spring.core;
	opens org.allGraphQLCases.demo.subscription to spring.beans, spring.context, spring.core;
	opens org.allGraphQLCases.minimal.oauth_app to spring.beans, spring.context, spring.core;
	opens org.allGraphQLCases.minimal.spring_app to spring.beans, spring.context, spring.core;

	// Dependency for the test code
	requires org.apache.commons.lang3;
	requires reactor.core;
	requires reactor.netty.http;

}