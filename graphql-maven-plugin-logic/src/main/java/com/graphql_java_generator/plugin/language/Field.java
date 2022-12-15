/**
 * 
 */
package com.graphql_java_generator.plugin.language;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.graphql_java_generator.plugin.language.impl.ObjectType;
import com.graphql_java_generator.util.GraphqlUtils;

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
	 * Returns the java type as it an be used to declare a variable or an attribute, <B>without declaring this type in
	 * the import list</B>. For instance, a field of GraphQL type <I>[ID]</I>, in client mode (where an ID is a java
	 * String), the result would be: <I>List&lt;java.lang.String&gt;</I>. This always uses the full class name. So the
	 * proper import doesn't need to be added into the enclosing java file.
	 */
	default public String getJavaTypeFullClassname() {
		return getFieldTypeAST().getJavaType(getType().getClassFullName());
	}

	/**
	 * Return the list of java full classnames for this field, in all the interfaces implemented directly or indirectly
	 * by the owning type, and that contains this field. <BR/>
	 * For instance, in the next sample, when called for bar's field of TFoo, this method returns a Set that contains
	 * the full java class names for both IBar1 and IBar2: <BR/>
	 * <code>
	interface IBar1 {
		id: ID
	}
	
	interface IBar2 {
		id: ID
	}
	
	interface IFoo1 {
		id: ID
		bar: IBar1
	}
	
	interface IFoo2 {
		id: ID
		bar: IBar2
	}
	
	type TBar12 implements IBar1 & IBar2 {
		id: ID
	}
	
	type TFoo implements IFoo1 & IFoo2 {
		id: ID
		bar: TBar12
	}
	</code>
	 * 
	 * @return
	 */
	default public Set<String> getFieldJavaFullClassnamesFromImplementedInterface() {
		Set<String> ret = new HashSet<>();

		Type owningType = getOwningType();
		if (owningType instanceof ObjectType) {
			for (ObjectType implementedType : ((ObjectType) owningType).getImplementedTypes()) {
				// If this type contains a field with the same name as the current field's name, we add its to the set
				// to be returned.
				for (Field f : implementedType.getFields()) {
					if (f.getName().equals(getName())) {
						ret.add(f.getJavaTypeFullClassname());

						// This field may itself inherit this field from an implemented type. Let's recurse once.
						ret.addAll(f.getFieldJavaFullClassnamesFromImplementedInterface());

						// We're done for the fields of this object
						break;
					}
				} // for (Field)
			} // for (ObjectType)
		} // if (owningType instanceof ObjectType)

		return ret;
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
	 * Returns the default value, as text, as it can be written into a generated GraphQL schema.<BR/>
	 * A <I>str</I> string default value will be returned as <I>"str"</I>,a <I>JEDI</I> enum value will be returned as
	 * <I>JEDI</I>, ...
	 * 
	 * @return
	 */
	default public String getDefaultValueAsText() {
		return GraphqlUtils.graphqlUtils.getValueAsText(getDefaultValue());
	}

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

	/** Returns the comments that have been found before this object, in the provided GraphQL schema */
	public List<String> getComments();

	/** Returns the description for this object, in the provided GraphQL schema */
	public Description getDescription();

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
