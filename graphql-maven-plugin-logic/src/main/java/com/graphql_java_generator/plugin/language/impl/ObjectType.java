/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import java.util.ArrayList;
import java.util.List;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.plugin.PluginConfiguration;
import com.graphql_java_generator.plugin.language.Field;

import lombok.Data;
import lombok.EqualsAndHashCode;
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
@Data
@EqualsAndHashCode(callSuper = true)
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
	 * @param packageName
	 *            the package name where it must be created
	 * @param pluginConfiguration
	 *            The current {@link PluginConfiguration}
	 */
	public ObjectType(String name, String packageName, PluginConfiguration pluginConfiguration) {
		super(packageName, pluginConfiguration, GraphQlType.OBJECT);
		setName(name);
	}

	/**
	 * @param packageName
	 *            the package name where it must be created
	 * @param pluginConfiguration
	 *            The current {@link PluginConfiguration}
	 */
	public ObjectType(String packageName, PluginConfiguration pluginConfiguration) {
		super(packageName, pluginConfiguration, GraphQlType.OBJECT);
	}

	/**
	 * This constructor is especially intended for subclasses, like {@link InterfaceType}
	 * 
	 * @param packageName
	 * @param pluginConfiguration
	 *            The current {@link PluginConfiguration}
	 * @param type
	 */
	protected ObjectType(String name, String packageName, PluginConfiguration pluginConfiguration, GraphQlType type) {
		super(packageName, pluginConfiguration, type);
		setName(name);
	}

	/** {@inheritDoc} */
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

	/** {@inheritDoc} */
	@Override
	public boolean isCustomScalar() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isScalar() {
		return false;
	}

	public String getRequestTypePascalCase() {
		return GraphqlUtils.graphqlUtils.getPascalCase(getRequestType());
	}
}
