package graphql.java_client.examples.hello_world.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.java.client.ID;
import graphql.java.client.request.ResponseDefinition;

public class QueryType {

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(QueryType.class);

	public Character hero(Episode episode, ResponseDefinition query) {
		executeQuery()
	}

	public Human human(String id, ResponseDefinition query) {

	}

	public Droid droid(ID id, ResponseDefinition query) {
		logger.warn("id is not marked as mandatory in the java parameters");
		if (id == null)
			throw new NullPointerException("id is mandatory, for Query 'droid'");
	}
}
