/**
 * 
 */
package com.graphql_java_generator.plugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.plugin.conf.CommonConfiguration;
import com.graphql_java_generator.plugin.language.AppliedDirective;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.plugin.language.impl.FieldImpl;
import com.graphql_java_generator.plugin.language.impl.InterfaceType;
import com.graphql_java_generator.plugin.language.impl.ObjectType;

/**
 * This method add the relay capabilities into the GraphQL schema, as it has been read by {@link DocumentParser}. The
 * relay capabilities are specified in <A HREF="https://relay.dev/graphql/connections.htm">this doc</A>.<BR/>
 * The implementation is the one described on the Relay website. It (currently) doesn't implement the <I>generic utility
 * types</I>, as described in this article:
 * <A HREF="https://dev.to/mikemarcacci/intermediate-interfaces-generic-utility-types-in-graphql-50e8">Intermediate
 * Interfaces & Generic Utility Types in GraphQL</A>, which explains the choices in the GraphQL specifications, for
 * interfaces that implements interface(s), especially when implementing the relay connection. The reason is that the
 * capacity to "narrow" a field's type in a type inherited from an interface is not compatible with Java. So the Java
 * implementation will need to use Java Generics, and probably some hint (directive) to properly generate the Java
 * code.<BR/>
 * This class will add the items described below in the currently read schema data. It is the possible to generate a
 * Java Really compatible code (by using the <I>graphql</I> goal/task), or to generate the Relay compatible GraphQL
 * schema (by using the <I>mergeSchema</I> task/goal). <BR/>
 * The items added to the in-memory read schema are:
 * <UL>
 * <LI>The <I>Node</I> interface in the GraphQL schema (if not already defined). If this interface is already defined in
 * the given schema, but is not compliant, then an error is thrown.</LI>
 * <LI>The <I>&#064;RelayConnection</I> directive definition in the GraphQL schema (if not already defined). If this is
 * already defined in the given schema, but is not compliant with the relay specification, then an error is thrown.</LI>
 * <LI>The <I>PageInfo</I> type in the GraphQL schema (if not already defined). If this type is already defined in the
 * given schema, but is not compliant with the relay specification, then an error is thrown.</LI>
 * <LI>All the Edge and Connection types in the GraphQL schema, for each type of a field that is marked by the
 * <I>&#064;RelayConnection</I> directive. If types with these names exist, and are not compliant with the Relay
 * specification, then an error is thrown</LI>
 * <LI>Each type of a field that is marked by the <I>&#064;RelayConnection</I> directive, is marked by the <I>Node</I>
 * interface</I>
 * </UL>
 * 
 * @author etienne-sf
 */
@Component
public class AddRelayConnections {

	/**
	 * The {@link DocumentParser} contains the GraphQL schema data, as it has been read from the given GraphQL schema
	 * file(s).
	 */
	@Autowired
	private DocumentParser documentParser;

	@Autowired
	CommonConfiguration configuration;

	/**
	 * This will contain the {@link InterfaceType} for the Node interface, whether it has been created by this class, or
	 * it already exist before
	 */
	InterfaceType node = null;

	/** The main entry point of the class. It is responsible for doing what's described in the class documentation */
	public void addRelayConnections() {
		addNodeInterface();
		// addEdgeInterface();
		// addConnectionInterface();
		addPageInfoType();
		addEdgeConnectionAndApplyNodeInterface();
	}

