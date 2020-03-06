/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import java.util.ArrayList;
import java.util.List;

import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.PluginMode;
import com.graphql_java_generator.plugin.language.Directive;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.Relation;
import com.graphql_java_generator.plugin.language.Type;

import lombok.Builder;
import lombok.Data;

/**
 * @author EtienneSF
 */
@Data
@Builder(toBuilder = true)
public class FieldImpl implements Field {

	/**
	 * The {@link DocumentParser} instance, which will allow to get the {@link Type} of the field, from its typeName,
	 * after the whole parsing is finished
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
	private boolean id = false;

	/** All fields in an object may have parameters. A parameter is actually a field. */
	@Builder.Default // Allows the default value to be used with the Lombok @Builder annotation on the class
	private List<Field> inputParameters = new ArrayList<>();

	/** Is this field a list? */
	private boolean list = false;

	/**
	 * Is this field mandatory? If this field is a list, then mandatory indicates whether the list itself is mandatory,
	 * or may be nullable
	 */
	private boolean mandatory = false;

	/** Indicates whether the item in the list are not nullable, or not. Only used if this field is a list. */
	private boolean itemMandatory = false;

	/**
	 * Contains the default value, as defined in the GraphQL schema. For enums, it contains the label of the enum, not
	 * the value of the enum.
	 */
	private Object defaultValue = null;

	/** Contains the description of the relation that this field holds */
	private Relation relation = null;

	/**
	 * The Java annotation to add to this type, ready to be added by the Velocity template. That is: one annotation per
	 * line, each line starting at the beginning of the line
	 */
	private String annotation = "";

	/** All directives that have been defined in the GraphQL schema for this field */
	private List<Directive> directives;

	/** {@inheritDoc} */
	@Override
	public Type getType() {
		return documentParser.getType(graphQLTypeName);
	}

	public void setGraphQLTypeName(String graphQLTypeName) {
		this.graphQLTypeName = graphQLTypeName;
		if (graphQLTypeName.equals("UUID")) {
			boolean breakpoint = true;
		}
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
	 * @parma replace if true, any existing annotation is first removed
	 */
	public void addAnnotation(String annotationToAdd, boolean replace) {
		if (replace)
			this.annotation = "";

		addAnnotation(annotationToAdd);
	}
}
