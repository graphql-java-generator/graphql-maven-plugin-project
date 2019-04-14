package graphql.mavenplugin.language;

import lombok.Data;

/**
 * The class that implements the {@link Relation} interface
 * 
 * @author EtienneSF
 */
@Data
public class RelationImpl implements Relation {

	/** @See {@link Relation#getObjectType()} */
	Type objectType;

	/** @See {@link Relation#getField()} */
	public Field field;

	/** @See {@link Relation#getRelationType()} */
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

}