	/**
	 * Adds the <I>Node</I> interfaces The <I>Node</I> is defined in the
	 * <A HREF="https://relay.dev/graphql/connections.htm">Relay Connection specification</A>. If a <I>Node</I>
	 * interface already exists in the schema, it is checked to be compliant to this specification. If not, an exception
	 * is thrown.
	 * 
	 * @throws RuntimeException
	 *             Thrown if a <I>Node</I> interface already exists, but is not compliant with the Relay Connection
	 *             specification
	 */
	void addNodeInterface() {
		final String NODE = "Node";
		boolean found = false;
		for (InterfaceType i : documentParser.getInterfaceTypes()) {
			if (NODE.equals(i.getName())) {
				// We've found it.
				found = true;

				// Let's check its properties
				if (i.getMemberOfUnions().size() != 0) {
					throw new RuntimeException("The " + NODE
							+ " interface already exists, but is not compliant with the Relay specification (member of unions)");
				}
				if (i.getFields().size() != 1) {
					throw new RuntimeException("The " + NODE
							+ " interface already exists, but is not compliant with the Relay specification (it should contain exactly one field)");
				}
				if (!"id".equals(i.getFields().get(0).getName())) {
					throw new RuntimeException("The " + NODE
							+ " interface already exists, but is not compliant with the Relay specification (it should contain only the 'id' field)");
				}
				if (!"ID".equals(i.getFields().get(0).getGraphQLTypeName())) {
					throw new RuntimeException("The " + NODE
							+ " interface already exists, but is not compliant with the Relay specification (it should contain only the 'id' field, of the 'ID' type)");
				}
				if (!i.getFields().get(0).isId()) {
					throw new RuntimeException("The " + NODE
							+ " interface already exists, but is not compliant with the Relay specification (it should contain only the 'id' field, that is an identifier)");
				}
				if (i.getFields().get(0).isList()) {
					throw new RuntimeException("The " + NODE
							+ " interface already exists, but is not compliant with the Relay specification (it should contain only the 'id' field, that is not a list)");
				}
				if (!i.getFields().get(0).isMandatory()) {
					throw new RuntimeException("The " + NODE
							+ " interface already exists, but is not compliant with the Relay specification (it should contain only the 'id' field, that is mandatory)");
				}
				if (i.getRequestType() != null) {
					throw new RuntimeException("The " + NODE
							+ " interface already exists, but is not compliant with the Relay specification (it should not be a query/mutation/subscription)");
				}
				if (i.isInputType()) {
					throw new RuntimeException("The " + NODE
							+ " interface already exists, but is not compliant with the Relay specification (it should not be an input type)");
				}

				// Ok, this interface is compliant. We're done.
				node = i;
				break;
			}
		}

		if (!found) {
			// The interface Node has not been found. But this name can (and may not) be used for another type.
			if (documentParser.getType(NODE, false) != null) {
				throw new RuntimeException("A " + NODE + " type already exists. This prevents to create the " + NODE
						+ " interface, as described in this article: https://dev.to/mikemarcacci/intermediate-interfaces-generic-utility-types-in-graphql-50e8");

			}

			// We're in the standard case: the interface doesn't exist in the given schema(s). Let's define it.
			node = new InterfaceType(NODE, configuration);
			// Adding the id field toe the Node interface
			FieldImpl f = FieldImpl.builder().name("id").graphQLTypeName("ID").id(true).mandatory(true).owningType(node)
					.documentParser(documentParser).build();
			node.getFields().add(f);

			documentParser.getInterfaceTypes().add(node);
			documentParser.getTypes().put(NODE, node);
		}

	}

	/**
	 * Adds the <I>Connection</I> interfaces. The <I>Connection</I> is described in the
	 * <A HREF="https://dev.to/mikemarcacci/intermediate-interfaces-generic-utility-types-in-graphql-50e8">generic
	 * utility types</A>, that leads to allow that an interface implements an interface in the GraphQL specification.If
	 * a <I>Connection</I> interface already exists in the schema, it is checked to be compliant to this specification.
	 * If not, an exception is thrown.
	 * 
	 * @throws RuntimeException
	 *             Thrown if a <I>Connection</I> interface already exists, but is not compliant with the above
	 *             description
	 */
	void addConnectionInterface() {
		final String CONNECTION = "Connection";
		boolean found = false;
		for (InterfaceType i : documentParser.getInterfaceTypes()) {
			if (CONNECTION.equals(i.getName())) {
				// We've found it.
				found = true;

				// Let's check its properties
				if (i.getMemberOfUnions().size() != 0) {
					throw new RuntimeException("The " + CONNECTION
							+ " interface already exists, but is not compliant with the specification (is is a member of unions)");
				}
				if (i.getRequestType() != null) {
					throw new RuntimeException("The " + CONNECTION
							+ " interface already exists, but is not compliant with the Relay specification (it should not be a query/mutation/subscription)");
				}
				if (i.isInputType()) {
					throw new RuntimeException("The " + CONNECTION
							+ " interface already exists, but is not compliant with the Relay specification (it should not be an input type)");
				}
				if (i.getFields().size() != 2) {
					throw new RuntimeException("The " + CONNECTION
							+ " interface already exists, but is not compliant with the specification (it should contain exactly two fields)");
				}
				/////////////// The pageInfo field
				int j = 0;
				if (!"pageInfo".equals(i.getFields().get(j).getName())) {
					throw new RuntimeException("The " + CONNECTION
							+ " interface already exists, but is not compliant with the Relay specification (the first field should be the 'pageInfo' field)");
				}
				if (!"PageInfo".equals(i.getFields().get(j).getGraphQLTypeName())) {
					throw new RuntimeException("The " + CONNECTION
							+ " interface already exists, but is not compliant with the Relay specification (the 'pageInfo' field must be of the 'PageInfo' type)");
				}
				if (i.getFields().get(j).isId()) {
					throw new RuntimeException("The " + CONNECTION
							+ " interface already exists, but is not compliant with the Relay specification (the 'pageInfo' field may not be an identifier)");
				}
				if (i.getFields().get(j).isList()) {
					throw new RuntimeException("The " + CONNECTION
							+ " interface already exists, but is not compliant with the Relay specification (the 'pageInfo' field may not be a list)");
				}
				if (!i.getFields().get(j).isMandatory()) {
					throw new RuntimeException("The " + CONNECTION
							+ " interface already exists, but is not compliant with the Relay specification (the 'pageInfo' field must be mandatory)");
				}
				/////////////// The edges field
				j += 1;
				if (!"edges".equals(i.getFields().get(j).getName())) {
					throw new RuntimeException("The " + CONNECTION
							+ " interface already exists, but is not compliant with the Relay specification (the first field should be the 'edges' field)");
				}
				if (!"Edge".equals(i.getFields().get(j).getGraphQLTypeName())) {
					throw new RuntimeException("The " + CONNECTION
							+ " interface already exists, but is not compliant with the Relay specification (the 'edges' field must be of the 'PageInfo' type)");
				}
				if (i.getFields().get(j).isId()) {
					throw new RuntimeException("The " + CONNECTION
							+ " interface already exists, but is not compliant with the Relay specification (the 'edges' field may not be an identifier)");
				}
				if (!i.getFields().get(j).isList()) {
					throw new RuntimeException("The " + CONNECTION
							+ " interface already exists, but is not compliant with the Relay specification (the 'edges' field must be a list)");
				}
				// No constraint on the edges field, about mandatory

				// Ok, this interface is compliant. We're done.
				break;
			}
		}

		if (!found) {
			// The interface Connection has not been found. But this name can (and may not) be used for another type.
			if (documentParser.getType(CONNECTION, false) != null) {
				throw new RuntimeException("A " + CONNECTION + " type already exists. This prevents to create the "
						+ CONNECTION
						+ " interface, as described in this article: https://dev.to/mikemarcacci/intermediate-interfaces-generic-utility-types-in-graphql-50e8");
			}

			// We're in the standard case: the interface doesn't exist in the given schema(s). Let's define it.
			InterfaceType i = new InterfaceType(CONNECTION, configuration);
			// Adding the id field toe the Node interface
			FieldImpl edges = FieldImpl.builder().name("edges").graphQLTypeName("Edge").list(true).owningType(i)
					.documentParser(documentParser).build();
			FieldImpl pageInfo = FieldImpl.builder().name("pageInfo").graphQLTypeName("PageInfo").mandatory(true)
					.owningType(i).documentParser(documentParser).build();
			i.getFields().add(edges);
			i.getFields().add(pageInfo);

			documentParser.getInterfaceTypes().add(i);
			documentParser.getTypes().put(CONNECTION, i);
		}

	}

