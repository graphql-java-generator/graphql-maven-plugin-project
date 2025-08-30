module com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_common_runtime {

	requires spring.context;
	requires spring.beans;
	requires spring.core;
	requires transitive com.graphqljava;
	requires org.slf4j;

	exports com.graphql_java_generator.annotation;
	exports com.graphql_java_generator.customscalars;
	exports com.graphql_java_generator.util;
}