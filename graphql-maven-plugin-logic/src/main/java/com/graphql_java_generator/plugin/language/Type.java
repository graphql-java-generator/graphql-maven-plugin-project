/**
 * 
 */
package com.graphql_java_generator.plugin.language;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.util.GraphqlUtils;

/**
 * All types found in the GraphQL schema(s), and discovered during the GraphQL parsing, are instance of {@link Type}.
 * 
 * @author etienne-sf
 */
public interface Type {

	public static enum TargetFileType {
		ENUM, EXECUTOR, INTERFACE, JACKSON_DESERIALIZER, MUTATION, OBJECT, QUERY, REACTIVE_EXECUTOR, RESPONSE, ROOT_RESPONSE, SUBSCRIPTION, UNION
	}

	public enum GraphQlType {
		OBJECT, SCALAR, ENUM, INTERFACE, UNION
	}

	/**
	 * Returns the {@link DataFetcher} that is associated to this type. This is used only when generating server side
	 * code: it allows to manage GraphQL fields that have arguments. For these fields, if
	 * <i>generateDataFetcherForAllFieldsWithArgument</i> is true, then data fetcher is generated even if the field is a
	 * scalar or an enum.
	 * 
	 * @return
	 */

	default public DataFetcher getDataFetcher() {
		return null; // Default is to have no DataFetcher associated to this type
	}

	/**
	 * The GraphQL name of the object type
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * The name of the field, as it can be used in the Java code. If the name is a java keyword (class, default,
	 * break...), the java name it prefixed by an underscore.<br/>
	 * If a prefix or a suffix has been defined in the plugin configuration for this kind of item (object type, union,
	 * enum...), then the java name contains the prefix and/or the suffix
	 * 
	 * @return The name of the type, as it can be used in Java code
	 */
	default public String getJavaName() {
		return GraphqlUtils.graphqlUtils.getJavaName(getName());
	}

	/**
	 * Returns the package's name where the GraphQL objects from the GraphQL schema must be generated.
	 * 
	 * @return
	 */
	public String getPackageName();

	/** Get the filename where this type must be created. Default is to return the name for the Type */
	default public String getTargetFileName(TargetFileType fileType) {
		switch (fileType) {
		case EXECUTOR:
			return getName() + "Executor"; //$NON-NLS-1$
		case REACTIVE_EXECUTOR:
			return getName() + "ReactiveExecutor"; //$NON-NLS-1$
		case RESPONSE:
			return getName() + "Response"; //$NON-NLS-1$
		case ROOT_RESPONSE:
			return getName() + "RootResponse"; //$NON-NLS-1$
		default:
			return getJavaName();
		}
	}

	/**
	 * Get the list of imports for this object, for classes in the {@link GraphQLConfiguration#getPackageName()}. It's
	 * an order Set, so that the generated java file is clean.
	 * 
	 * @return
	 */
	public Set<String> getImports();

	/**
	 * Get the list of imports for this object, for classes in the utility package, that is for utility classes when
	 * {@link GraphQLConfiguration#isSeparateUtilityClasses()} is true. It's an order Set, so that the generated java
	 * file is clean.
	 * 
	 * @return
	 */
	public Set<String> getImportsForUtilityClasses();

	/**
	 * Add the given class as an import for the current type. This import will be added only if the given class is not
	 * in the same package as the java class for this type, and if it doesn't already exist in the imports set.<BR/>
	 * classes from the <I>java.lang</I> package are not imported.<BR/>
	 * Note1: for inner class, the classname may be "MainClassname$InnerClassname" (as returned by the
	 * {@link Class#getName()} method), par as "MainClassname.InnerClassname".<BR/>
	 * Note2: it is not allowed to import a class of the same name as the current class: there would be a name conflict.
	 * In this case, the import "silently fails": the class is not imported in the imports list.
	 * 
	 * @param targetPackageName
	 *            The package where the objects are generated
	 * @param classname
	 *            The full class name (java.lang.String for instance) of the class to import
	 */
	public void addImport(String targetPackageName, String classname);

	/**
	 * Same as {@link #addImport(String, String, String)}, but for the utility classes
	 * 
	 * @param targetPackageName
	 *            The package where the objects are generated
	 * @param classname
	 *            The full class name (java.lang.String for instance) of the class to import
	 */
	public void addImportForUtilityClasses(String targetPackageName, String classname);

