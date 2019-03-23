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
public class ObjectType implements Type {

	/** The name of the object type */
	private String name;

	/** The name of the package for this class */
	private String packageName;

	/** List of the names of the ObjetType, that are implemented by this object */
	private List<String> implementz = new ArrayList<>();

	/** The fields for this object type */
	private List<Field> fields = new ArrayList<>();

	public ObjectType(String packageName) {
		this.packageName = packageName;
	}

	public GraphQlType getGraphQlType() {
		return GraphQlType.OBJECT;
	}

	/** {@inheritDoc} */
	@Override
	public String getClassSimpleName() {
		return name;
	}

	/** {@inheritDoc} */
	@Override
	public String getConcreteClassSimpleName() {
		return getClassSimpleName();
	}

	@Override
	public String getClassFullName() {
		return packageName + "." + getClassSimpleName();
	}
}
