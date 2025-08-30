module com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_server_runtime {

	exports com.graphql_java_generator.server.util;

	requires org.reactivestreams;
	requires spring.graphql;
	requires spring.context;
	requires transitive com.fasterxml.jackson.databind;
	requires transitive com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_common_runtime;
	requires reactor.core;

}