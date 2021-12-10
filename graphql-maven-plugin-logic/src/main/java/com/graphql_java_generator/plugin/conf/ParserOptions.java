/**
 * 
 */
package com.graphql_java_generator.plugin.conf;

import lombok.Data;

/**
 * List of options, that can be set into the {@link graphql.parser.ParserOptions} parser options, for graphql-java.
 * 
 * @author etienne-sf
 */
@Data
public class ParserOptions {

	/** Maximum number of tokens that may be read from a GraphQL schema. */
	private Integer maxTokens = null;

}
