/**
 * 
 */
package graphql.mavenplugin.language.impl;

import java.util.List;

import graphql.mavenplugin.DocumentParser;
import graphql.mavenplugin.PluginMode;
import graphql.mavenplugin.language.Field;
import graphql.mavenplugin.language.Relation;
import graphql.mavenplugin.language.Type;
import lombok.Data;

/**
 * @author EtienneSF
 */
@Data
public class FieldImpl implements Field {

	/**
	 * The {@link DocumentParser} instance, which will allow to get the {@link Type} of the field, from its typeName,
	 * after the whole parsing is finished
	 */
	final DocumentParser documentParser;

	/** The name of the field */
	private String name;

	/** The {@link Type} which contains this field */
	private Type owningType;

	/**
	 * The type of this field. This type is either the type of the field (if it's not a list), or the type of the items
	 * in the list (if it's a list)
	 */
	private String typeName;

	/**
	 * Indicates whether this field is an id or not. It's used in {@link PluginMode#SERVER} mode to add the
	 * javax.persistence annotations for the id fields. Default value is false. This field is set to true for GraphQL
	 * fields which are of 'ID' type.
	 */
	private boolean id = false;

	/** All fields in an object may have parameters. A parameter is actually a field. */
	private List<Field> inputParameters = null;

	/** Is this field a list? */
	private boolean list = false;

	/**
	 * Is this field mandatory? If this field is a list, then mandatory indicates whether the list itself is mandatory,
	 * or may be nullable
	 */
	private boolean mandatory = false;

	/** Indicates whether the item in the list are not nullable, or not. Only used if this field is a list. */
	private boolean itemMandatory = false;

	/** Contains the default value.. Only used if this field is a list. */
	private String defaultValue = null;

	/** Contans the description of the relation that this field holds */
	private Relation relation = null;

	/**
	 * Tha Java annotationto add to this type, ready to be added by the Velocity template. That is: one annotation per
	 * line, each line starting at the beginning of the line
	 */
	private String annotation;

	/**
	 * To construct such a class, you need ro provide the current DocumentParser
	 * 
	 * @param documentParser
	 */
	public FieldImpl(DocumentParser documentParser) {
		this.documentParser = documentParser;
	}

	/** {@inheritDoc} */
	@Override
	public Type getType() {
		Type type = documentParser.getType(typeName);
		if (type == null) {
			throw new NullPointerException("Could not find any Type of name '" + typeName + "'");
		}
		return type;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
		if (typeName.equals("ID")) {
			setId(true);
		}
	}

}
