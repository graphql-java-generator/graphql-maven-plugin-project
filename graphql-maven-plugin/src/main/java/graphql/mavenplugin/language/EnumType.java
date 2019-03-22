/**
 * 
 */
package graphql.mavenplugin.language;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * This class is the image for a graphql Enum
 * 
 * @author EtienneSF
 */
@Data
public class EnumType implements Type {

	/** The name for this enum */
	String name;

	/** The name of the package for this class */
	private String packageName;

	/** The list of values */
	List<String> values = new ArrayList<String>();

	/**
	 * 
	 * @param packageName
	 *            The package name for this enum class
	 */
	public EnumType(String packageName) {
		this.packageName = packageName;
	}

	@Override
	public String getClassSimpleName() {
		return name;
	}

	@Override
	public String getConcreteClassSimpleName() {
		return getClassSimpleName();
	}

	@Override
	public String getClassFullName() {
		return packageName + "." + getClassSimpleName();
	}

}