	/**
	 * Adds the <I>Edge</I> interface. The <I>Edge</I> is defined in the
	 * <A HREF="https://dev.to/mikemarcacci/intermediate-interfaces-generic-utility-types-in-graphql-50e8">generic
	 * utility types blog entry</A>, that leads to allow that an interface implements an interface in the GraphQL
	 * specification.If an <I>Edge</I> interface already exists in the schema, it is checked to be compliant to this
	 * specification. If not, an exception is thrown.
	 * 
	 * @throws RuntimeException
	 *             Thrown if a <I>Edge</I> interface already exists, but is not compliant with the specification
	 */
	void addEdgeInterface() {
		final String EDGE = "Edge";
		boolean found = false;
		for (InterfaceType i : documentParser.getInterfaceTypes()) {
			if (EDGE.equals(i.getName())) {
				// We've found it.
				found = true;

				// Let's check its properties
				if (i.getMemberOfUnions().size() != 0) {
					throw new RuntimeException("The " + EDGE
							+ " interface already exists, but is not compliant with the Relay specification (member of unions)");
				}
				if (i.getRequestType() != null) {
					throw new RuntimeException("The " + EDGE
							+ " interface already exists, but is not compliant with the Relay specification (it should not be a query/mutation/subscription)");
				}
				if (i.isInputType()) {
					throw new RuntimeException("The " + EDGE
							+ " interface already exists, but is not compliant with the Relay specification (it should not be an input type)");
				}
				if (i.getFields().size() != 2) {
					throw new RuntimeException("The " + EDGE
							+ " interface already exists, but is not compliant with the Relay specification (it should contain exactly two fields)");
				}
				/////////////////////
				// The cursor field
				int j = 0;
				if (!"cursor".equals(i.getFields().get(j).getName())) {
					throw new RuntimeException("The " + EDGE
							+ " interface already exists, but is not compliant with the Relay specification (the first field should be the 'cursor' field)");
				}
				if (!"String".equals(i.getFields().get(j).getGraphQLTypeName())) {
					throw new RuntimeException("The " + EDGE
							+ " interface already exists, but is not compliant with the Relay specification (the 'cursor' field should be a String field)");
				}
				if (i.getFields().get(j).isId()) {
					throw new RuntimeException("The " + EDGE
							+ " interface already exists, but is not compliant with the Relay specification (the 'cursor' field should not be an identifier)");
				}
				if (i.getFields().get(j).isList()) {
					throw new RuntimeException("The " + EDGE
							+ " interface already exists, but is not compliant with the Relay specification (the 'cursor' field should not be a list)");
				}
				if (!i.getFields().get(j).isMandatory()) {
					throw new RuntimeException("The " + EDGE
							+ " interface already exists, but is not compliant with the Relay specification (the 'cursor' field should be mandatory)");
				}
				/////////////////////
				// The node field
				j += 1;
				if (!"node".equals(i.getFields().get(j).getName())) {
					throw new RuntimeException("The " + EDGE
							+ " interface already exists, but is not compliant with the Relay specification (the second field should be the 'node' field)");
				}
				if (!"Node".equals(i.getFields().get(j).getGraphQLTypeName())) {
					throw new RuntimeException("The " + EDGE
							+ " interface already exists, but is not compliant with the Relay specification (the 'node' field should be of type [Edge])");
				}
				if (i.getFields().get(j).isId()) {
					throw new RuntimeException("The " + EDGE
							+ " interface already exists, but is not compliant with the Relay specification (the 'node' field should not be an identifier)");
				}
				if (i.getFields().get(j).isList()) {
					throw new RuntimeException("The " + EDGE
							+ " interface already exists, but is not compliant with the Relay specification (the 'node' field should not be a list)");
				}
				if (i.getFields().get(j).isMandatory()) {
					throw new RuntimeException("The " + EDGE
							+ " interface already exists, but is not compliant with the Relay specification (the 'node' field should not be mandatory)");
				}

				// Ok, this interface is compliant. We're done.
				break;
			}
		}

		if (!found) {
			// The interface Edge has not been found. But this name can (and may not) be used for another type.
			if (documentParser.getType(EDGE, false) != null) {
				throw new RuntimeException("A " + EDGE + " type already exists. This prevents to create the " + EDGE
						+ " interface, as described in this article: https://dev.to/mikemarcacci/intermediate-interfaces-generic-utility-types-in-graphql-50e8");
			}

			// We're in the standard case: the interface doesn't exist in the given schema(s). Let's define it.
			InterfaceType i = new InterfaceType(EDGE, configuration);
			// Adding the id field toe the Node interface
			FieldImpl cursor = FieldImpl.builder().name("cursor").graphQLTypeName("String").mandatory(true)
					.owningType(i).documentParser(documentParser).build();
			FieldImpl node = FieldImpl.builder().name("node").graphQLTypeName("Node").owningType(i)
					.documentParser(documentParser).build();
			i.getFields().add(cursor);
			i.getFields().add(node);

			documentParser.getInterfaceTypes().add(i);
			documentParser.getTypes().put(EDGE, i);
		}

	}

