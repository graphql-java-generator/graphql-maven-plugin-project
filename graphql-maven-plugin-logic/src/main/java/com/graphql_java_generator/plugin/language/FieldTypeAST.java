/**
 * 
 */
package com.graphql_java_generator.plugin.language;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * This class is a transcription of the GraphQL type of a field, as it has been read in the
 * <A HREF="https://en.wikipedia.org/wiki/Abstract_syntax_tree">AST</A>. When browsing through the GraphQL
 * <A HREF="https://en.wikipedia.org/wiki/Abstract_syntax_tree">AST</A>, the type may not already have been parsed. This
 * can occur when a field F1 of the type T1 refers to the T2 type, while the T2 type contains a F2 field, that is of the
 * T1 type.<BR/>
 * So, when reading the <A HREF="https://en.wikipedia.org/wiki/Abstract_syntax_tree">AST</A>, we only store the type
 * name, if it's mandatory and its structure (if it's a list, a list of list...). When generating the code, the whole
 * <A HREF="https://en.wikipedia.org/wiki/Abstract_syntax_tree">AST</A> has been read. At this moment, it's possible to
 * access to the instance of the {@link Type} for this type, and read all its properties (java class...)
 * 
 * @author etienne-sf
 */
@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class FieldTypeAST {

	/**
	 * The type of this field, as defined in the GraphQL schema. This type is either the type of the field (if it's not
	 * a list), or the type of the items in the list (if it's a list)
	 */
	private String graphQLTypeSimpleName;

	/**
	 * The depth of the list for this input parameter: 0 if this field is not a list. And 2, for instance, if the
	 * field's type is "[[Int]]"
	 */
	@Builder.Default // Allows the default value to be used with the Lombok @Builder annotation on the class
	int listDepth = 0;

	/**
	 * Is this field mandatory? If this field is a list, then mandatory indicates whether the list itself is mandatory,
	 * or may be nullable. For item of the list, you'll have to call {@link #getListItemFieldTypeAST()}, to get the item
	 * list's properties.<BR/>
	 * By default, it's not a list
	 */
	@Builder.Default // Allows the default value to be used with the Lombok @Builder annotation on the class
	boolean mandatory = false;

	/** Only used if this type is a list: it indicates if the items for this list are mandatory? */
	@Builder.Default // Allows the default value to be used with the Lombok @Builder annotation on the class
	boolean itemMandatory = false;

	/**
	 * When this field type is a list, returns the properties for the item of this list. This can also be a list, when
	 * the GraphQL type is defined by nested arrays.
	 * 
	 * @return
	 * @throws RuntimeException
	 *             When the current field type is not a list
	 */
	@Builder.Default // Allows the default value to be used with the Lombok @Builder annotation on the class
	FieldTypeAST listItemFieldTypeAST = null;

	/** The default constructor */
	public FieldTypeAST() {
	}

	/** The standard constructor */
	public FieldTypeAST(String graphQLTypeName) {
		this.graphQLTypeSimpleName = graphQLTypeName;
	}

	/**
	 * Returns the java type as it an be used to declare a variable or an attribute. For instance, a field of GraphQL
	 * type <I>[ID]</I>, in client mode (where an ID is a java String), the result would be: <I>List&lt;String&gt;</I>.
	 * This always uses the java short name. So the proper import must be added into the enclosing java file.<BR/>
	 * The aim of this recursive method is to manage list of list of list of list...
	 */
	public String getJavaType(String classSimpleName) {
		if (listDepth > 0)
			return new StringBuilder()//
					.append(listDepth > 0 ? "List<" : "")//
					.append(listItemFieldTypeAST.getJavaType(classSimpleName))//
					.append(listDepth > 0 ? ">" : "")//
					.toString();
		else
			return classSimpleName;
	}

	/**
	 * The full type of this field, as defined in the GraphQL schema. For instance, a list of list of String, where
	 * everything is mandatory would be <I>[[String!]!]!</I>
	 */
	public String getGraphQLType() {
		if (listDepth > 0)
			return new StringBuilder()//
					.append(listDepth > 0 ? "[" : "")//
					.append(listItemFieldTypeAST.getGraphQLType())//
					.append(listDepth > 0 ? "]" : "")//
					.append(mandatory ? "!" : "")//
					.toString();
		else
			return graphQLTypeSimpleName + (mandatory ? "!" : "");
	}

	public String getGraphQLTypeSimpleName() {
		if (listDepth > 0)
			return listItemFieldTypeAST.getGraphQLTypeSimpleName();
		else
			return graphQLTypeSimpleName;
	}

	public void setGraphQLTypeName(String graphQLTypeName) {
		if (listDepth > 0)
			listItemFieldTypeAST.setGraphQLTypeName(graphQLTypeName);
		else
			this.graphQLTypeSimpleName = graphQLTypeName;
	}

}
