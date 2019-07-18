package com.graphql_java_generator.plugin.language;

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
	public Type getObjectType();

	/**
	 * Get the {@link Field} which handles this association. The {@link Field#getType()} is the target Object/Entity of
	 * the association.
	 * 
	 * @return
	 */
	public Field getField();

	/**
	 * Returns the JPA relation type of this relation
	 * 
	 * @return
	 */
	public RelationType getRelationType();

	/**
	 * Indicates whether the object type obtained by {@link #getObjectType()} is the owner or not of this relation.<BR/>
	 * As described in the JPA documentation: a bidirectional relationship has both an owning side and an inverse side.
	 * 
	 * @return
	 */
	public boolean isOwnerSide();

	/**
	 * If this association is bidirectionnal, and this object is not the owner of the relation, this method returns the
	 * field in the Object/Entity that owns this relation.
	 * 
	 * @return
	 */
	public Field getMappedyBy();

	/**
	 * Returns the name of the DataFetcher to use for this relation.
	 * 
	 * @return
	 */
	public String getDataFetcherName();

	/**
	 * Retrieves the annotation to add to this field, when in server mode, to serve the relation that this field holds
	 * 
	 * @return The relevant annotation, or "" (an empty string) if there no annotation to add
	 */
	public Object getAnnotation();

}