	/**
	 * Returns the annotation or annotations that must be added to this type.
	 * 
	 * @return The relevant annotation(s) ready to add directly as-is in the Velocity template, or "" (an empty string)
	 *         if there is no annotation to add. The return is never null.
	 */
	public String getAnnotation();

	/**
	 * The annotation setter should be added. This method allows to properly manage indentation in the generated source
	 * code
	 * 
	 * @param annotationToAdd
	 *            The annotation, that will be added to the current one
	 */
	public void addAnnotation(String annotationToAdd);

	/**
	 * The annotation setter should be added. This method allows to properly manage indentation in the generated source
	 * code
	 * 
	 * @param annotationToAdd
	 *            The annotation, that will be added to the current one
	 * @parma replace if true, any existing annotation is first removed
	 */
	public void addAnnotation(String annotationToAdd, boolean replace);

	/** Returns the comments that have been found before this object, in the provided GraphQL schema */
	public List<String> getComments();

	/** Returns the description for this object, in the provided GraphQL schema */
	public Description getDescription();

	/**
	 * The GraphQlType for this type
	 * 
	 * @return
	 */
	public GraphQlType getGraphQlType();

	/**
	 * Returns "query", "mutation" or "subscription" if this type is a query, mutation or subscription. And null
	 * otherwise.
	 * 
	 * @return
	 */
	default public String getRequestType() {
		return null;
	}

	/**
	 * The java class simple name for this type. It may be and interface or a concrete class. <BR/>
	 * 
	 * @return The java classname is usually the name of the type. But in some case, collision my occur with the Java
	 *         syntax. In this cas, this method will return a classname different from the name
	 */
	public String getClassSimpleName();

	/**
	 * Returns the camel case String, based on the object's name. For instance: <I>bigFoot</I> for a type named
	 * <I>BigFoot</I>
	 * 
	 * @return
	 */
	public String getCamelCaseName();

	/**
	 * The java class full name for this type. It may be an interface or a concrete class. <BR/>
	 * 
	 * @return The java classname is usually the name of the type. But in some case, collision my occur with the Java
	 *         syntax. In this cas, this method will return a classname different from the name
	 */
	default public String getClassFullName() {
		if (getPackageName() == null)
			return getClassSimpleName();
		else
			return getPackageName() + "." + getClassSimpleName(); //$NON-NLS-1$
	}

	/**
	 * Returns the list of {@link Field}s for this type. Or null, if this field can't have any field, like a
	 * GraphQLScalar for instance
	 * 
	 * @return It returns the list of Fields, or an empty list of there are not fields. It should never be null. The
	 *         interface returns, by default, an empty list.
	 */
	default public List<Field> getFields() {
		return new ArrayList<Field>();
	}

	/**
	 * Returns the Field of the given name
	 * 
	 * @param fieldName
	 * @return
	 * @throws NoSuchFieldException
	 *             If this type has not field of this name
	 */
	default public Field getField(String fieldName) throws NoSuchFieldException {
		for (Field f : getFields()) {
			if (f.getName().equals(fieldName)) {
				return f;
			}
		}
		throw new NoSuchFieldException("The type '" + getName() + " has no field of name '" + fieldName + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Returns the identifier for this type. Typically : the field which has an ID as a type.
	 * 
	 * @return The identifier for this type, or null of this type has no identifier or multiplier identifiers (that is:
	 *         multiple identifiers or identifier based on several fields are not currently managed)
	 */
	public Field getIdentifier();

	/** Returns true if this type is a GraphQL InputObjectType, false otherwise */
	public boolean isInputType();

	/** Returns true if this type is a GraphQL Scalar (custom or not), false otherwise */
	public boolean isScalar();

	/** Returns true if this type is a GraphQL Custom Scalar, false otherwise */
	public boolean isCustomScalar();

	/** Returns true if this type is a GraphQL enum, false otherwise */
	default public boolean isEnum() {
		return false;
	}

	/** Returns the list of directives that have been defined for this type, in the GraphQL schema */
	public List<AppliedDirective> getAppliedDirectives();

}
