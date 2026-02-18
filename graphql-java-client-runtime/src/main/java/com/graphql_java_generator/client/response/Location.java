package com.graphql_java_generator.client.response;

/**
 * @author etienne-sf
 */
public class Location {

	public int line;
	public int column;
	public String sourceName;

	public Location() {
		// Empty constructor
	}

	@Override
	public String toString() {
		return "line=" + line + ", column=" + column + (sourceName == null ? "" : " of " + sourceName);
	}

}