	/**
	 * Adds the <I>PageInfo</I> type, as defined in the <A HREF="https://relay.dev/graphql/connections.htm">Relay
	 * Connection specification</A>. If a <I>PageInfo</I> type already exists in the schema, it is checked to be
	 * compliant to this specification. If not, an exception is thrown.
	 * 
	 * @throws RuntimeException
	 *             Thrown if a <I>PageInfo</I> type already exists, but is not compliant with the Relay Connection
	 *             specification
	 */
	void addPageInfoType() {
		final String PAGE_INFO = "PageInfo";
		Type o = documentParser.getType(PAGE_INFO, false);

		if (o == null) {
			// PageInfo is not defined in the GraphQL source schema. Let's add it.
			ObjectType pageInfo = new ObjectType(PAGE_INFO, configuration);
			// Adding the PageInfo's fields
			pageInfo.getFields().add(FieldImpl.builder().name("hasNextPage").graphQLTypeName("Boolean").mandatory(true)
					.owningType(pageInfo).documentParser(documentParser).build());
			pageInfo.getFields().add(FieldImpl.builder().name("hasPreviousPage").graphQLTypeName("Boolean")
					.mandatory(true).owningType(pageInfo).documentParser(documentParser).build());
			pageInfo.getFields().add(FieldImpl.builder().name("startCursor").graphQLTypeName("String").mandatory(true)
					.owningType(pageInfo).documentParser(documentParser).build());
			pageInfo.getFields().add(FieldImpl.builder().name("endCursor").graphQLTypeName("String").mandatory(true)
					.owningType(pageInfo).documentParser(documentParser).build());
			//
			documentParser.getObjectTypes().add(pageInfo);
			documentParser.getTypes().put(PAGE_INFO, pageInfo);
		} else if (!(o instanceof ObjectType)) {
			throw new RuntimeException(
					"A " + PAGE_INFO + " item already exists in the GraphQL schema, but it isn't a GraphQL type");
		} else {
			// PageInfo is defined in the GraphQL source schema. Let's check that it is compliant to the relay
			// specification.
			ObjectType pageInfo = (ObjectType) o;
			if (pageInfo.getMemberOfUnions().size() != 0) {
				throw new RuntimeException("The " + PAGE_INFO
						+ " type already exists, but is not compliant with the Relay specification (member of unions)");
			}
			if (pageInfo.getFields().size() < 4) {
				throw new RuntimeException("The " + PAGE_INFO
						+ " type already exists, but is not compliant with the Relay specification (it should contain exactly at least four fields)");
			}
			checkField(pageInfo, "hasNextPage", false, false, true, false, "Boolean", 0);
			checkField(pageInfo, "hasPreviousPage", false, false, true, false, "Boolean", 0);
			checkField(pageInfo, "startCursor", false, false, true, false, "String", 0);
			checkField(pageInfo, "endCursor", false, false, true, false, "String", 0);
		}
	}

