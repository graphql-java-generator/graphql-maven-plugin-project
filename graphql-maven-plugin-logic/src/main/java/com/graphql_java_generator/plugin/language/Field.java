/**
 * 
 */
package com.graphql_java_generator.plugin.language;

import java.util.List;

import com.graphql_java_generator.GraphqlUtils;

import graphql.language.Value;

/**
 * This interface describes one field of one object type (or interface...). It aims to be simple enough, so that the
 * Velocity template can easily generated the fields from it.<BR/>
 * For instance:
 * 
 * <PRE>
 * name: String!
 * </PRE>
 * 
 * is a {@link Field} of name 'name', type 'String', and is mandatory. or
 * 
 * <PRE>
 * appearsIn: [Episode!]!
 * </PRE>
 * 
 * is a {@link Field} of name 'appearsIn', type 'Episode', is a list, is mandatory and its items are mandatory.
 * 
 * @author etienne-sf
 */
public interface Field {

	/**
	 * The name of the field, as found in the GraphQL schema
	 * 
	 * @return The name of the field
	 */
	public String getName();

	/**
	 * The type of this field, as defined in the GraphQL schema. This type is either the type of the field (if it's not
	 * a list), or the type of the items in the list (if it's a list)
	 */
	default public String getGraphQLTypeSimpleName() {
		return getFieldTypeAST().getGraphQLTypeSimpleName();
	}

	/**
	 * The full type of this field, as defined in the GraphQL schema. For instance, a list of list of String, where
	 * everything is mandatory would be <I>[[String!]!]!</I>
	 */
	default public String getGraphQLType() {
		return getFieldTypeAST().getGraphQLType();
	}

	/**
	 * Returns the GraphQL type information, as it has been read from the AST. See {@link FieldTypeAST} for more
	 * information, here.
	 * 
	 * @return
	 */
	public FieldTypeAST getFieldTypeAST();

	/**
	 * The name of the field, as it can be used in the Java code. If the name is a java keyword (class, default,
	 * break...), the java name it prefixed by an underscore.
	 * 
	 * @return The name of the field, as it can be used in Java code
	 */
	default public String getJavaName() {
		return GraphqlUtils.graphqlUtils.getJavaName(getName());
	}

	/**
	 * Returns the java type as it an be used to declare a variable or an attribute. For instance, a field of GraphQL
	 * type <I>[ID]</I>, in client mode (where an ID is a java String), the result would be: <I>List&lt;String&gt;</I>.
	 * This always uses the java short name. So the proper import must be added into the enclosing java file.
	 */
	default public String getJavaType() {
		return getFieldTypeAST().getJavaType(getType().getClassSimpleName());
	}

	/**
	 * Retrieves the {@link Type} which contains this field
	 * 
	 * @return
	 */
	public Type getOwningType();

	/**
	 * Retrieves the {@link Type} for this field
	 * 
	 * @return
	 */
	public Type getType();

	/**
	 * Indicates whether this field is an id or not. It's used in server mode to add the javax.persistence annotations
	 * for the id fields. Default value is false. This field is set to true for GraphQL fields which are of 'ID' type.
	 */
	public boolean isId();

	/** All fields in an object may have parameters. A parameter is actually a field. */
	public List<Field> getInputParameters();

	/**
	 * Contains the default value.. Only used if this field is an input parameter. For enums, it contains the label of
	 * the enum, not the value of the enum.<BR/>
	 * We store the graphql.language.Value as we receive it. We may not have parsed the relevant Object to check its
	 * field, and obviously, we can"t instanciate any object or enum yet, as we dont't even generated any code.
	 */
	public Value<?> getDefaultValue();

	/**
	 * Returns the {@link Relation} description for this field.
	 * 
	 * @return null if this field is not a relation to another Entity
	 */
	public Relation getRelation();

	/**
	 * Retrieves the annotation or annotations to add to this field, when in server mode, to serve the relation that
	 * this field holds
	 * 
	 * @return The relevant annotation(s) ready to add directly as-is in the Velocity template, or "" (an empty string)
	 *         if there is no annotation to add. The return is never null.
	 */
	public String getAnnotation();

	/**
	 * Convert the given name, which is supposed to be in camel case (for instance: thisIsCamelCase) to a pascal case
	 * string (for instance: ThisIsCamelCase).
	 * 
	 * @return
	 */
	public String getPascalCaseName();

	/**
	 * Convert the given name, which can be in non camel case (for instance: ThisIsCamelCase) to a pascal case string
	 * (for instance: thisIsCamelCase).
	 * 
	 * @return
	 */
	public default String getCamelCaseName() {
		return GraphqlUtils.graphqlUtils.getCamelCase(getName());
	}

	/** Returns the list of directives that have been defined for this field, in the GraphQL schema */
	public List<AppliedDirective> getAppliedDirectives();
}
