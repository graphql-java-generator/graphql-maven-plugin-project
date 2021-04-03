/**
 * 
 */
package com.graphql_java_generator.client.request;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * This class is responsible to split the GraphQL query into meaningful tokens: the spaces, EOL (...) are removed and
 * the (, @, { (...) are sent as token. It also allows to check the comming tokens. For instance, when reading a field
 * name, it's possible to check if the next token is :, which means that the current token s not the field name, but the
 * field alias.
 * 
 * @author etienne-sf
 *
 */
public class QueryTokenizer {

	/**
	 * The list of character that can separate tokens, and that has no meaning. By default, these tokens are removed
	 * from the result, and won't be visible in the returned tokens.
	 * 
	 * @see #nextToken(boolean)
	 */
	public static final String EMPTY_DELIMITERS = " \n\r\t";
	public static final String MEANINGFUL_DELIMITERS = "{}[]!,:()@\"";

	/**
	 * The list of tokens are stored into this lists, which allows to get information from the coming tokens, without
	 * getting them out of the tokenizer
	 */
	final List<String> tokens;

	/** Index of the next token that is to be read */
	int index = 0;

	/**
	 * Create a tokenizer for the given GraphQL query
	 * 
	 * @param graphQLRequest
	 * @throws NullPointerException
	 *             If graphQLRequest is null
	 */
	public QueryTokenizer(String graphQLRequest) {
		// We still use this old StringTokenizer, to get the delimiters returned as token
		StringTokenizer st = new StringTokenizer((graphQLRequest == null) ? "" : graphQLRequest,
				EMPTY_DELIMITERS + MEANINGFUL_DELIMITERS, true);
		tokens = new ArrayList<>(st.countTokens());
		while (st.hasMoreTokens()) {
			tokens.add(st.nextToken());
		}
	}

	/**
	 * Indicates if there are next non empty tokens in the list.
	 * 
	 * @return true if there are real token or meaningful delimiters left to read
	 */
	public boolean hasMoreTokens() {
		return hasMoreTokens(false);
	}

	/**
	 * Indicates if there are next non empty tokens in the list.
	 * 
	 * @param returnEmptyDelimiters
	 *            If true, all token are sent, which means that every character found in the source String are sent as
	 *            token. Each separator is sent one character per one character.<BR/>
	 *            If false the characters that are in the {@link #EMPTY_DELIMITERS} are not sent.
	 * @return true if there are real token or meaningful delimiters left to read
	 */
	public boolean hasMoreTokens(boolean returnEmptyDelimiters) {
		for (int i = index; i < tokens.size(); i += 1) {
			if (!EMPTY_DELIMITERS.contains(tokens.get(i))) {
				// We've found a real token coming.
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the next token. The characters that exist in the {@link #MEANINGFUL_DELIMITERS} are sent one character by
	 * one character. The characters that exist in the {@link #EMPTY_DELIMITERS} are not sent.
	 * 
	 * @return
	 */
	public String nextToken() {
		return nextToken(false);
	}

	/**
	 * Returns the next token, which may or may not be an empty one, depending on returnEmptyDelimiters
	 * 
	 * @param returnEmptyDelimiters
	 *            If true, all token are sent, which means that every character found in the source String are sent as
	 *            token. Each separator is sent one character per one character.<BR/>
	 *            If false the characters that are in the {@link #EMPTY_DELIMITERS} are not sent.
	 * @return
	 */
	public String nextToken(boolean returnEmptyDelimiters) {
		while (true) {
			if (index >= tokens.size()) {
				throw new RuntimeException("No more token where found");
			}

			// Let's read and check the next token in the list
			String token = tokens.get(index++);
			if (returnEmptyDelimiters) {
				// We return the token, whatever it contains
				return token;
			} else if (!EMPTY_DELIMITERS.contains(token)) {
				// Ok, we've found a non empty token
				return token;
			}
		}
	}

	/**
	 * Checks if the next meaningful token is the expected string that is given. The index is not updated, which means
	 * that if this method returns true, the next returned token will be the expected value.
	 * 
	 * @param expected
	 * @return
	 */
	public boolean checkNextToken(String expected) {
		for (int i = index; i < tokens.size(); i += 1) {
			if (!EMPTY_DELIMITERS.contains(tokens.get(i))) {
				// We've found a real token coming. Is it equals to the expected one?
				return tokens.get(i).equals(expected);
			}
			// It was an empty delimiter. Let's iterate once more.
		}
		// We found only empty delimiters.
		return false;
	}

	/**
	 * Checks if the next meaningful token starts by the expected string that is given. The index is not updated, which
	 * means that if this method returns true, the next returned token will be the token that begins by the expected
	 * string.
	 * 
	 * @param expectedStart
	 * @return
	 */
	public boolean checkNextTokenStartsWith(String expectedStart) {
		for (int i = index; i < tokens.size(); i += 1) {
			if (!EMPTY_DELIMITERS.contains(tokens.get(i))) {
				// We've found a real token coming. Is it equals to the expected one?
				return tokens.get(i).startsWith(expectedStart);
			}
			// It was an empty delimiter. Let's iterate once more.
		}
		// We found only empty delimiters.
		return false;
	}

	/**
	 * Reads the next real token, that is the next token that is not a separator
	 * 
	 * @param expected
	 *            If expected is not null, this method will check that the real token read is equal to this expected
	 *            value
	 * @param action
	 *            The action for which the real token is needed (use to get some context in a the exception message, if
	 *            any). The exception message will be: <I>"error occurs while " + action</I>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public String readNextRealToken(String expected, String action) throws GraphQLRequestPreparationException {

		while (hasMoreTokens()) {
			String token = nextToken(false);

			// We found a non null token
			if (expected != null && !expected.equals(token))
				throw new GraphQLRequestPreparationException("The token read is '" + token
						+ "', but the expected one is '" + expected + "' while " + action);
			// Ok, we're done
			return token;
		}

		throw new GraphQLRequestPreparationException("End of string found while " + action);
	}

}
