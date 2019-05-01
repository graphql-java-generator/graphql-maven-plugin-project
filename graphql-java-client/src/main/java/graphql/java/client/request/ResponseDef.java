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
import graphql.java.client.QueryExecutor;

/**
 * This class describes the fields and sub-objects that the response from the GraphQL server should contain, for one
 * GraphQL type. The structure is recursive: a {@link ResponseDef} itsled contains one or more {@link ResponseDef}, to
 * describe the Sub-object(s) that should be returned.<BR/>
 * A {@link ResponseDef} can not be created directly. You must use an {@link ResponseDefBuilder} to create a
 * {@link ResponseDef}. This Builder allows to easily add fields or sub objects. And it validate for each the GraphQL
 * schema is respected.
 * 
 * @author EtienneSF
 */
public class ResponseDef {

	/** Logger for this class */
	private static Logger logger = LogManager.getLogger();

	static GraphqlUtils graphqlUtils = new GraphqlUtils();

	/** This class is a Builder that'll help to define what should appear in the response from the GraphQL server */
	public static class ResponseDefBuilder {
		final ResponseDef responseDef;

		public ResponseDefBuilder(String graphqlObjectName) {
			responseDef = new ResponseDef(graphqlObjectName);
		}

		public ResponseDefBuilder withField(String fieldName) {
			return withField(fieldName, null);
		}

		public ResponseDefBuilder withField(String fieldName, String alias) {
			Field field = new Field(fieldName, alias);
			responseDef.fields.add(field);
			return this;
		}

		public ResponseDefBuilder withSubObject(String fieldName, ResponseDef responseDef) {
			return withSubObject(fieldName, null, responseDef);
		}

		public ResponseDefBuilder withSubObject(String fieldName, String fieldAlias, ResponseDef responseDef) {
			responseDef.setFieldName(fieldName);
			responseDef.setFieldAlias(fieldAlias);
			this.responseDef.subObjects.add(responseDef);
			return this;
		}

		public ResponseDef build() {
			return responseDef;
		}
	}

	/**
	 * This class represents an attribute of a GraphQL Object, that should appear in the response from the GraphQL
	 * server.
	 */
	static class Field {
		final String name;
		final String alias;

		Field(String name, String alias) {
			graphqlUtils.checkName(name);
			if (alias != null) {
				graphqlUtils.checkName(alias);
			}
			this.name = name;
			this.alias = alias;
		}
	}

	Marker marker = QueryExecutor.GRAPHQL_MARKER;

	/**
	 * Indicates the GraphQL object name for which this object lists the field and sub-objects that should be returned
	 * by the GraphQL server
	 */
	final String graphqlObjectName;

	/** Indicates the fieldName within the parent objet, if this object is a subobject. Null otherwise */
	String fieldName;
	/** Indicates the alias under which this GraphQl object should be returned by the GraphQL server */
	String fieldAlias;

	/** The list of fields that the GraphQL server should return for this GraphQL object */
	List<Field> fields = new ArrayList<>();

	/**
	 * The list of direct sub-ojects that the GraphQL server should return for this GraphQL object, in the form of the
	 * list of what response is expected for each. This is recursive, so of course, this sub-object may also have their
	 * own sub-objects
	 */
	List<ResponseDef> subObjects = new ArrayList<>();

	/**
	 * A {@link ResponseDef} can only be created through the {@link ResponseDefBuilder} created for it. See the
	 * {@link #newResponseDeBuilder(String)} to create such a builder.
	 * 
	 * @param name
	 * @see #newResponseDeBuilder(String)
	 */
	ResponseDef(String graphqlObjectName) {
		this.graphqlObjectName = graphqlObjectName;
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
	public ResponseDef addSubObjectResponseDef(String graphqlObjectName, String fieldName) {
		return addSubObjectResponseDefWithAlias(graphqlObjectName, fieldName, null);
	}

	/**
	 * Add a field (which is itself a GraphQL type) to the response for the current GraphQL type.
	 * 
	 * @param fieldName
	 *            The field name to add, as defined in the GraphQL schema
	 * @param fieldAlias
	 *            The alias for this field
	 */
	public ResponseDef addSubObjectResponseDefWithAlias(String graphqlObjectName, String fieldName, String fieldAlias) {
		logger.warn(marker, "No check that {} is really a non scalar field", fieldName);
		ResponseDef responseDef = new ResponseDef(graphqlObjectName);
		responseDef.setFieldName(fieldName);
		responseDef.setFieldAlias(fieldAlias);
		subObjects.add(responseDef);
		return responseDef;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		graphqlUtils.checkName(fieldName);
		this.fieldName = fieldName;
	}

	public String getFieldAlias() {
		return fieldAlias;
	}

	public void setFieldAlias(String fieldAlias) {
		if (fieldAlias != null) {
			graphqlUtils.checkName(fieldAlias);
		}
		this.fieldAlias = fieldAlias;
	}

	/**
	 * Contruct a new {@link ResponseDefBuilder}
	 * 
	 * @param name
	 * @return
	 */
	public static ResponseDefBuilder newResponseDeBuilder(String graphqlObjectName) {
		return new ResponseDefBuilder(graphqlObjectName);
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

		// We first loop through the field of the current ResponseDef
		fields.stream().forEach(f -> appendFieldName(sb, f.name, f.alias));

		// Then we loop though all sub-objects
		for (ResponseDef o : subObjects) {
			appendFieldName(sb, o.fieldName, o.fieldAlias);
			// Let's add all queried fields for this object
			o.appendResponseQuery(sb);
		} // for

		sb.append("}");
	}

	/**
	 * Append one field (or object) name and optional alias to the given {@link StringBuilder}.
	 * 
	 * @param sb
	 * @param f
	 */
	void appendFieldName(StringBuilder sb, String name, String alias) {
		sb.append(" ");

		// If we've an alias, let's write it
		if (alias != null) {
			sb.append(alias).append(": ");
		}

		sb.append(name);
	}

}
