/**
 * 
 */
package graphql.java.client.request;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;

import graphql.java.client.GraphqlUtils;

/**
 * This class describe the fields that the response must contain, for one GraphQL type. These fields may be scalars or
 * entity. Adding an entity will create a new {@link ResponseDefinition}, in which it is possible to define the field to
 * return for this entity.
 * 
 * @author EtienneSF
 */
public class ResponseDefinition {

	/** Logger for this class */
	private static Logger logger = LogManager.getLogger();

	GraphqlUtils graphqlUtils = new GraphqlUtils();

	class Field {
		final String name;
		final String alias;
		final ResponseDefinition responseDef;

		Field(String name, String alias) {
			this(name, alias, null);
		}

		Field(String name, String alias, ResponseDefinition responseDef) {
			graphqlUtils.checkName(name);
			if (alias != null) {
				graphqlUtils.checkName(alias);
			}
			this.name = name;
			this.alias = alias;
			this.responseDef = responseDef;
		}
	}

	final Marker marker;

	List<Field> fields = new ArrayList<>();

	/**
	 * Standard constructor
	 * 
	 * @param marker
	 *            A log4j2 marker,to group all GRAPHQL logs
	 */
	public ResponseDefinition(Marker marker) {
		this.marker = marker;
	}

	/**
	 * Add a scalar field to the response for the current GraphQL type.
	 * 
	 * @param fieldName
	 *            The field name to add, as defined in the GraphQL schema
	 * @throws IllegalArgumentException
	 *             When the fieldName is not valid
	 */
	public void addResponseField(String fieldName) {
		addResponseFieldWithAlias(fieldName, null);
	}

	/**
	 * Add a scalar field to the response for the current GraphQL type.
	 * 
	 * @param fieldName
	 *            The field name to add, as defined in the GraphQL schema
	 * @param alias
	 *            The alias for this field
	 */
	public void addResponseFieldWithAlias(String fieldName, String alias) {
		logger.warn(marker, "No check that {} is really a scalar field", fieldName);
		fields.add(new Field(fieldName, alias));
	}

	/**
	 * Add a field (which is itself a GraphQL type) to the response for the current GraphQL type.
	 * 
	 * @param fieldName
	 *            The field name to add, as defined in the GraphQL schema
	 */
	public ResponseDefinition addResponseEntity(String fieldName) {
		return addResponseEntityWithAlias(fieldName, null);
	}

	/**
	 * Add a field (which is itself a GraphQL type) to the response for the current GraphQL type.
	 * 
	 * @param fieldName
	 *            The field name to add, as defined in the GraphQL schema
	 * @param alias
	 *            The alias for this field
	 */
	public ResponseDefinition addResponseEntityWithAlias(String fieldName, String alias) {
		logger.warn(marker, "No check that {} is really a non scalar field", fieldName);
		ResponseDefinition responseDef = new ResponseDefinition(marker);
		fields.add(new Field(fieldName, alias, responseDef));
		return responseDef;
	}

	/**
	 * Retrieves the part of the query, which describes the fields that the GraphQL server should return.<BR/>
	 * For instance, for the query: <I>{hero(episode: NEWHOPE) {id name}}</I>, the response definition is <I>{id
	 * name}</I>
	 * 
	 * @param sb
	 *            The {@link StringBuilder} where the response must be appended
	 * 
	 * @return
	 */
	public void appendResponseQuery(StringBuilder sb) {
		sb.append("{");
		boolean appendSpace = false;
		for (Field f : fields) {
			if (appendSpace) {
				sb.append(" ");
			}
			appendSpace = true;

			// If we've an alias, let's write it
			if (f.alias != null) {
				sb.append(f.alias).append(": ");
			}

			sb.append(f.name);

			// If it's a graphql type, we need its fields
			if (f.responseDef != null) {
				f.responseDef.appendResponseQuery(sb);
			}
		} // for
		sb.append("}");
	}

}
