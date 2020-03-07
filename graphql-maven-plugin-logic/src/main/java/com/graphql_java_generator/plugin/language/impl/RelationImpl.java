package com.graphql_java_generator.plugin.language.impl;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.Relation;
import com.graphql_java_generator.plugin.language.RelationType;
import com.graphql_java_generator.plugin.language.Type;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * The class that implements the {@link Relation} interface
 * 
 * @author EtienneSF
 */
@Data
@RequiredArgsConstructor
public class RelationImpl implements Relation {

	/** @See {@link Relation#getObjectType()} */
	@NonNull
	Type objectType;

	/** @See {@link Relation#getField()} */
	@ToString.Exclude
	@NonNull
	public Field field;

	/** @See {@link Relation#getRelationType()} */
	@NonNull
	RelationType relationType;

	/**
	 * The default value is true.
	 * 
	 * @See {@link Relation#isOwnerSide()}
	 */
	boolean ownerSide = true;

	/**
	 * The default value is null
	 * 
	 * @See {@link Relation#getMappedyBy()}
	 */
	Field mappedyBy = null;

	/** {@inheritDoc} */
	@Override
	public String getDataFetcherName() {
		return GraphqlUtils.graphqlUtils.getCamelCase(objectType.getName())
				+ GraphqlUtils.graphqlUtils.getPascalCase(field.getName());
	}

	/** {@inheritDoc} */
	@Override
	public Object getAnnotation() {
		return "	@Transient";
	}

}
