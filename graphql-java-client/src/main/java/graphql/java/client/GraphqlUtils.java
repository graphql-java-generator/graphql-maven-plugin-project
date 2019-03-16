/**
 * 
 */
package graphql.java.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author EtienneSF
 */
public class GraphqlUtils {

	Pattern graphqlNamePattern = Pattern.compile("^[_A-Za-z][_0-9A-Za-z]*$");

	/**
	 * Checks that the given graphql name is valid.
	 * 
	 * @param name
	 */
	public void checkName(String name) {
		Matcher m = graphqlNamePattern.matcher(name);
		if (!m.matches()) {
			throw new IllegalArgumentException("'" + name + "' is not a valid graphql name");
		}
	}

}
