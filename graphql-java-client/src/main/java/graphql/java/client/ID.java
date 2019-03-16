package graphql.java.client;

public class ID {

	/** The id String. May not be null */
	private String id;

	/** Default constructor. It should not be used. It's only here for deserialization */
	public ID() {
		id = null;
	}

	/**
	 * Standard constructor. It should not be used. It's only here for deserialization
	 * 
	 * @param id
	 *            The value for this ID. May not be null
	 * @throws NullPointerException
	 *             If the given id is null, or is an empty String
	 */
	public ID(String id) {
		if (id == null || id.equals("")) {
			throw new NullPointerException("id may not be null");
		}
		this.id = id;
	}

	public String getId() {
		return id;
	}

	/**
	 * Setter for he id. It MAY NOT be called, as ID is an unmutable class. But we still need it for deserialization. If
	 * this setter is called when an id value has already been set, then an exception is thrown.
	 * 
	 * @param id
	 *            The value for this ID. May not be null
	 * @throws NullPointerException
	 *             If the given id is null, or is an empty String
	 * @throws RuntimeException
	 *             When a call is done on an already initialized ID
	 */
	public void setId(String id) {
		if (id == null || id.equals("")) {
			throw new NullPointerException("id may not be null");
		}
		if (this.id != null) {
			// This class is unmutable. This call is forbidden
			throw new RuntimeException("ID is unmutable. Its value may not change");
		}
		this.id = id;
	}

	@Override
	public String toString() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		// id may not be null
		if (o == null)
			return false;
		else if (!(o instanceof ID))
			return false;
		else
			return id.equals(((ID) o).getId());
	}
}
