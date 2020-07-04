/**
 * 
 */
package com.graphql_java_generator.plugin.test.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.graphql_java_generator.plugin.test.helper.DeepComparator.ComparisonRule;
import com.graphql_java_generator.plugin.test.helper.DeepComparator.DiffenceType;
import com.graphql_java_generator.plugin.test.helper.DeepComparator.Difference;

/**
 * Test for the {@link DeepComparator} test helper
 * 
 * @author etienne-sf
 */
class DeepComparatorTest {

	DeepComparator deepComparator;

	public class ComparisonSuperClass {
		ComparisonObject comp;

		public ComparisonObject getComp() {
			return comp;
		}
	}

	public class ComparisonObject extends ComparisonSuperClass {
		int id;
		String name;
		Long l;
		String ignored;

		public ComparisonObject(int id, String name, Long l, String ignored) {
			super();
			this.id = id;
			this.name = name;
			this.l = l;
			this.ignored = ignored;
			this.comp = this;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public Long getL() {
			return l;
		}

		public String getIgnored() {
			return ignored;
		}
	}

	@BeforeEach
	public void beforeEach() {
		deepComparator = new DeepComparator();

		deepComparator.addComparedClass(ComparisonObject.class);
		deepComparator.addIgnoredFields(ComparisonObject.class, "ignored");

		// To break the cycle where comparing ComparisonSuperClass.comp cycle with the Comparison class, we define a
		// specific comparison rule:
		deepComparator.addSpecificComparisonRules(ComparisonSuperClass.class, "comp", new ComparisonRule() {
			@Override
			public List<Difference> compare(Object o1, Object o2) {
				ComparisonObject co1 = (ComparisonObject) o1;
				ComparisonObject co2 = (ComparisonObject) o2;
				if (co1.id != co2.id) {
					List<Difference> differences = new ArrayList<>();
					differences.add(new DeepComparator.Difference("/id", DiffenceType.VALUE, co1.id, co2.id, null));
					return differences;
				}
				return null;
			}
		});
	}

	@Test
	void test_basicTypes() {
		test_oneType("QSDQSD", "sdsdq", 1);
		test_oneType(1, 2, 1);
		test_oneType(1L, 2L, 1);
		test_oneType(Short.valueOf((short) 1), Short.valueOf((short) 2), 1);
		test_oneType(Long.valueOf(1), Long.valueOf(2), 1);
		test_oneType(Double.valueOf(1), Double.valueOf(2), 1);
		test_oneType(Float.valueOf(1), Float.valueOf(2), 1);
	}

	@Test
	void test_twoClasses() {
		List<Difference> differences;

		differences = assertDiffType(1, 2L, 1, DeepComparator.DiffenceType.TYPE);
		assertEquals("", differences.get(0).path);
		assertEquals(1, differences.get(0).value1);
		assertEquals(2L, differences.get(0).value2);

		differences = assertDiffType(1, "2", 1, DeepComparator.DiffenceType.TYPE);
		assertEquals("", differences.get(0).path);
		assertEquals(1, differences.get(0).value1);
		assertEquals("2", differences.get(0).value2);
	}

	@Test
	void test_Lists() {
		// Preparation
		List<Difference> differences;
		String str1 = "str1";
		String str2 = "str2";
		String str3 = "str3";
		String str4 = "str4";

		List<String> lst123 = Arrays.asList(str1, str2, str3);
		List<String> lst132 = Arrays.asList(str1, str3, str2);
		List<String> lst412 = Arrays.asList(str4, str1, str2);
		List<String> lst12 = Arrays.asList(str1, str2);

		// Tests

		test_oneType(lst123, lst132, 0);

		// The test is non-ordered for lists: when no difference
		test_oneType(lst123, lst132, 0);

		// The test is non-ordered for lists: same size, but content is different
		differences = test_oneType(lst123, lst412, 1);
		assertEquals(DeepComparator.DiffenceType.VALUE, differences.get(0).type);
		assertEquals("", differences.get(0).path);
		assertEquals("list1 contains the following item but not list2: str3", differences.get(0).info);

		// Different list size
		differences = test_oneType(lst123, lst12, 1);
		assertEquals(DeepComparator.DiffenceType.LIST_SIZE, differences.get(0).type);
		assertEquals("", differences.get(0).path);
		assertEquals("o1: 3 items, o2: 2 items", differences.get(0).info);
	}

