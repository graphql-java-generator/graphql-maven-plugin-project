package graphql.mavenplugin.language.impl;

import graphql.mavenplugin.language.Field;
import graphql.mavenplugin.language.Relation;
import graphql.mavenplugin.language.RelationType;
import graphql.mavenplugin.language.Type;
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
		return TypeUtil.getCamelCase(objectType.getName()) + TypeUtil.getPascalCase(field.getName());
	}

	/** {@inheritDoc} */
	@Override
	public Object getAnnotation() {
		return "	@Transient";
	}

}
