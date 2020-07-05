/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.GraphQLDocumentParser;
import com.graphql_java_generator.plugin.PluginMode;
import com.graphql_java_generator.plugin.language.AppliedDirective;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.Relation;
import com.graphql_java_generator.plugin.language.Type;

import graphql.language.Value;
import lombok.Builder;
import lombok.Data;

/**
 * @author etienne-sf
 */
@Data
@Builder(toBuilder = true)
public class FieldImpl implements Field {

	/**
	 * The {@link GraphQLDocumentParser} instance, which will allow to get the {@link Type} of the field, from its
	 * typeName, after the whole parsing is finished
	 */
	final DocumentParser documentParser;

	/** The name of the field */
	private String name;

	/** The {@link Type} which contains this field */
	private Type owningType;

	/**
	 * The type of this field, as defined in the GraphQL schema. This type is either the type of the field (if it's not
	 * a list), or the type of the items in the list (if it's a list)
	 */
	private String graphQLTypeName;

	/**
	 * Indicates whether this field is an id or not. It's used in {@link PluginMode#SERVER} mode to add the
	 * javax.persistence annotations for the id fields. Default value is false. This field is set to true for GraphQL
	 * fields which are of 'ID' type.
	 */
	@Builder.Default
	private boolean id = false;

	/** All fields in an object may have parameters. A parameter is actually a field. */
	@Builder.Default // Allows the default value to be used with the Lombok @Builder annotation on the class
	private List<Field> inputParameters = new ArrayList<>();

	/** Is this field a list? */
	@Builder.Default
	private boolean list = false;

	/**
	 * Is this field mandatory? If this field is a list, then mandatory indicates whether the list itself is mandatory,
	 * or may be nullable
	 */
	@Builder.Default
	private boolean mandatory = false;

	/** Indicates whether the item in the list are not nullable, or not. Only used if this field is a list. */
	@Builder.Default
	private boolean itemMandatory = false;

	/**
	 * Contains the default value, as defined in the GraphQL schema. For enums, it contains the label of the enum, not
	 * the value of the enum.<BR/>
	 * We store the graphql.language.Value as we receive it. We may not have parsed the relevant Object to check its
	 * field, and obviously, we can"t instanciate any object or enum yet, as we dont't even generated any code.
	 */
	@Builder.Default
	private Value<?> defaultValue = null;

	/** Contains the description of the relation that this field holds */
	@Builder.Default
	private Relation relation = null;

	/**
	 * The Java annotation to add to this type, ready to be added by the Velocity template. That is: one annotation per
	 * line, each line starting at the beginning of the line
	 */
	@Builder.Default
	private String annotation = "";

	/** All directives that have been defined in the GraphQL schema for this field */
	@Builder.Default
	private List<AppliedDirective> appliedDirectives = new ArrayList<>();

	@Override
	public Type getType() {
		return documentParser.getType(graphQLTypeName);
	}

	public void setGraphQLTypeName(String graphQLTypeName) {
		this.graphQLTypeName = graphQLTypeName;
		if (graphQLTypeName.equals("ID")) {
			setId(true);
		}
	}

	@Override
	public String getAnnotation() {
		return (this.annotation == null) ? "" : this.annotation;
	}

	/**
	 * The annotation setter should be used. Please use the {@link #addAnnotation(String))} instead
	 * 
	 * @param annotation
	 *            The annotation, that will replace the current one
	 */
	@Deprecated
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	/**
	 * The annotation setter should be added. This method allows to properly manage indentation in the generated source
	 * code
	 * 
	 * @param annotationToAdd
	 *            The annotation, that will be added to the current one
	 */
	public void addAnnotation(String annotationToAdd) {
		if (this.annotation == null || this.annotation.contentEquals("")) {
			this.annotation = annotationToAdd;
		} else {
			// We add this annotation on a next line.
			this.annotation = this.annotation + "\n\t" + annotationToAdd;
		}
	}

	/**
	 * The annotation setter should be added. This method allows to properly manage indentation in the generated source
	 * code
	 * 
	 * @param annotationToAdd
	 *            The annotation, that will be added to the current one
	 * @param replace
	 *            if true, any existing annotation is first removed
	 */
	public void addAnnotation(String annotationToAdd, boolean replace) {
		if (replace)
			this.annotation = "";

		addAnnotation(annotationToAdd);
	}

	@Override
	public String getPascalCaseName() {
		String name = getName();
		if ("Boolean".equals(name)) {
			String[] camelSplittedProperty = name.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
			if ("is".equals(camelSplittedProperty[0]) && camelSplittedProperty.length > 1) {
				name = GraphqlUtils.graphqlUtils
						.getCamelCase(StringUtils.join(ArrayUtils.remove(camelSplittedProperty, 0)));
			}
		}
		return GraphqlUtils.graphqlUtils.getPascalCase(name);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		// Field's name
		sb.append("Field {name: ").append(getName());

		// Field's type
		sb.append(", type: ");
		appendType(sb, this);

		// Field's parameters
		sb.append(", params: [");
		boolean appendSeparator = false;
		for (Field param : inputParameters) {
			if (appendSeparator)
				sb.append(",");
			else
				appendSeparator = true;
			appendType(sb, this);
		} // for
		sb.append("]}");

		return sb.toString();
	}

	private void appendType(StringBuilder sb, FieldImpl field) {
		if (field.isList())
			sb.append("[");
		sb.append(field.graphQLTypeName);
		if (field.isList() && field.isItemMandatory())
			sb.append("!");
		if (field.isList())
			sb.append("]");
		if (field.isMandatory())
			sb.append("!");
	}
}
