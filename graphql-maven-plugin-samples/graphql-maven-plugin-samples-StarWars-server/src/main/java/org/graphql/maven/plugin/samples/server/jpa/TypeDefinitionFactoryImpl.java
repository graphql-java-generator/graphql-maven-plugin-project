package org.graphql.maven.plugin.samples.server.jpa;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import graphql.language.Definition;
import graphql.language.FieldDefinition;
import graphql.language.ListType;
import graphql.language.ObjectTypeDefinition;
import graphql.language.TypeName;

@Component
public class TypeDefinitionFactoryImpl /* implements TypeDefinitionFactory */ {

	public List<Definition<?>> create(final List<Definition<?>> existing) {
		List<Definition<?>> defs = new ArrayList<>();

		defs.add(ObjectTypeDefinition.newObjectTypeDefinition().name("Character")
				.fieldDefinition(new FieldDefinition("id", new TypeName("ID")))
				.fieldDefinition(new FieldDefinition("name", new TypeName("String")))
				.fieldDefinition(new FieldDefinition("friends", new ListType(new TypeName("Character"))))
				.fieldDefinition(new FieldDefinition("appearsIn", new ListType(new TypeName("Episode")))).build());

		// defs.add(ObjectTypeDefinition.newObjectTypeDefinition().name("Human")
		// .fieldDefinition(new FieldDefinition("id", new TypeName("ID")))
		// .fieldDefinition(new FieldDefinition("name", new TypeName("String")))
		// .fieldDefinition(new FieldDefinition("friends", new TypeName("Character")))
		// .fieldDefinition(new FieldDefinition("appearsIn", new TypeName("Episode")))
		// .fieldDefinition(new FieldDefinition("name", new TypeName("String"))).build());
		//
		// defs.add(ObjectTypeDefinition.newObjectTypeDefinition().name("Droid")
		// .fieldDefinition(new FieldDefinition("id", new TypeName("ID")))
		// .fieldDefinition(new FieldDefinition("name", new TypeName("String")))
		// .fieldDefinition(new FieldDefinition("friends", new TypeName("Character")))
		// .fieldDefinition(new FieldDefinition("appearsIn", new TypeName("Episode")))
		// .fieldDefinition(new FieldDefinition("primaryFunction", new TypeName("String"))).build());

		return defs;
	}

}
