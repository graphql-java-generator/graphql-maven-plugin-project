@SuppressWarnings({ "requires-automatic", "requires-transitive-automatic" }) //
module com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_common_runtime {

	requires transitive com.graphqljava;
	requires transitive org.slf4j;
	requires transitive spring.beans;
	requires transitive spring.context;
	requires transitive spring.core;

	exports com.graphql_java_generator.annotation;
	exports com.graphql_java_generator.customscalars;
	exports com.graphql_java_generator.util;
}