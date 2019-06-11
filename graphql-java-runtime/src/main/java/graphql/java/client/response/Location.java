/**
 * 
 */
package graphql.java.client.response;

/**
 * @author EtienneSF
 */
public class Location {

	public int line;
	public int column;
	public String sourceName;

	@Override
	public String toString() {
		return "line=" + line + ", column=" + column + (sourceName == null ? "" : " of " + sourceName);
	}

}