	@Test
	void test_Map() {
		// Preparation
		List<Difference> differences;
		int i = 0;
		Map<Integer, String> map123 = new HashMap<>();
		map123.put(1, "1");
		map123.put(2, "2");
		map123.put(3, "3");
		//
		Map<Integer, String> map123bis = new HashMap<>();
		map123bis.put(1, "11");
		map123bis.put(2, "22");
		map123bis.put(3, "33");
		//
		Map<Integer, String> map12 = new HashMap<>();
		map12.put(1, "1");
		map12.put(2, "2");

		// Tests

		test_oneType(map123, map123, 0);

		// Different map size
		differences = deepComparator.compare(map123, map12);
		assertEquals(2, differences.size());
		//
		assertEquals(DeepComparator.DiffenceType.LIST_SIZE, differences.get(0).type);
		assertEquals("", differences.get(0).path);
		assertEquals("o1: 3 items, o2: 2 items", differences.get(0).info);
		//
		assertEquals(DeepComparator.DiffenceType.VALUE, differences.get(1).type);
		assertEquals("[3]", differences.get(1).path);
		assertEquals("3", differences.get(1).value1);
		assertEquals(null, differences.get(1).value2);

		// Same map size, but difference in the values
		differences = test_oneType(map123, map123bis, 3);
		//
		Difference d = differences.get(i++);
		assertEquals(DeepComparator.DiffenceType.VALUE, d.type);
		assertEquals("[1]", d.path);
		assertEquals("1", d.value1);
		assertEquals("11", d.value2);
		//
		d = differences.get(i++);
		assertEquals(DeepComparator.DiffenceType.VALUE, d.type);
		assertEquals("[2]", d.path);
		assertEquals("2", d.value1);
		assertEquals("22", d.value2);
		//
		d = differences.get(i++);
		assertEquals(DeepComparator.DiffenceType.VALUE, d.type);
		assertEquals("[3]", d.path);
		assertEquals("3", d.value1);
		assertEquals("33", d.value2);
	}

	@Test
	void test_nonAddedObjects() {
		// Preparation : we want an empty object list
		deepComparator.comparedClasses = new TreeSet<>();
		deepComparator.ignoredClasses = new ArrayList<>();
		ComparisonObject o1 = new ComparisonObject(1, "o1", 100L, "ignored 1");
		ComparisonObject o2 = new ComparisonObject(2, "o2", 200L, "ignored 2");

		// Go, go, go
		RuntimeException e = assertThrows(RuntimeException.class, () -> deepComparator.compare(o1, o2));
		assertTrue(e.getMessage().contains(o1.getClass().getName() + " is not managed by this comparison"));
	}

	@Test
	void ignoredObjects() {
		// Preparation
		deepComparator.comparedClasses = new TreeSet<>();
		deepComparator.addIgnoredClass(ComparisonObject.class);
		ComparisonObject o1 = new ComparisonObject(1, "o1", 100L, "ignored 1");
		ComparisonObject o2 = new ComparisonObject(2, "o2", 200L, "ignored 2");

		// Go, go, go
		assertEquals(0, deepComparator.compare(o1, o2).size());
	}

