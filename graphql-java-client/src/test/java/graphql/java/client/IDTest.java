package graphql.java.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IDTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testID() {
		ID id = new ID();
		assertNull(id.getId(), "Before initialization, id is null");
	}

	@Test
	void testIDString() {
		String aValue = "a value";
		ID id = new ID(aValue);
		assertEquals(aValue, id.getId(), "Before initialization, id is null");
	}

	@Test
	void testSetId() {
		// Preparation
		String aValue = "a value";
		ID id = new ID();
		assertNull(id.getId(), "Before initialization, id is null");

		// Go, go, go
		assertThrows(NullPointerException.class, () -> id.setId(null), "May not set a null id");
		assertThrows(NullPointerException.class, () -> id.setId(""), "May not set an empty id");
		id.setId(aValue);

		// Verification
		assertEquals(aValue, id.getId(), "Before initialization, id is null");

		// No more modication
		assertThrows(RuntimeException.class, () -> id.setId("another value"), "May not modify an initialized id");
	}

	@Test
	void testToString() {
		String aValue = "another  value";
		ID id = new ID(aValue);
		assertEquals(aValue, id.toString());
	}

	@Test
	void testEqualsObject() {
		ID id1 = new ID("first ID");
		ID id2 = new ID("second ID");
		ID id3 = id1;// Just to avoid a compilation warning below... :)

		assertTrue(id1 == id3, "id1==id1");
		assertFalse(id1 == id2, "id1==id2");
		assertFalse(id1.equals((Object) this), "id1==this");
		assertFalse(id1 == null, "id1==null");
		assertFalse(id1 == new ID(), "id1==new ID()");
		assertFalse(new ID() == id2, "new ID()==id2");
		assertFalse(id1 == new ID(id1.getId()), "id1==new ID(id1.getId())");
	}

}
