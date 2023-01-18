/**
 * 
 */
package com.graphql_java_generator.plugin.language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class matches the description of a GraphQL item, as described in the GraphQL schema. It allows several
 * capabilities for use in the template, for code or schema generation
 * 
 * @author etienne-sf
 */
public class Description {

	/** The description as it has been read from the source GraphQL schema */
	final graphql.language.Description description;

	/** The decomposition of the description in lines, without the EOL character(s) */
	List<String> lines = null;

	public Description(graphql.language.Description description2) {
		this.description = description2;
	}

	/**
	 * Returns the content of the description
	 * 
	 * @return
	 */
	public String getContent() {
		return description.getContent();
	}

	public boolean isMultiLine() {
		return description.isMultiLine();
	}

	/**
	 * Returns an array of the lines of this description. This array contains at least one item.
	 * 
	 * @return
	 */
	public List<String> getLines() {
		if (lines == null) {
			if (description.isMultiLine()) {
				try (BufferedReader sr = new BufferedReader(new StringReader(description.getContent()))) {
					lines = sr.lines().collect(Collectors.toList());
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			} else {
				lines = new ArrayList<String>(1);
				lines.add(description.getContent());
			}
		}
		return lines;
	}

}
