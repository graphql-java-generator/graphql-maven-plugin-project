/**
 * 
 */
package graphql.mavenplugin.language.impl;

import java.util.ArrayList;
import java.util.List;

import graphql.mavenplugin.PluginMode;
import graphql.mavenplugin.language.Field;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * This class describes one object type, as found in a graphql schema. It aims to be simple enough, so that the Velocity
 * template can easily generated ths fields from it.<BR/>
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

	/** List of the names of the ObjetType, that are implemented by this object */
	private List<String> implementz = new ArrayList<>();

	/** The fields for this object type */
	@ToString.Exclude
	private List<Field> fields = new ArrayList<>();

	/***
	 * One of null, "query", "mutation" or "subscription". This is used for queries, mutations and subscriptions as the
	 * string to put in the JSON query toward the GraphQL server
	 */
	String requestType;

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

}
