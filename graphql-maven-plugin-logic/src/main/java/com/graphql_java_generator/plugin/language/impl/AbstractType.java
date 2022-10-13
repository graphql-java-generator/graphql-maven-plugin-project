package com.graphql_java_generator.plugin.language.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.conf.CommonConfiguration;
import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.plugin.language.AppliedDirective;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.util.GraphqlUtils;

import graphql.language.Comment;
import lombok.Data;

@Data
public abstract class AbstractType implements Type {

	/**
	 * The current plugin configuration, which is accessible through an interface that extends
	 * {@link CommonConfiguration}
	 */
	final CommonConfiguration configuration;

	/** The {@link DocumentParser} that has parsed the schema, and so that contains the whole schema definition */
	final DocumentParser documentParser;

	/** The name of the object type */
	private String name;

	/** The list of imports for this object. It's an order Set, so that the generated java file is clean */
	private Set<String> imports = new TreeSet<>();

	/**
	 * The list of imports for this object, that should be used for utility classes, when
	 * {@link GraphQLConfiguration#isSeparateUtilityClasses()} is true. It's an order Set, so that the generated java
	 * file is clean
	 */
	private Set<String> importsForUtilityClasses = new TreeSet<>();

	/**
	 * Tha Java annotation(s) to add to this type, ready to be added by the Velocity template. That is: one annotation
	 * per line, each line starting at the beginning of the line
	 */
	private String annotation;

	/** All directives that have been defined in the GraphQL schema for this type */
	private List<AppliedDirective> appliedDirectives = new ArrayList<>();

	/** The comments that have been found for this object, in the provided GraphQL schema */
	private List<String> comments = new ArrayList<>();

	/** The GraphQL type for this type */
	final private GraphQlType graphQlType;

	/**
	 * @param name
	 * @param graphQlType
	 *            The type of object
	 * @param configuration
	 *            The current plugin configuration, which is accessible through an interface that extends
	 *            {@link CommonConfiguration}
	 * @param documentParser
	 *            The {@link DocumentParser} that has parsed the schema, and so that contains the whole schema
	 *            definition
	 */
	public AbstractType(String name, GraphQlType graphQlType, CommonConfiguration configuration,
			DocumentParser documentParser) {
		this.name = name;
		this.graphQlType = graphQlType;
		this.configuration = configuration;
		this.documentParser = documentParser;
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

    @Override
    public String getJavaName() {
        // Optionally add a prefix and/or suffix to the name
		String name = Stream.of(getPrefix(), getName(), getSuffix())
				.filter(Objects::nonNull).collect(Collectors.joining());

		return GraphqlUtils.graphqlUtils.getJavaName(name);
    }

	protected String getPrefix() {
		return "";
	}
	protected String getSuffix() {
		return "";
	}

	/** {@inheritDoc} */
	@Override
	public String getCamelCaseName() {
		return GraphqlUtils.graphqlUtils.getCamelCase(getClassSimpleName());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(name);

		if (getComments() == null) {
			sb.append(", comments=null");
		} else if (getComments().size() > 0) {
			sb.append(", comments=empty");
		} else {
			sb.append(", comments \"");
			sb.append(String.join("\\n", getComments()));
			sb.append("\"");
		}

		return sb.toString();
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

	@Override
	public void addImport(String targetPackageName, String classname) {
		if (!classname.endsWith("." + getJavaName())) {
			// We only import if it's another simple classname
			GraphqlUtils.graphqlUtils.addImport(imports, targetPackageName, classname);
		}
	}

	@Override
	public void addImportForUtilityClasses(String targetPackageName, String classname) {
		GraphqlUtils.graphqlUtils.addImport(importsForUtilityClasses, targetPackageName, classname);
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
			// Add indentation only for fields (not types)
			this.annotation = this.annotation + ((this instanceof Field) ? "\n\t\t" : "\n") + annotationToAdd;
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

	public void setComments(List<Comment> comments) {
		this.comments = new ArrayList<>(comments.size());
		for (Comment c : comments) {
			this.comments.add(c.getContent());
		}
	}
}
