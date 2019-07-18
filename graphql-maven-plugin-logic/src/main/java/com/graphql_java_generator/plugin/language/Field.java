/**
 * 
 */
package com.graphql_java_generator.plugin.language;

import java.util.List;

import com.graphql_java_generator.plugin.PluginMode;
import com.graphql_java_generator.plugin.language.impl.TypeUtil;

/**
 * This interface describes one field of one objet type (or interface...). It aims to be simple enough, so that the
 * Velocity template can easily generated ths fields from it.<BR/>
 * For instance:
 * 
 * <PRE>
 * name: String!
 * </PRE>
 * 
 * is a {@link Field} of name 'name', type 'String', and is mandatory. or
 * 
 * <PRE>
 * appearsIn: [Episode!]!
 * </PRE>
 * 
 * is a {@link Field} of name 'appearsIn', type 'Episode', is a list, is mandatory and its items are mandatory.
 * 
 * @author EtienneSF
 */
public interface Field {

	/**
	 * The name of the field, as found in the GraphQL schema
	 * 
	 * @return The name of the field
	 */
	public String getName();

	/**
	 * Retrieves the {@link Type} which contains this field
	 * 
	 * @return
	 */
	public Type getOwningType();

	/**
	 * Retrieves the {@link Type} for this field
	 * 
	 * @return
	 */
	public Type getType();

	/**
	 * The type of this field. This type is either the type of the field (if it's not a list), or the type of the items
	 * in the list (if it's a list)
	 */
	public String getTypeName();

	/**
	 * Indicates whether this field is an id or not. It's used in {@link PluginMode#SERVER} mode to add the
	 * javax.persistence annotations for the id fields. Default value is false. This field is set to true for GraphQL
	 * fields which are of 'ID' type.
	 */
	public boolean isId();

	/** All fields in an object may have parameters. A parameter is actually a field. */
	public List<Field> getInputParameters();

	/** Is this field a list? */
	public boolean isList();

	/**
	 * Is this field mandatory? If this field is a list, then mandatory indicates whether the list itself is mandatory,
	 * or may be nullable
	 */
	public boolean isMandatory();

	/** Indicates whether the item in the list are not nullable, or not. Only used if this field is a list. */
	public boolean isItemMandatory();

	/** Contains the default value.. Only used if this field is an input parameter. */
	public String getDefaultValue();

	/**
	 * Returns the {@link Relation} description for this field.
	 * 
	 * @return null if this field is not a relation to another Entity
	 */
	public Relation getRelation();

	/**
	 * Retrieves the annotation or annotations to add to this field, when in server mode, to serve the relation that
	 * this field holds
	 * 
	 * @return The relevant annotation(s) ready to add directly as-is in the Velocity template, or "" (an empty string)
	 *         if there is no annotation to add. The return is never null.
	 */
	public String getAnnotation();

	/**
	 * Convert the given name, which is supposed to be in camel case (for instance: thisIsCamelCase) to a pascal case
	 * string (for instance: ThisIsCamelCase).
	 * 
	 * @return
	 */
	public default String getPascalCaseName() {
		return TypeUtil.getPascalCase(getName());
	}

	/**
	 * Convert the given name, which can be in non camel case (for instance: ThisIsCamelCase) to a pascal case string
	 * (for instance: thisIsCamelCase).
	 * 
	 * @return
	 */
	public default String getCamelCaseName() {
		return TypeUtil.getCamelCase(getName());
	}
}
