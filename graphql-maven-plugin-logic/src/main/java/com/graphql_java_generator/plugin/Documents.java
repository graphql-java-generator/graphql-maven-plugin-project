/**
 * 
 */
package com.graphql_java_generator.plugin;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;

import graphql.language.Document;

/**
 * This class is a Spring component that just embed the documents attribute, which is a list of {@link Document}. This
 * allows to create a spring component with a class annotated by the {@link Component} spring annotation.
 * 
 * @author etienne-sf
 */
public interface Documents {

	/**
	 * The list of GraphQL documents (schemas) that have been found and parsed by the GraphQL engine. This is the AST
	 * that is the then used by the plugin to handle the GraphQL schema(s) content.
	 * 
	 * @throws IOException
	 */
	List<Document> getDocuments() throws IOException;
}
