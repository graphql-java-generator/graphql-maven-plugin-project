/**
 * 
 */
package graphql.mavenplugin.generation;

import lombok.Data;

/**
 * This class describers one field of one objet type (or interface...). It aims to be simple enough, so that the
 * Velocity template can easily generated ths fields from it.<BR/>
 * For instance:
 * 
 * <PRE>
 * name: String!
 * </PRE>
 * 
 * or
 * 
 * <PRE>
 * appearsIn: [Episode!]!
 * </PRE>
 * 
 * @author EtienneSF
 */
@Data
public class Field {

	/** The name of the field */
	private String name;

	/**
	 * The type of this field. This type is either the type of the field (if it's not a list), or the type of the items
	 * in the list (if it's a list)
	 */
	private FieldType type;

	/** Is this field a list? */
	private boolean list = false;

	/**
	 * Is this field mandatory? If this field is a list, then mandatory indicates whether the list itself is mandatory,
	 * or may be nullable
	 */
	private boolean mandatory = false;

	/** Indicates whether the item in the list are not nullable, or not. Only used if this field is a list. */
	private boolean itemMandatory = false;

}
