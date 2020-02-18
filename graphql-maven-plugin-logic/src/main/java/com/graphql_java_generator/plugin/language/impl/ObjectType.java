/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import java.util.ArrayList;
import java.util.List;

import com.graphql_java_generator.plugin.PluginMode;
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
 * @author EtienneSF
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
	 * If null: this object is defined in the GraphQL schema(s).<BR/>
	 * If not null : this Object is not defined in the given GraphQL schema(s). Instead it has been added as a default
	 * implementation for this interface. Such an object is mandatory for the client mode (when returning instances of
	 * this Interface), and can be used in the server mode.
	 */
	private InterfaceType defaultImplementationForInterface = null;

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
	 * @param mode
	 *            The current {@link PluginMode}
	 */
	public ObjectType(String name, String packageName, PluginMode mode) {
		super(packageName, mode, GraphQlType.OBJECT);
		setName(name);
	}

	/**
	 * @param packageName
	 *            the package name where it must be created
	 * @param mode
	 *            The current {@link PluginMode}
	 */
	public ObjectType(String packageName, PluginMode mode) {
		super(packageName, mode, GraphQlType.OBJECT);
	}

	/**
	 * This constructor is especially intended for subclasses, like {@link InterfaceType}
	 * 
	 * @param packageName
	 * @param mode
	 * @param type
	 */
	protected ObjectType(String name, String packageName, PluginMode mode, GraphQlType type) {
		super(packageName, mode, type);
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
}
