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
public class ResponseDefinitionImpl implements ResponseDefinition {

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
	public ResponseDefinitionImpl(Marker marker) {
		this.marker = marker;
	}

	/** {@inheritDoc} */
	@Override
	public void addResponseField(String fieldName) {
		addResponseFieldWithAlias(fieldName, null);
	}

	/** {@inheritDoc} */
	@Override
	public void addResponseFieldWithAlias(String fieldName, String alias) {
		logger.warn(marker, "No check that {} is really a scalar field", fieldName);
		fields.add(new Field(fieldName, alias));
	}

	/** {@inheritDoc} */
	@Override
	public ResponseDefinition addResponseEntity(String fieldName) {
		return addResponseEntityWithAlias(fieldName, null);
	}

	/** {@inheritDoc} */
	@Override
	public ResponseDefinition addResponseEntityWithAlias(String fieldName, String alias) {
		logger.warn(marker, "No check that {} is really a non scalar field", fieldName);
		ResponseDefinition responseDef = new ResponseDefinitionImpl(marker);
		fields.add(new Field(fieldName, alias, responseDef));
		return responseDef;
	}

	/** {@inheritDoc} */
	@Override
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
