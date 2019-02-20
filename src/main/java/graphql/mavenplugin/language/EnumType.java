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
public class EnumType {

	/** The name for this enum */
	String name;

	/** The list of values */
	List<String> values = new ArrayList<String>();

}
