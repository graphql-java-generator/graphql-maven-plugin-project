/**
 * 
 */
package com.graphql_java_generator.client.request;

/**
 * This class contains a portion of GraphQL request. It's typically the GraphQL part for the definition of a parameter,
 * like in the <I>human</I> and <I>anArray</I> parameters in the GraphQL request below:
 * 
 * <PRE>
 * 	mutation {createHuman (human:  {name: "a name with two { { and a } ", friends: [], appearsIn: [JEDI,NEWHOPE], type: "a type" } )
 * &#64;testDirective(value:&value, anotherValue:?anotherValue, anArray  : [  "a string that contains [ [ and ] that should be ignored" ,  "another string" ] ,
 * anObject:{name: "a name" , [{name="subname"}],type:"type"}) 
 * {id name appearsIn friends {id name}}}
 * </PRE>
 * 
 * It's used to store parameters value that are given in their GraphQL form, when declaring queries. It must be of a
 * type that is not a {@link String}, for the
 * {@link InputParameter#getValueForGraphqlQuery(Object, graphql.schema.GraphQLScalarType)} to work. In this method,
 * double quotes are added to limit the values of {@link String}s.
 * 
 * @author etienne-sf
 */
public class RawGraphQLString {

	private final String str;

	public RawGraphQLString(String str) {
		this.str = str;
	}

	@Override
	public String toString() {
		return str;
	}

}
