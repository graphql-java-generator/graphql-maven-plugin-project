package graphql.mavenplugin.language;

/**
 * This class describes a relation between two objects or interfaces of the GraphQL Schema. It's used to generate the
 * GraphQLProvider class, which declares every Data Fetchers, and the GraphQlDataFetchers, which contains these Data
 * Fetchers.
 * 
 * @author EtienneSF
 */
public interface Relation {

	/**
	 * Returns the type of the object, which contains this field
	 * 
	 * @return
	 */
	Type getObjectType();

	/**
	 * Get the {@link Field} which handles this association. The {@link Field#getType()} is the target Object/Entity of
	 * the association.
	 * 
	 * @return
	 */
	Field getField();

	/**
	 * Returns the JPA relation type of this relation
	 * 
	 * @return
	 */
	RelationType getRelationType();

	/**
	 * Indicates whether the object type obtained by {@link #getObjectType()} is the owner or not of this relation.<BR/>
	 * As described in the JPA documentation: a bidirectional relationship has both an owning side and an inverse side.
	 * 
	 * @return
	 */
	boolean isOwnerSide();

	/**
	 * If this association is bidirectionnal, and this object is not the owner of the relation, this method returns the
	 * field in the Object/Entity that owns this relation.
	 * 
	 * @return
	 */
	Field getMappedyBy();

}
