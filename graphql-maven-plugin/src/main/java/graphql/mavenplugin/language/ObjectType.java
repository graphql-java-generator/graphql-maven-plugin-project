/**
 * 
 */
package graphql.mavenplugin.language;

import java.util.ArrayList;
import java.util.List;

import graphql.mavenplugin.PluginMode;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
	private List<Field> fields = new ArrayList<>();

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
	public ObjectType(String packageName, PluginMode mode, GraphQlType type) {
		super(packageName, mode, type);
	}

}
