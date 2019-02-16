/**
 * 
 */
package graphql.mavenplugin.language;

import java.util.List;

import lombok.Data;

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
public class ObjectType {

	/** The name of the object type */
	private String name;

	/** The fields for this object type */
	private List<Field> fields;
}