	@Test
	void test_Objects() {
		// Preparation
		Difference d;
		int i = 0;
		ComparisonObject o1 = new ComparisonObject(1, "o1", 100L, "ignored 1");
		ComparisonObject o2 = new ComparisonObject(2, "o2", 200L, "ignored 2");

		// Go, go, go
		List<Difference> differences = deepComparator.compare(o1, o2);

		// Verification
		assertEquals(4, differences.size());

		d = differences.get(i++);
		assertEquals(DeepComparator.DiffenceType.VALUE, d.type);
		assertEquals("/id", d.path);
		assertEquals(o1.id, d.value1);
		assertEquals(o2.id, d.value2);

		d = differences.get(i++);
		assertEquals(DeepComparator.DiffenceType.VALUE, d.type);
		assertEquals("/name", d.path);
		assertEquals(o1.name, d.value1);
		assertEquals(o2.name, d.value2);

		d = differences.get(i++);
		assertEquals(DeepComparator.DiffenceType.VALUE, d.type);
		assertEquals("/l", d.path);
		assertEquals(o1.l, d.value1);
		assertEquals(o2.l, d.value2);

		d = differences.get(i++);
		assertEquals(DeepComparator.DiffenceType.VALUE, d.type);
		assertEquals("/comp/id", d.path);
		assertEquals(o1.comp.id, d.value1);
		assertEquals(o2.comp.id, d.value2);
	}

	@Test
	void test_ObjectsWithDiffInSuperClassFields() {
		// Preparation
		Difference d;
		int i = 0;
		ComparisonObject o1 = new ComparisonObject(1, "o1", 100L, "ignored 1");
		ComparisonObject o2 = new ComparisonObject(1, "o1", 100L, "ignored 1");

		// Go, go, go (exactly the same)
		assertEquals(0, deepComparator.compare(o1, o2).size());

		// Go, go, go (o2 is another object, with the same values)
		o2 = new ComparisonObject(1, "o1", 100L, "ignored 1");
		assertEquals(0, deepComparator.compare(o1, o2).size());

		// Go, go, go (We now change only the o2.comp field, but again another instance having the same values)
		o2.comp = new ComparisonObject(1, "o1", 100L, "ignored 1");
		assertEquals(0, deepComparator.compare(o1, o2).size());

		// Go, go, go (We now change only the o2.comp field, but again another instance having the same values out of
		// the changed name, wich is not compared by the specific rule define in the setUp method, here above)
		o2.comp = new ComparisonObject(1, "a different name", 100L, "ignored 1");
		assertEquals(0, deepComparator.compare(o1, o2).size());

		// Go, go, go (We now change only the o2.comp field, but again another instance having the same values out of
		// the changed id)
		o2.comp = new ComparisonObject(666, "o1", 100L, "ignored 1");
		List<Difference> differences = deepComparator.compare(o1, o2);
		assertEquals(1, differences.size());
		d = differences.get(i++);
		assertEquals(DeepComparator.DiffenceType.VALUE, d.type);
		assertEquals("/comp/id", d.path);
		assertEquals(o1.comp.id, d.value1);
		assertEquals(o2.comp.id, d.value2);
	}

	/**
	 * Test of one type, with two different objects of the same type.
	 * 
	 * @param o1
	 *            Must be non null
	 * @param o2
	 *            Must be non null, and different from o1
	 * @param nbDifferences
	 *            Number of expected differences between o1 and o2
	 * @return The differences list when calling
	 */
	List<Difference> test_oneType(Object o1, Object o2, int nbDifferences) {
		// Different
		assertEquals(1, deepComparator.compare(o1, null).size());
		assertEquals(1, deepComparator.compare(null, o2).size());
		List<Difference> differences = deepComparator.compare(o1, o2);
		assertEquals(nbDifferences, differences.size());
		assertEquals(nbDifferences, deepComparator.compare(o2, o1).size());

		// Equal
		assertEquals(0, deepComparator.compare(null, null).size());
		assertEquals(0, deepComparator.compare(o1, o1).size());
		assertEquals(0, deepComparator.compare(o2, o2).size());

		return differences;
	}

	List<Difference> assertDiffType(Object o1, Object o2, int nbDifferences, DeepComparator.DiffenceType type) {
		List<Difference> differences = deepComparator.compare(o1, o2);
		assertEquals(nbDifferences, differences.size());
		for (Difference d : differences) {
			assertEquals(type, d.type);
		}
		return differences;
	}

}
