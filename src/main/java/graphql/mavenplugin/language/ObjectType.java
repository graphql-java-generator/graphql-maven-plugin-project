/**
 * 
 */
package graphql.mavenplugin.language;

import java.util.ArrayList;
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

	/** True if this object is a graphql interface. Default value is false, meaning: this objet is a regular object */
	boolean interfaceType = false;

	/** List of the names of the ObjetType, that are implemented by this object */
	private List<String> implementz = new ArrayList<>();

	/** The fields for this object type */
	private List<Field> fields = new ArrayList<>();;
}