	/**
	 * This method searches for all interfaces, then type fields that have been marked by the RelayConnection directive,
	 * then, for each type, say <I>Xxx</I>:
	 * <UL>
	 * <LI>Creates the XxxEdge type</LI>
	 * <LI>Creates the XxxConnection type</LI>
	 * <LI>Add the <I>Node</I> interface as implemented by the Xxx type</LI>
	 * <LI>Update each field that is marked with the <I>&#064;RelayConnection</I> interface, to change its type by the
	 * TypeConnection type instead</I>
	 * </UL>
	 */
	void addEdgeConnectionAndApplyNodeInterface() {

		// A first step is to control that the schema is correct:
		// * Standard case: a type's field has the @RelayConnection directive, and this field doesn't come from an
		// interface.
		// * More complex case: an interface's field has the @RelayConnection directive. We'll have to loop into
		// each interface or type that implements this interface, and check that the relevant has the @RelayConnection
		// directive. If not, a warning is issued.
		// * Erroneous case: an interface or a type's field has the @RelayConnection directive, this field comes from an
		// implemented interface, and the relevant field's interface doesn't have this directive.
		//
		// To do this:
		//
		// Step 1: identify all fields that have been marked with the @RelayConnection directive
		// Step 2: for each of these fields, whether it is owned by an object or an interface, check if it inherits from
		// an interface field. If yes, add the check that the field in the implemented interface is also marked with the
		// @RelayConnection directive. If no, raise an error.
		// Step 3: for fields of an interface that is marked with the @RelayConnection directive, checks that the
		// implemented fields (that this: field if the same name in types that implement this interface) are also marked
		// with the @RelayConnection directive. If not, a warning is issued, but the field is still added to the list of
		// fields that implements the @RelayConnection directive
		// Step 4: Identify the list of types and interfaces for which the Edge and Connection and Node interface should
		// be done.
		// Step 5: Actually implement the edges, connections and mark these types/interfaces with the Node interface
		// Step 6: Update every field that implements the RelayConnection and change its type by the relevant
		// XxxConnection object. The list of field to update is: all fields marked by the @RelayConnection, or that
		// implements a field that is marked by the directive (see step3). This @RelayConnection directive must also be
		// removed from the final schema

		List<Field> fields = new ArrayList<>();
		int nbErrors = 0;

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Step 1: identify all fields in types and interfaces that have been marked with the @RelayConnection directive
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		Stream.concat(documentParser.getObjectTypes().stream(), documentParser.getInterfaceTypes().stream())
				.forEach((type) -> {
					for (Field f : type.getFields()) {
						// Is this field marked by the @RelayConnection directive?
						for (AppliedDirective d : f.getAppliedDirectives()) {
							if (d.getDirective().getName().equals("RelayConnection")) {
								// This Field has the @RelayConnection directive applied
								//
								// It must be a list
								if (!f.isList()) {
									throw new RuntimeException("The " + f.getOwningType().getName() + "." + f.getName()
											+ " field has the @RelayConnection directive applied, but is not a list. The @RelayConnection directive may only be applied on lists.");
								}
								//
								// InputType may not have relay connection fields
								if (((ObjectType) f.getOwningType()).isInputType()) {
									throw new RuntimeException("The " + f.getOwningType().getName() + "." + f.getName()
											+ " field has the @RelayConnection directive applied. But input type may not have fields to which the @RelayConnection directive is applied.");
								}
								//
								// Everything is Ok. Let's go
								fields.add(f);
								break;
							}
						} // for(AppliedDirective)
					} // for(Field))
				});

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Step 2: for each of these fields, whether it is owned by an object or an interface, check if it inherits from
		// an interface field. If yes, add the check that the field in the implemented interface is also marked with the
		// @RelayConnection directive
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		for (Field f : fields) {
			// Let's find the list of interfaces from which this field is inherited (a given field can be defined in
			// more than one implemented interfaces).
			// We actually don't capture the interface, but the field's interface in the "super" interface(s) that match
			// the current looping field
			for (Field fieldInheritedFrom : getFieldInheritedFrom(f)) {
				// This field must be marked by the @RelayConnection directive. So/and it must exist in the fields list
				if (!fields.contains(fieldInheritedFrom)) {
					configuration.getLog()
							.error("The field " + f.getName() + " of the "
									+ (f.getOwningType() instanceof InterfaceType ? "interface" : "type") + " "
									+ f.getOwningType().getName()
									+ " has the directive @RelayConnection applied. But it inherits from the interface "
									+ fieldInheritedFrom.getOwningType().getName()
									+ ", in which this field doesn't have the directive @RelayConnection applied");
					nbErrors += 1;
				}
			} // for(getFieldInheritedFrom())
		} // for(fields)

		if (nbErrors > 0) {
			throw new RuntimeException(nbErrors
					+ " error(s) was(were) found in this schema, linked with the @RelayConnection schema. Please check the logged errors.");
		}

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Step 3: for fields of an interface that is marked with the @RelayConnection directive, checks that the
		// implemented fields (that this: field if the same name in types that implement this interface) are also marked
		// with the @RelayConnection directive. If not, a warning is issued, but the field is still added to the list of
		// fields that implements the @RelayConnection directive
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		List<Field> fieldsToAdd = new ArrayList<>();
		for (Field field : fields) {
			if (field.getOwningType() instanceof InterfaceType) {
				for (Field inheritedField : getInheritedFields(field)) {
					boolean found = false;
					for (AppliedDirective d : inheritedField.getAppliedDirectives()) {
						if (d.getDirective().getName().equals("RelayConnection")) {
							found = true;
							break;
						}
					} // for(getAppliedDirectives)

					if (!found) {
						// We've found an object's field, inherited from an interface in which it is marked by the
						// @RelayConnection directive. But this object's field is not marked with this directive. It's
						// strange, but is generally Ok. So we display a warning. And we add this field to the list of
						// field that must implement the relay connection.
						configuration.getLog()
								.warn("The field " + inheritedField.getOwningType().getName() + "."
										+ inheritedField.getName() + " implements (directly or indirectly) the "
										+ field.getOwningType().getName() + "." + field.getName()
										+ " field, but does not have the @RelayConnection directive");
						// As we may not update a list, while we're looping in it, we create another list, that we'll be
						// added afterward.
						fieldsToAdd.add(inheritedField);
					} // if (!found)
				} // for(getInheritedFields)
			} // if
		} // for(fields)
		fields.addAll(fieldsToAdd);

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Step 4: Identify the list of types and interfaces for which the Edge and Connection and Node interface should
		// be done.
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		Set<String> connectionTypeNames = new HashSet<>();
		for (Field f : fields) {
			connectionTypeNames.add(f.getGraphQLTypeName());
		}

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Step 5: Actually implement the edges, connections and mark these types/interfaces with the Node interface
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		for (String typeName : connectionTypeNames) {
			Type type = documentParser.getType(typeName);
			// Add the Node interface to the implemented interface, if it's not already the case
			if (0 == ((ObjectType) type).getImplementz().stream()
					.filter((interfaceName) -> interfaceName.equals("Node")).count()) {
				// This type didn't implement the Node interface :
				// 1) We add the Node interface to its list of implemented interfaces.
				((ObjectType) type).getImplementz().add("Node");
				// 2) We add this type to the list of types that are implemented by the Node interface
				node.getImplementingTypes().add((ObjectType) type);
			}

			generateConnectionType(type);
			generateEdgeType(type);
		}

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Step 6: Update every field that implements the RelayConnection and change its type by the relevant
		// XxxConnection object. The list of field to update is: all fields marked by the @RelayConnection, or that
		// implements a field that is marked by the directive (see step3). This @RelayConnection directive must also be
		// removed from the final schema.
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		for (Field f : fields) {
			((FieldImpl) f).setGraphQLTypeName(f.getGraphQLTypeName() + "Connection");
			((FieldImpl) f).setList(false);
			((FieldImpl) f).setMandatory(true);
			((FieldImpl) f).setItemMandatory(false);// No more sense, for item lists
		}
	}

