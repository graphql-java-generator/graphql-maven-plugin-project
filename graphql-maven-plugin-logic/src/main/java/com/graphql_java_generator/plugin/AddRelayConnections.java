/**
 * 
 */
package com.graphql_java_generator.plugin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.plugin.language.AppliedDirective;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.plugin.language.impl.FieldImpl;
import com.graphql_java_generator.plugin.language.impl.InterfaceType;
import com.graphql_java_generator.plugin.language.impl.ObjectType;

/**
 * This method add the relay capabilities into the GraphQL schema, as it has been read by {@link DocumentParser}. The
 * relay capabilities are specified in <A HREF="https://relay.dev/graphql/connections.htm">this doc</A>.<BR/>
 * This class will add the items described below in the currently read schema data. It is the possible to generate a
 * Java Really compatible code (by using the <I>graphql</I> goal/task), or to generate the Relay compatible GraphQL
 * schema (by using the <I>mergeSchema</I> task/goal). <BR/>
 * The items added to the in-memory read schema are:
 * <UL>
 * <LI>The <I>Node</I> interface in the GraphQL schema (if not already defined). If this interface is already defined in
 * the given schema, but is not compliant, then an error is thrown.</LI>
 * <LI>The <I>@RelayConnexion</I> directive definition in the GraphQL schema (if not already defined). If this is
 * already defined in the given schema, but is not compliant with the relay specification, then an error is thrown.</LI>
 * <LI>The <I>PageInfo</I> type in the GraphQL schema (if not already defined). If this type is already defined in the
 * given schema, but is not compliant with the relay specification, then an error is thrown.</LI>
 * <LI>All the Edge and Connection types in the GraphQL schema, for each type that is marked by the
 * <I>@RelayConnexion</I> directive.</LI>
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

	/** The main entry point of the class. It is responsible for doing what's described in the class documentation */
	public void addRelayConnections() {
		addNodeInterface();
		addPageInfoType();
		addEdgeConnectionAndApplyNodeInterface();
	}

	/**
	 * Adds the <I>Node</I> interface, as defined in the <A HREF="https://relay.dev/graphql/connections.htm">Relay
	 * Connection specification</A>. If a <I>Node</I> interface already exists in the schema, it is checked to be
	 * compliant to this specification. If not, an exception is thrown.
	 * 
	 * @throws RuntimeException
	 *             Thrown if a <I>Node</I> interface already exists, but is not compliant with the Relay Connection
	 *             specification
	 */
	private void addNodeInterface() {
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
				if ("id".equals(i.getFields().get(0).getName())) {
					throw new RuntimeException("The " + NODE
							+ " interface already exists, but is not compliant with the Relay specification (it should contain only the 'id' field)");
				}
				if ("ID".equals(i.getFields().get(0).getGraphQLTypeName())) {
					throw new RuntimeException("The " + NODE
							+ " interface already exists, but is not compliant with the Relay specification (it should contain only the 'id' field, of the 'ID' type)");
				}
				if (!i.getFields().get(0).isId()) {
					throw new RuntimeException("The " + NODE
							+ " interface already exists, but is not compliant with the Relay specification (it should contain only the 'id' field, that is an identified)");
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
				break;
			}
		}

		if (!found) {
			// The standard case: the interface doesn't exist in the given schema(s). Let's define it.
			InterfaceType i = new InterfaceType(NODE, configuration.getPackageName());
			// Adding the id field toe the Node interface
			FieldImpl f = FieldImpl.builder().name("id").graphQLTypeName("ID").id(true).mandatory(true).owningType(i)
					.build();
			i.getFields().add(f);

			documentParser.getInterfaceTypes().add(i);
			documentParser.getTypes().put(NODE, i);
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
	private void addPageInfoType() {
		final String PAGE_INFO = "PageInfo";
		Type o = documentParser.getType(PAGE_INFO, false);

		if (o == null) {
			// PageInfo is not defined in the GraphQL source schema. Let's add it.
			ObjectType pageInfo = new ObjectType(PAGE_INFO, configuration.getPackageName());
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

			if ("id".equals(pageInfo.getFields().get(0).getName())) {
				throw new RuntimeException("The " + PAGE_INFO
						+ " type already exists, but is not compliant with the Relay specification (it should contain only the 'id' field)");
			}
			if ("ID".equals(pageInfo.getFields().get(0).getGraphQLTypeName())) {
				throw new RuntimeException("The " + PAGE_INFO
						+ " type already exists, but is not compliant with the Relay specification (it should contain only the 'id' field, of the 'ID' type)");
			}
			if (!pageInfo.getFields().get(0).isId()) {
				throw new RuntimeException("The " + PAGE_INFO
						+ " type already exists, but is not compliant with the Relay specification (it should contain only the 'id' field, that is an identified)");
			}
			if (pageInfo.getFields().get(0).isList()) {
				throw new RuntimeException("The " + PAGE_INFO
						+ " type already exists, but is not compliant with the Relay specification (it should contain only the 'id' field, that is not a list)");
			}
			if (!pageInfo.getFields().get(0).isMandatory()) {
				throw new RuntimeException("The " + PAGE_INFO
						+ " type already exists, but is not compliant with the Relay specification (it should contain only the 'id' field, that is mandatory)");
			}
			if (pageInfo.getRequestType() != null) {
				throw new RuntimeException("The " + PAGE_INFO
						+ " type already exists, but is not compliant with the Relay specification (it should not be a query/mutation/subscription)");
			}
			if (pageInfo.isInputType()) {
				throw new RuntimeException("The " + PAGE_INFO
						+ " type already exists, but is not compliant with the Relay specification (it should not be an input type)");
			}
		}
	}

	/**
	 * This method searches for all interface, then type fields that have been marked by the RelayConnection directive,
	 * then, for each type, say <I>Xxx</I>:
	 * <UL>
	 * <LI>Creates the XxxEdge type</LI>
	 * <LI>Creates the XxxConnection type</LI>
	 * <LI>Add the <I>Node</I> interface as implemented by the Xxx type</LI>
	 * </UL>
	 */
	private void addEdgeConnectionAndApplyNodeInterface() {

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
		// Step 3: Identify the list of types and interfaces for which the Edge and Connection and Node interface should
		// be done.
		// Step 4: Actually implement the edges, connections and mark these types/interfaces with the Node interface

		List<Field> fields = new ArrayList<>();

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Step 1: identify all fields that have been marked with the @RelayConnection directive
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		for (InterfaceType i : documentParser.getInterfaceTypes()) {
			for (Field f : i.getFields()) {
				// Is this field marked by the @RelayConnection directive?
				for (AppliedDirective d : f.getAppliedDirectives()) {
					if (d.getDirective().getName().equals(f.getName())) {
						// This Field has the @RelayConnection directive applied
						fields.add(f);
						break;
					}
				} // for(AppliedDirective)
			} // for(Field)
		} // for(InterfaceType)

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Step 2: for each of these fields, whether it is owned by an object or an interface, check if it inherits from
		// an interface field. If yes, add the check that the field in the implemented interface is also marked with the
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		for (Field f : fields) {
			// Let's find the list of interfaces from which this field is inherited (a given field can be defined in
			// more than one implemented interfaces).
			// We actually don't capture the interface, but the field's interface in the "super" interface(s) that match
			// the current looping field
			List<Field> inheritedFrom = getFieldInheritedFrom(f);

		} // for(Field)

		// throw new RuntimeException("not finished");
	}

	/**
	 * Retrieve the list of fields, from which the given field inherits
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
