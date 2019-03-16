package com.graphql_java_client.examples.graphql_java_client.examples.hello_world.graphql_java_gen.multiple;

/**
* 
*/
public enum Episode {
	EMPIRE,

	JEDI,

	NEWHOPE,

	UNKNOWN_VALUE;

	public static Episode fromGraphQl(String value) {
		if (value == null) {
			return null;
		}

		switch (value) {
		case "EMPIRE": {
			return EMPIRE;
		}

		case "JEDI": {
			return JEDI;
		}

		case "NEWHOPE": {
			return NEWHOPE;
		}

		default: {
			return UNKNOWN_VALUE;
		}
		}
	}

	public String toString() {
		switch (this) {
		case EMPIRE: {
			return "EMPIRE";
		}

		case JEDI: {
			return "JEDI";
		}

		case NEWHOPE: {
			return "NEWHOPE";
		}

		default: {
			return "";
		}
		}
	}
}