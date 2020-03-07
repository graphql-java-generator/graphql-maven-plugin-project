package com.graphql_java_generator.plugin.language.impl;

import java.util.ArrayList;
import java.util.List;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.plugin.PluginMode;
import com.graphql_java_generator.plugin.language.AppliedDirective;
import com.graphql_java_generator.plugin.language.Type;

import lombok.Data;

@Data
public abstract class AbstractType implements Type {

	/** The name of the object type */
	private String name;

	/** The current generation mode */
	private PluginMode mode;

	/** The name of the package for this class */
	private String packageName;

	/**
	 * Tha Java annotation(s) to add to this type, ready to be added by the Velocity template. That is: one annotation
	 * per line, each line starting at the beginning of the line
	 */
	private String annotation;

	/** All directives that have been defined in the GraphQL schema for this type */
	private List<AppliedDirective> appliedDirectives = new ArrayList<>();

	/** The GraphQL type for this type */
	final private GraphQlType graphQlType;

	public AbstractType(String packageName, PluginMode mode, GraphQlType graphQlType) {
		this.packageName = packageName;
		this.mode = mode;
		this.graphQlType = graphQlType;
	}

	@Override
	public GraphQlType getGraphQlType() {
		return graphQlType;
	}

	/** {@inheritDoc} */
	@Override
	public String getClassSimpleName() {
		return getJavaName();
	}

	/** {@inheritDoc} */
	@Override
	public String getCamelCaseName() {
		return GraphqlUtils.graphqlUtils.getCamelCase(getClassSimpleName());
	}

	/** {@inheritDoc} */
	@Override
	public String getConcreteClassSimpleName() {
		return getClassSimpleName();
	}

	@Override
	public String getClassFullName() {
		return packageName + "." + getClassSimpleName();
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public String getAnnotation() {
		return (this.annotation == null) ? "" : this.annotation;
	}

	/**
	 * The annotation setter should be used. Please use the {@link #addAnnotation(String)} instead
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
	@Override
	public void addAnnotation(String annotationToAdd) {
		if (this.annotation == null || this.annotation.contentEquals("")) {
			this.annotation = annotationToAdd;
		} else {
			// We add this annotation on a next line.
			this.annotation = this.annotation + "\n\t\t" + annotationToAdd;
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
	@Override
	public void addAnnotation(String annotationToAdd, boolean replace) {
		if (replace)
			this.annotation = "";

		addAnnotation(annotationToAdd);
	}

}
