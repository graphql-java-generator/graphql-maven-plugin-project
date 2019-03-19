/**
 * 
 */
package graphql.java.client.domain;

/**
 * Contains the return of the GraphQlServer.<BR/>
 * Typically, the request
 * 
 * <PRE>
 * {hero(episode: NEWHOPE) {id name appearsIn friends{name}}}
 * </PRE>
 * 
 * will return:
 * 
 * <PRE>
 * {
 *  "data": {
 *    "hero": {
 *      "id": "An id",
 *      "name": "A hero's name",
 *      "appearsIn": [
 *        "NEWHOPE",
 *        "JEDI"
 *      ],
 *      "friends": null
 *    }
 *  }
 *}
 * </PRE>
 * 
 * The class {@link Data} is the root class to which is mapped the GraphQl server response.
 * 
 * @author EtienneSF
 */
public class Data {

	public Character hero;

	public Human human;

	public Droid droid;

}
