package com.graphql_java_generator.client.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLInterfaceType;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLQuery;
import com.graphql_java_generator.annotation.GraphQLUnionType;
import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.directive.Directive;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * This class gives parsing capabilities for the QueryString for one object.<BR/>
 * For instance, for the GraphQL query <I>queryType.boards("{id name publiclyAvailable topics(since:
 * \"2018-12-20\"){id}}")</I>, it is created for the field named <I>boards</I>, then the
 * {@link #readTokenizerForResponseDefinition(StringTokenizer)} is called for the whole String. <BR/>
 * Then another {@link QueryField} is created, for the field named <I>topics</I>, and the <I>(since: \"2018-12-20\")</I>
 * is parsed by the {@link #readTokenizerForInputParameters(StringTokenizer)}, then the <I>{id}</I> String is parsed by
 * {@link #readTokenizerForResponseDefinition(StringTokenizer)} .
 * 
 * @author EtienneSF
 */
public class QueryField {

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(QueryField.class);

	/** A utility class, for various ... utility methods :) */
	GraphqlUtils graphqlUtils = GraphqlUtils.graphqlUtils;
	/** Another utility class, for various ... utility methods :) */
	GraphqlClientUtils graphqlClientUtils = GraphqlClientUtils.graphqlClientUtils;

	/** The class that contains this field */
	Class<?> owningClazz;
	/**
	 * The GraphQL class of the type, that is: the type of the field if it's not a List. And the type of the items of
	 * the list, if the field's type is a list
	 */
	final Class<?> clazz;
	/** The name of this field */
	final String name;
	/** The alias of this field */
	final String alias;
	/**
	 * The package name, where the generated classes are. It's used to load the class definition, and get the GraphQL
	 * metadata coming from the GraphQL schema
	 */
	final String packageName;

	/** true if the {@link QueryField} is a query, a mutation or a subscription. False otherwise. */
	Boolean scalar = null;
	/** true if the {@link QueryField} is a query, a mutation or a subscription. False otherwise. */
	Boolean queryLevel = null;

	/** The list of input parameters for this QueryField */
	List<InputParameter> inputParameters = new ArrayList<>();

	/** The list of directives for this QueryField */
	List<Directive> directives = new ArrayList<>();

	/**
	 * The lists of fragment that are in this field's definition, like <I>fragment1</I> and <I>fragment2</I> in:
	 * <I>thisField {field1 ...fragment1 field2 ...fragment2}</I>.
	 */
	List<AppliedGlobalFragment> fragments = new ArrayList<>();
	/** The list of inline fragments that are defined for this field */
	List<Fragment> inlineFragments = new ArrayList<>();

	/**
	 * All subfields contained in this field. It should remain empty if the field is a GraphQL Scalar. At least one if
	 * the field is a not a Scalar
	 */
	List<QueryField> fields = new ArrayList<>();

	/**
	 * The constructor, when created by the {@link Builder}: it must provide the owningClass
	 * 
	 * @param owningClass
	 *            The {@link Class} that owns the field
	 * @param fieldName
	 *            The name of the field
	 * @param fieldAlias
	 *            The alias for this field
	 * @throws GraphQLRequestPreparationException
	 */
	public QueryField(Class<?> owningClass, String fieldName, String fieldAlias)
			throws GraphQLRequestPreparationException {
		graphqlClientUtils.checkName(fieldName);
		if (fieldAlias != null) {
			graphqlClientUtils.checkName(fieldAlias);
		}
		this.owningClazz = owningClass;
		this.clazz = graphqlClientUtils.checkFieldOfGraphQLType(fieldName, null, owningClass);
		this.name = fieldName;
		this.alias = fieldAlias;
		this.packageName = owningClass.getPackage().getName();
	}

	/**
	 * The constructor, when created by the {@link Builder}: it must provide the owningClass
	 * 
	 * @param owningClass
	 *            The {@link Class} that owns the field
	 * @param fieldName
	 *            The name of the field
	 * @throws GraphQLRequestPreparationException
	 */
	public QueryField(Class<?> owningClass, String fieldName) throws GraphQLRequestPreparationException {
		this(owningClass, fieldName, null);
	}

	/**
	 * The constructor, when created for a {@link Fragment}. We only know the class of the Fragment. This class is the
	 * owning class of all the fields defined in the fragment.<BR/>
	 * The access for this constructor is limited to the package, as only the {@link Fragment} class should call it.
	 * 
	 * @param clazz
	 *            The {@link Class} of the {@link Fragment} we're about to read.
	 * @throws GraphQLRequestPreparationException
	 */
	QueryField(Class<?> clazz) throws GraphQLRequestPreparationException {
		this.owningClazz = null;
		this.clazz = clazz;
		this.name = null;
		this.alias = null;
		this.packageName = clazz.getPackage().getName();
	}

	/**
	 * Reads the definition of the expected response definition from the server. It is recursive.<BR/>
	 * For instance, for the GraphQL query <I>queryType.boards("{id name publiclyAvailable topics(since:
	 * \"2018-12-20\"){id}}")</I>, it will be called twice: <BR/>
	 * Once for the String <I>id name publiclyAvailable topics(since: \"2018-12-20\"){id}}</I> (without the leading
	 * '{'), where QueryField is <I>boards</I>,<BR/>
	 * Then for the String <I>id}</I>, where the QueryField is <I>topics</I>
	 * 
	 * @param qt
	 *            The {@link StringTokenizer}, where the next token is the first token <B><I>after</I></B> the '{' have
	 *            already been read. <BR/>
	 *            The {@link StringTokenizer} is read until the '}' associated with this already read '{'.<BR/>
	 *            For instance, when this method is called with the {@link StringTokenizer} where these characters are
	 *            still to read: <I>id date author{name email alias} title content}}</I>, the {@link StringTokenizer} is
	 *            read until and including the first '}' that follows content. Thus, there is still a '}' to read.
	 * @throws GraphQLRequestPreparationException
	 */
	public void readTokenizerForResponseDefinition(QueryTokenizer qt) throws GraphQLRequestPreparationException {
		// The field we're reading
		QueryField currentField = null;

		while (qt.hasMoreTokens()) {

			String token = qt.nextToken();

			switch (token) {
			case "@":
				// We're found a GraphQL directive.
				currentField.directives.add(new Directive(qt));
				break;
			case "(":
				if (currentField != null) {
					// We're starting the reading of field parameters for the current
					currentField.inputParameters = InputParameter.readTokenizerForInputParameters(qt, null,
							currentField.owningClazz, currentField.name);
				} else {
					throw new GraphQLRequestPreparationException(
							"The given query has a parentesis '(' not preceded by a field name (error while reading field <"
									+ name + ">");
				}
				break;
			case "{":
				// The last field we've read is actually an object (a non Scalar GraphQL type), as it itself has
				// fields
				if (currentField == null) {
					throw new GraphQLRequestPreparationException(
							"The given query has two '{', one after another (error while reading field <" + name
									+ ">)");
				} else if (currentField.clazz == null) {
					throw new GraphQLRequestPreparationException(
							"Starting reading definition of field '" + currentField.name + "' of class '"
									+ owningClazz.getName() + "', but the owningClass is not set");
				} else if (currentField.fields.size() > 0) {
					throw new GraphQLRequestPreparationException(
							"The given query contains a '{' not preceded by a fieldname, after field <"
									+ currentField.name + "> while reading <" + this.name + ">");
				} else {
					// Ok, let's read the field for the subobject, for which we just read the name (and potentiel
					// alias :
					currentField.readTokenizerForResponseDefinition(qt);
					// Let's clear the lastReadField, as we already have read its content.
					currentField = null;
				}
				break;
			case "...":
				// We're reading an inline fragment
				inlineFragments.add(new Fragment(qt, packageName, true, clazz));
				break;
			case "}":
				// We're finished our current object : let's get out of this method
				// (end of this recursion level)
				return;
			default:
				if (token.startsWith("...")) {
					// This token starts by "...", we've read a global fragment
					fragments.add(new AppliedGlobalFragment(token, qt));
					logger.trace("Found fragment {} for field {}", token, name);
				} else {
					// We've read a regular field
					if (qt.checkNextToken(":")) {
						// The next token is ":", so we've found an alias (not a name field)
						String alias = token;
						token = qt.nextToken(); // It's the ":". We ignore it
						token = qt.nextToken();
						currentField = new QueryField(clazz, token, alias);
					} else {
						currentField = new QueryField(clazz, token);
					}

					// Does a field of this name already exist ?
					// (if this name is an alias, we'll read the real name later, and we'll repeat the check later)
					if (getField(currentField.name) != null) {
						throw new GraphQLRequestPreparationException("The field <" + currentField.name
								+ "> exists twice in the field list for the " + owningClazz.getSimpleName() + " type");
					}

					fields.add(currentField);
				}
			}// switch
		} // while

		// Oups, we should not arrive here:
		throw new GraphQLRequestPreparationException("The field <" + name
				+ "> has a non finished list of fields (it lacks the finishing '}') while reading <" + this.name + ">");
	}

	/**
	 * Append this query field in the {@link StringBuilder} in which the query is being written. Any parameter will be
	 * replaced by its value. It's a recursive method, that calls itself when this field is not a scalar: it calls
	 * itself for each subfield.
	 * 
	 * @param sb
	 * @param parameters
	 * @param appendName
	 *            true if the name of the field must be written in the query (for regular fields for instance). False
	 *            otherwise (for fragments, for instance)
	 * @throws GraphQLRequestExecutionException
	 */
	public void appendToGraphQLRequests(StringBuilder sb, Map<String, Object> parameters, boolean appendName)
			throws GraphQLRequestExecutionException {

		//////////////////////////////////////////////////////////
		// We start with the field name
		if (appendName) {
			if (alias == null) {
				sb.append(name);
			} else {
				sb.append(alias).append(":").append(name);
			}
		}

		//////////////////////////////////////////////////////////
		// Then the input parameters
		InputParameter.appendInputParametersToGraphQLRequests(sb, inputParameters, parameters);

		//////////////////////////////////////////////////////////
		// Then the directives
		for (Directive d : directives) {
			d.appendToGraphQLRequests(sb, parameters);
		}

		//////////////////////////////////////////////////////////
		// Then field list (if any)
		boolean appendSpaceLocal = false;

		String unionName = getUnionName();
		if (fields.size() > 0 || fragments.size() > 0 || inlineFragments.size() > 0 || unionName != null) {
			logger.debug("Appending ReponseDef content for field " + name + " of type " + clazz.getSimpleName());
			sb.append("{");

			// For union, we need to be sure to always have the __typename field
			if (unionName != null) {
				sb.append("... on ");
				sb.append(unionName);
				sb.append("{__typename}");
				appendSpaceLocal = true;
			}

			// Let's append the fields...
			for (QueryField f : fields) {
				if (appendSpaceLocal) {
					sb.append(" ");
				}
				f.appendToGraphQLRequests(sb, parameters, true);
				appendSpaceLocal = true;
			}

			// ...the fragment names
			for (AppliedGlobalFragment f : fragments) {
				if (appendSpaceLocal) {
					sb.append(" ");
				}
				f.appendToGraphQLRequests(sb, parameters);
				appendSpaceLocal = true;
			} // for

			// ...the inline fragments
			for (Fragment f : inlineFragments) {
				if (appendSpaceLocal) {
					sb.append(" ");
				}
				sb.append("...");
				f.appendToGraphQLRequests(sb, parameters);
				appendSpaceLocal = true;
			} // for

			sb.append("}");
		}
	}

	/**
	 * If the field's type is a GraphQL union, then this method returns the union's name as defined in the GraphQL
	 * schema. Otherwise returns null
	 * 
	 * @return
	 */
	private String getUnionName() {
		// All the generated classes have a GraphQL annotation.
		// If no such annotation, then this type is a scalar.
		GraphQLUnionType graphQLUnionType = clazz.getAnnotation(GraphQLUnionType.class);
		return (graphQLUnionType == null) ? null : graphQLUnionType.value();
	}

	/**
	 * If this field is not a scalar, this method adds the _typename into the requested fields list (if it doesn't
	 * already exist) for this {@link QueryField}, and the same recursively for all its non scalar fields. <BR/>
	 * If this field is a scalar, no action.
	 * 
	 * @param objectResponse
	 * @throws GraphQLRequestPreparationException
	 */
	void addTypenameFields() throws GraphQLRequestPreparationException {

		// Action only for non scalar fields
		if (!isScalar()) {
			if (inlineFragments.size() > 0) {
				// We add the __typename field into all fragments, but not on the type itself (useless)
				for (Fragment f : inlineFragments) {
					f.addTypenameFields();
				}
			} else if (fragments.size() == 0) {
				// It's a non scalar field, without any fragment. We must add the __typename
				QueryField __typename = null;

				// Let's go through sub fields to look for an existing __typename field
				for (QueryField f : fields) {
					if (f.name.equals("__typename")) {
						__typename = f;
						break;
					}
					f.addTypenameFields();
				}

				// We add the __typename for all levels, but not for the query/mutation/subscription one
				if (!isQueryLevel() && __typename == null) {
					__typename = new QueryField(this.clazz, "__typename");
					fields.add(__typename);
				}
			}

			// In all cases, we need to recurse into each fields of the current one.
			for (QueryField f : fields) {
				f.addTypenameFields();
			}
		}

	}

	/**
	 * Indicates whether this field is a scalar or not.
	 * 
	 * @return true if this field is a scalar (custom or not), and false otherwise.
	 * @throws GraphQLRequestPreparationException
	 */
	public boolean isScalar() throws GraphQLRequestPreparationException {
		if (scalar == null) {
			// The scalar value has not yet been calculated.

			// All the generated classes have a GraphQL annotation.
			// If no such annotation, then this type is a scalar.
			GraphQLInputType graphQLInputType = clazz.getAnnotation(GraphQLInputType.class);
			GraphQLInterfaceType graphQLInterfaceType = clazz.getAnnotation(GraphQLInterfaceType.class);
			GraphQLObjectType graphQLObjectType = clazz.getAnnotation(GraphQLObjectType.class);
			GraphQLQuery graphQLQuery = clazz.getAnnotation(GraphQLQuery.class);
			GraphQLUnionType graphQLUnionType = clazz.getAnnotation(GraphQLUnionType.class);

			// If one of these annotations is not null, then it's not a scalar. Otherwise, this type is a scalar.
			scalar = !(graphQLInputType != null || graphQLInterfaceType != null || graphQLObjectType != null
					|| graphQLQuery != null || graphQLUnionType != null);
		}
		return scalar;
	}

	/**
	 * Indicates whether this field is a query/mutation/subscription or not
	 * 
	 * @return true if the {@link QueryField} is a query, a mutation or a subscription. False otherwise.
	 */
	public boolean isQueryLevel() {
		if (queryLevel == null) {
			queryLevel = name != null
					&& (name.equals("query") || name.equals("mutation") || name.equals("subscription"));
		}
		return queryLevel;
	}

	/**
	 * Returns the subfield for this {@link QueryField} of the given name
	 * 
	 * @param name
	 *            The field's name to search
	 * @return The subfield of the given name, or null of this {@link QueryField} contains no field of this name
	 */
	QueryField getField(String name) {
		for (QueryField f : fields) {
			if (f.name.equals(name)) {
				// We found it
				return f;
			}
		}
		// No field of this name has been found
		return null;
	}

	public Class<?> getOwningClazz() {
		return owningClazz;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public String getName() {
		return name;
	}

	public String getAlias() {
		return alias;
	}

}