	void generateConnectionType(Type type) {
		final String connectionTypeName = type.getName() + "Connection";
		// The XxxEdge type should not already exist
		Type xxxConnection = documentParser.getType(connectionTypeName, false);
		if (xxxConnection != null) {
			if (!(xxxConnection instanceof ObjectType)) {
				// As ObjectType is a superclass of InterfaceType, xxxConnection is neither a GraphQL Object neither a
				// GraphQL interface
				throw new RuntimeException("The " + connectionTypeName
						+ " already exist in the provided GraphQL schema. But it is not an Object, nor an interface.");
			}
			// The type exist, let's check if it is compliant with the Relay connection specification: it must contain
			// at least the edges and the pageInfo fields.
			if (xxxConnection.getFields().size() < 2) {
				throw new RuntimeException("The " + connectionTypeName
						+ " already exist in the provided GraphQL schema. But it is not compliant with the Relay connection specification: it should have at least the edges and the pageInfo fields");
			}
			boolean edgesFound = false;
			boolean pageInfoFound = false;
			for (Field f : xxxConnection.getFields()) {
				switch (f.getName()) {
				case "edges":
					if (f.getType().getName().equals(type.getName() + "Edge") && !f.isMandatory() && f.isList()) {
						// This field is compliant to the Relay specification
						edgesFound = true;
					}
					break;
				case "pageInfo":
					if (f.getType().getName().equals("PageInfo") && f.isMandatory() && !f.isList()) {
						// This field is compliant to the Relay specification
						pageInfoFound = true;
					}
					break;
				}
			} // for
			if (!edgesFound || !pageInfoFound) {
				throw new RuntimeException("The " + connectionTypeName
						+ " already exist in the provided GraphQL schema. But it is not compliant with the Relay connection specification: it must have at least these fields:"
						+ " edged(not mandatory, list of the " + type.getName()
						+ " type) and pageInfo (mandatory, PageInfo type) fields");
			}
		} else {
			// Standard case: the XxxConnection type doesn't exist. Let's create it.

			ObjectType xxxConnectionObject;
			if (type instanceof InterfaceType) {
				xxxConnectionObject = new InterfaceType(connectionTypeName, configuration);
				documentParser.getInterfaceTypes().add((InterfaceType) xxxConnectionObject);
			} else {
				xxxConnectionObject = new ObjectType(connectionTypeName, configuration);
				documentParser.getObjectTypes().add(xxxConnectionObject);
			}
			documentParser.getTypes().put(connectionTypeName, xxxConnectionObject);

			FieldImpl edges = FieldImpl.builder().name("edges").graphQLTypeName(type.getName() + "Edge").list(true)
					.documentParser(documentParser).owningType(xxxConnectionObject).build();
			xxxConnectionObject.getFields().add(edges);
			FieldImpl pageInfo = FieldImpl.builder().name("pageInfo").graphQLTypeName("PageInfo").mandatory(true)
					.documentParser(documentParser).owningType(xxxConnectionObject).build();
			xxxConnectionObject.getFields().add(pageInfo);

			// The XxxConnection objects/interfaces will implement the Connection interface, once the Generic type are
			// managed here.
			// xxxConnectionObject.getImplementz().add("Connection");
		}
	}

