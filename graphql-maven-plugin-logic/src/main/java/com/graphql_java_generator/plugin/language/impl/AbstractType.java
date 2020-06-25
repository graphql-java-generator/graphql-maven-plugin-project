package com.graphql_java_generator.plugin.language.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.plugin.language.AppliedDirective;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.Type;

import lombok.Data;

@Data
public abstract class AbstractType implements Type {

	/** The name of the object type */
	private String name;

	/** The name of the package for this class */
	private String packageName;

	/** The list of imports for this object. It's an order Set, so that the generated java file is net */
	private Set<String> imports = new TreeSet<>();

	/**
	 * Tha Java annotation(s) to add to this type, ready to be added by the Velocity template. That is: one annotation
	 * per line, each line starting at the beginning of the line
	 */
	private String annotation;

	/** All directives that have been defined in the GraphQL schema for this type */
	private List<AppliedDirective> appliedDirectives = new ArrayList<>();

	/** The GraphQL type for this type */
	final private GraphQlType graphQlType;

	public AbstractType(String packageName, GraphQlType graphQlType) {
		this.packageName = packageName;
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

	/** {@inheritDoc} */
	@Override
	public void addImport(Class<?> clazz) {
		// For classes defined in a class, the simple name is the name of like this: "MainClassname$InnerClassname"
		// For them, we do like if the packagename is : package.name.MainClassname
		// and the simple name is InnerClassname
		String classFullname = clazz.getName();
		int dollarPos = classFullname.indexOf('$');
		if (dollarPos > 0) {
			String packageName = classFullname.substring(0, dollarPos);
			String classname = classFullname.substring(dollarPos + 1);
			addImport(packageName, classname);
		} else {
			addImport(clazz.getPackage().getName(), clazz.getSimpleName());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void addImport(String packageName, String classname) {
		// For inner class, the classname is "MainClassname$InnerClassname". And the inner class must be imported, even
		// if we are in the same package
		int dollarPos = classname.indexOf('$');
		if (dollarPos > 0) {
			packageName = packageName + "." + classname.substring(0, dollarPos);
			classname = classname.substring(dollarPos + 1);
		}

		// For inner classes, the classname may be sent as "MainClassname.InnerClassname"
		int dotPos = classname.indexOf('.');
		if (dotPos > 0) {
			packageName = packageName + "." + classname.substring(0, dotPos);
			classname = classname.substring(dotPos + 1);
		}

		// No import for java.lang
		// And no import if the classname is the same as the current object: there would be a name conflict.
		// In this case, the import "silently fails": the class is not imported in the imports list.
		if (!packageName.equals("java.lang") && !getJavaName().equals(classname)) {
			// As we're in a type, we're not in a utility class. So the current type will generate a class in the
			// pluginConfiguration's packageName
			if (!getPackageName().equals(packageName)) {
				imports.add(packageName + "." + classname);
			}
		}
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

}
