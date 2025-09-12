/**
 * @See https://docs.spring.io/spring-security/reference/modules.html
 */
module com.graphql_java_generator.graphql_maven_plugin_project.graphql_maven_plugin_samples_allGraphQLCases_server {

	// /////////////////////////////////////////////////////////////
	// Modules needed for the server generated code (start)

	// requires jakarta.persistence; // Only if the "generateJPAAnnotation" plugin parameter is set to true
	requires org.dataloader;
	// requires spring.data.jpa; // Only if the "generateJPAAnnotation" plugin parameter is set to true
	// requires spring.data.jpa; // Only if the "generateJPAAnnotation" plugin parameter is set to true

	// The graphql_java_server_runtime is needed only when the "copyRuntimeSources" plugin parameter is false
	requires com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_server_runtime;

	// Below are the java modules needed when the "copyRuntimeSources" plugin parameter is true
	// requires com.fasterxml.jackson.databind;
	// requires com.graphqljava;
	// requires org.reactivestreams;
	// requires spring.web;

	// Modules needed for the server generated code (end)
	// /////////////////////////////////////////////////////////////

	// Modules needed for the rest of the server code
	requires com.graphqljava.extendedscalars;
	requires dozer.core;
	requires jakarta.annotation;
	requires org.jspecify;
	requires spring.security.config;
	requires spring.security.core;
	requires spring.security.oauth2.jose;
	requires spring.security.oauth2.resource.server;
	requires spring.security.web;
	requires spring.web;
}