	/**
	 * Adds the XxxEdge type for the given type into the current in-memory GraphQL model, according to the Relay
	 * Connection specification.
	 * 
	 * @param type
	 * @throws RuntimeException
	 *             If the XxxEdge type already exists and is not compliant with the Relay Connection specification
	 */
	void generateEdgeType(Type type) {
		final String edgeTypeName = type.getName() + "Edge";
		// The XxxEdge type should not already exist
		Type xxxEdge = documentParser.getType(edgeTypeName, false);
		if (xxxEdge != null) {
			if (!(xxxEdge instanceof ObjectType)) {
				// As ObjectType is a superclass of InterfaceType, xxxEdge is neither a GraphQL Object neither a GraphQL
				// interface
				throw new RuntimeException("The " + edgeTypeName
						+ " already exist in the provided GraphQL schema. But it is not an Object, nor an interface.");
			}
			// The type exist, let's check if it is compliant with the Relay connection specification: it must contain
			// at least the node and the cursor fields.
			if (xxxEdge.getFields().size() < 2) {
				throw new RuntimeException("The " + edgeTypeName
						+ " already exist in the provided GraphQL schema. But it is not compliant with the Relay connection specification: it should have at least the node and the cursor fields");
			}
			boolean nodeFound = false;
			boolean cursorFound = false;
			for (Field f : xxxEdge.getFields()) {
				switch (f.getName()) {
				case "node":
					if (f.getType().getName().equals(type.getName()) && !f.isMandatory() && !f.isList()) {
						// This field is compliant to the Relay specification
						nodeFound = true;
					}
					break;
				case "cursor":
					if (f.getType().getName().equals("String") && f.isMandatory() && !f.isList()) {
						// This field is compliant to the Relay specification
						cursorFound = true;
					}
					break;
				}
			} // for
			if (!nodeFound || !cursorFound) {
				throw new RuntimeException("The " + edgeTypeName
						+ " already exist in the provided GraphQL schema. But it is not compliant with the Relay connection specification: it must have at least these two fields: node (not mandatory, of the "
						+ type.getName() + " type) and cursor (mandatory, String)");
			}
		} else {
			// Standard case: the XxxEdge type doesn't exist. Let's create it.

			ObjectType xxxEdgeObject;
			if (type instanceof InterfaceType) {
				xxxEdgeObject = new InterfaceType(edgeTypeName, configuration);
				documentParser.getInterfaceTypes().add((InterfaceType) xxxEdgeObject);
			} else {
				xxxEdgeObject = new ObjectType(edgeTypeName, configuration);
				documentParser.getObjectTypes().add(xxxEdgeObject);
			}
			documentParser.getTypes().put(edgeTypeName, xxxEdgeObject);

			FieldImpl node = FieldImpl.builder().name("node").graphQLTypeName(type.getName())
					.documentParser(documentParser).owningType(xxxEdgeObject).build();
			xxxEdgeObject.getFields().add(node);
			FieldImpl cursor = FieldImpl.builder().name("cursor").graphQLTypeName("String").mandatory(true)
					.documentParser(documentParser).owningType(xxxEdgeObject).build();
			xxxEdgeObject.getFields().add(cursor);

			// The XxxEdge objects/interfaces must implement the Edge interface, once the Generic type are
			// managed here.
			// xxxEdgeObject.getImplementz().add("Edge");
		}
	}

