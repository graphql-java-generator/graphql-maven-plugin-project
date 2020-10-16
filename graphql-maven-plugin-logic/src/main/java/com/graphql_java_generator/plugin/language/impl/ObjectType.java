/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import java.util.ArrayList;
import java.util.List;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.plugin.conf.CommonConfiguration;
import com.graphql_java_generator.plugin.language.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This class describes one object type, as found in a graphql schema. It aims to be simple enough, so that the Velocity
 * template can easily generated the fields from it.<BR/>
 * An ObjectType would for instance describe:
 * 
 * <PRE>
 * type Character {
 *   name: String!
 *   appearsIn: [Episode!]!
 * }
 * </PRE>
 * 
 * @author etienne-sf
 */
@Getter
@Setter
// @Data
// @EqualsAndHashCode(callSuper = true)
public class ObjectType extends AbstractType {

	/** List of the names of GraphQL types (interface or enum or unions), that are implemented by this object */
	private List<String> implementz = new ArrayList<>();

	/** List of the names of unions, that are this object is member of */
	private List<UnionType> memberOfUnions = new ArrayList<>();

	/** The fields for this object type */
	@ToString.Exclude
	private List<Field> fields = new ArrayList<>();

	/**
	 * One of null, "query", "mutation" or "subscription". This is used for queries, mutations and subscriptions as the
	 * string to put in the JSON query toward the GraphQL server
	 */
	String requestType;

	/** Indicated whether this type is an InputObjectType or not. Default value is false */
	private boolean inputType = false;

	/**
	 * 
	 * @param name
	 *            The name of this object type
	 * @param configuration
	 *            The current plugin configuration, which is accessible through an interface that extends
	 *            {@link CommonConfiguration}
	 */
	public ObjectType(String name, CommonConfiguration configuration) {
		super(name, GraphQlType.OBJECT, configuration);
	}

	/**
	 * This constructor is especially intended for subclasses, like {@link InterfaceType}
	 * 
	 * @param name
	 *            the name for this type
	 * @param type
	 *            the kind of object
	 * @param configuration
	 *            The current plugin configuration, which is accessible through an interface that extends
	 *            {@link CommonConfiguration}
	 * @param type
	 */
	protected ObjectType(String name, GraphQlType type, CommonConfiguration configuration) {
		super(name, type, configuration);
	}

	@Override
	public String getPackageName() {
		return configuration.getPackageName();
	}

	@Override
	public Field getIdentifier() {
		List<Field> identifiers = new ArrayList<>();
		for (Field f : getFields()) {
			if (f.isId()) {
				identifiers.add(f);
			}
		}

		// Currently, only one identifier per Type is managed
		return identifiers.size() == 1 ? identifiers.get(0) : null;
	}

	@Override
	public boolean isCustomScalar() {
		return false;
	}

	@Override
	public boolean isScalar() {
		return false;
	}

	public String getRequestTypePascalCase() {
		return GraphqlUtils.graphqlUtils.getPascalCase(getRequestType());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean addSeparator;

		sb.append(getClass().getSimpleName() + " {name: ").append(getName());

		sb.append(", fields: {");
		addSeparator = false;
		for (Field f : getFields()) {
			sb.append(f.toString());
			if (addSeparator)
				sb.append(",");
			else
				addSeparator = true;
		}
		sb.append("}");

		if (getImplementz().size() > 0) {
			sb.append("implements ");
			sb.append(String.join(",", getImplementz()));
		}

		return sb.toString();
	}

}
