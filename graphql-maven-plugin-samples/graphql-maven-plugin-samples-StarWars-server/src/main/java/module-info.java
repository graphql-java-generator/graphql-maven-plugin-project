module com.graphql_java_generator.graphql_maven_plugin_project.graphql_maven_plugin_samples_StarWars_server {

	// /////////////////////////////////////////////////////////////
	// Modules needed for the server generated code (start), as the "copyRuntimeSources" plugin parameter is true
	requires tools.jackson.databind;
	requires com.graphqljava;
	requires org.reactivestreams;
	requires org.slf4j;
	requires jakarta.persistence; // Only if the "generateJPAAnnotation" plugin parameter is set to true
	requires org.dataloader;
	requires spring.beans;
	requires spring.boot;
	requires spring.core;
	requires spring.data.jpa; // Only if the "generateJPAAnnotation" plugin parameter is set to true
	requires spring.web;
	requires spring.boot.autoconfigure;
	requires spring.graphql;
	requires spring.context;
	requires reactor.core;
	// Modules needed for the server generated code (end)
	// /////////////////////////////////////////////////////////////

	// Dependencies for the code that is specific to this project
	requires jakarta.annotation;
	requires jakarta.transaction;
	requires io.reactivex.rxjava3;
	requires spring.data.commons;
	requires spring.boot.graphql;
	requires spring.boot.persistence;

}