	/**
	 * Retrieve the list of fields, from which the given field inherits. That is: all fields of the same name than the
	 * given field, that are owned by interface implemented by the given field's type (whether it's an interface or a
	 * type)
	 * 
	 * @param field
	 * @return
	 */
	List<Field> getFieldInheritedFrom(Field field) {
		List<Field> ret = new ArrayList<>();
		for (String interfaceName : ((ObjectType) field.getOwningType()).getImplementz()) {
			// getImplementz() returns unions and interfaces:
			Type type = documentParser.getType(interfaceName);
			if (type instanceof InterfaceType) {
				InterfaceType i = (InterfaceType) type;
				for (Field f : i.getFields()) {
					if (f.getName().equals(field.getName())) {
						// An implemented interface of the field's owning type contains a field of the same name.
						// So, "field" is inherited from this interface, through the 'f' field.
						ret.add(f);
						break;
					}
				} // for(Field f)
			} // if(type)
		} // for(interfaceName)
		return ret;
	}

	/**
	 * Retrieve the list of fields, that are inherited from the given field. That is: all fields of the same name than
	 * the given field, that are owned by a type or an interface that implements the given field's type. As a GraphQL
	 * type may not inherit from another type, the given field must be owned by an interface.
	 * 
	 * @param field
	 * @return Never null. The list of all inherited fields. If the field is owned by a type (not an interface), this
	 *         method returns an empty list.
	 */
	List<Field> getInheritedFields(Field field) {
		List<Field> ret = new ArrayList<>();
		if (field.getOwningType() instanceof InterfaceType) {
			// Search for all types that implements this interface
			for (Type type : documentParser.getTypes().values()) {
				if (type instanceof ObjectType || type instanceof InterfaceType) {
					ObjectType o = (ObjectType) type;
					// Does this type implement the field's interface?
					for (String i : o.getImplementz()) {
						if (i.equals(field.getOwningType().getName())) {
							// Let's find the right field
							for (Field f : o.getFields()) {
								if (f.getName().equals(field.getName())) {
									// f inherits from field.
									ret.add(f);
									// If type is an interface, we need to iterate once more.
									if (type instanceof InterfaceType) {
										ret.addAll(getInheritedFields(f));
									}
								}
							} // for(Field)
						} // if(interface name)
					} // for(getImplementz())
				} // if(instanceof)
			} // for(type)
		}
		return ret;
	}

	/**
	 * Checks that a field of the given fieldName is compliant with the given properties
	 * 
	 * @param type
	 *            The type that should contain this field
	 * @param fieldName
	 *            The name for the field to check
	 * @param id
	 *            Should this field be an id?
	 * @param list
	 *            Should this field be a list?
	 * @param mandatory
	 *            Should this field be mandatory?
	 * @param itemMandatory
	 *            Only when this field is a list, should its item be mandatory?
	 * @param typeName
	 *            The name of the type, that this field should have
	 * @param nbParameters
	 *            The number of expected parameters for this field
	 * @throws RuntimeException
	 *             Thrown if the field is not found, or a field of the same name is found, but has different properties
	 */
	private void checkField(ObjectType type, String fieldName, boolean id, boolean list, boolean mandatory,
			Boolean itemMandatory, String typeName, int nbParameters) {
		for (Field f : type.getFields()) {
			if (f.getName().equals(fieldName)) {
				// We've found the field, let's check its properties
				if (id != f.isId()) {
					throw new RuntimeException("The value for the isId() property of the field '" + fieldName
							+ "' of the type '" + type.getName() + "' is expected to be " + id + " but is " + f.isId());
				}
				if (list != f.isList()) {
					throw new RuntimeException(
							"The value for the isList() property of the field '" + fieldName + "' of the type '"
									+ type.getName() + "' is expected to be " + list + " but is " + f.isList());
				}
				if (mandatory != f.isMandatory()) {
					throw new RuntimeException("The value for the isMandatory() property of the field '" + fieldName
							+ "' of the type '" + type.getName() + "' is expected to be " + mandatory + " but is "
							+ f.isMandatory());
				}
				if (f.isList() && itemMandatory != null && (itemMandatory != f.isItemMandatory())) {
					throw new RuntimeException("The value for the isItemMandatory() property of the field '" + fieldName
							+ "' of the type '" + type.getName() + "' is expected to be " + itemMandatory + " but is "
							+ f.isItemMandatory());
				}
				if (!typeName.equals(f.getGraphQLTypeName())) {
					throw new RuntimeException("The type of the field '" + fieldName + "' of the type '"
							+ type.getName() + "' is expected to be " + typeName + " but is " + f.getGraphQLTypeName());
				}
				if (f.getInputParameters().size() != nbParameters) {
					throw new RuntimeException("The number of input parameters for the field '" + fieldName
							+ "' of the type '" + type.getName() + "' is expected to be " + nbParameters + " but is "
							+ f.getInputParameters().size());
				}

				// If we get there, then the type is compliant: we're done
				return;
			} // if (f.getName().equals(fieldName))
		} // for

		throw new RuntimeException(
				"No field of name '" + fieldName + "' has been found in the type '" + type.getName() + "'");
	}
}
