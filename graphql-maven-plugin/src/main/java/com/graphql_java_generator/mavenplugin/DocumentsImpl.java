/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.plugin.MojoExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.plugin.Documents;
import com.graphql_java_generator.plugin.ResourceSchemaStringProvider;

import graphql.language.Document;
import graphql.parser.Parser;

/**
 * @author etienne-sf
 */
@Component
public class DocumentsImpl implements Documents {

	@Autowired
	ResourceSchemaStringProvider schemaStringProvider;

	private List<Document> documents = null;

	/**
	 * Loads the schema from the graphqls files. This method uses the GraphQLJavaToolsAutoConfiguration from the
	 * project, to load the schema from the graphqls files
	 * 
	 * @param schemaStringProvider
	 *            The String Provider
	 * @return the {@link Document}s to read
	 * @throws MojoExecutionException
	 *             When an error occurs while reading or parsing the graphql definition files
	 */
	@Override
	public List<Document> getDocuments() throws IOException {
		if (documents == null) {
			// It's not yet initialized. Let's load and parse the GraphQL schemas
			Parser parser = new Parser();
			documents = schemaStringProvider.schemaStrings().stream().map(parser::parseDocument)
					.collect(Collectors.toList());
		}
		return documents;
	}
}
