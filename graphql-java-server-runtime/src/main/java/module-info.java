module com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_server_runtime {

	exports com.graphql_java_generator.server.util;

	requires transitive org.reactivestreams;
	requires transitive org.slf4j;
	requires transitive com.fasterxml.jackson.databind;
	requires transitive com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_common_runtime;
	requires transitive reactor.core;
	requires transitive spring.beans;
	requires transitive spring.boot.autoconfigure;
	requires transitive spring.boot;
	requires transitive spring.context;
	requires transitive spring.core;
	requires transitive spring.graphql;

}