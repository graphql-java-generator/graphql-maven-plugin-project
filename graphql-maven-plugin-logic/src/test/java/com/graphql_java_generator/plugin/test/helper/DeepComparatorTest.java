/**
 * 
 */
package com.graphql_java_generator.plugin.test.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.graphql_java_generator.plugin.test.helper.DeepComparator.ComparisonRule;
import com.graphql_java_generator.plugin.test.helper.DeepComparator.Difference;
import com.graphql_java_generator.plugin.test.helper.DeepComparator.DifferenceType;
import com.graphql_java_generator.plugin.test.helper.DeepComparator.ExecutedComparison;

/**
 * Test for the {@link DeepComparator} test helper
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
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

		@Override
		public String toString() {
			return ComparisonObject.class.getSimpleName() + "(id=" + id + ",name=" + name + ",ignored=" + ignored + ")";
		}
	}

	public class ComparisonObjectWithStringId {
		String id;
		String name;
		String description;
		String ignored;

		public ComparisonObjectWithStringId(String id, String name, String description, String ignored) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.ignored = ignored;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public String getIgnored() {
			return ignored;
		}

		@Override
		public String toString() {
			return ComparisonObjectWithStringId.class.getSimpleName() + "(id=" + id + ",name=" + name + ",description="
					+ description + ",ignored=" + ignored + ")";
		}
	}

	public enum TestEnum {
		ENUM1, ENUM2, ENUM3, ENUM4;
	}

	/**
	 * This class allows to test cycles between objects. The {@link DeepComparatorTest#test_Cycle()} test will create
	 * two instances, each one references the other. So there is a loop between them. The test will check that there is
	 * no endless loop (that is: no stack overflow)<BR/>
	 * The behavior is this one: the comparator compares the name of the two {@link Cycler} instances. But it doesn't
	 * compare the cycler field, to avoid loops.
	 * 
	 * @author etienne-sf
	 * @See DeepComparatorTest#test_Cycle()
	 */
	public class Cycler {
		String name;
		Cycler cycler;

		public String getName() {
			return name;
		}

		public Cycler getCycler() {
			return cycler;
		}
	}

	@BeforeEach
	public void beforeEach() {
		deepComparator = new DeepComparator();

		deepComparator.addIdField(ComparisonObject.class, "id");
		deepComparator.addIdField(ComparisonObjectWithStringId.class, "id");

		deepComparator.addIgnoredFields(ComparisonObject.class, "ignored");
		deepComparator.addIgnoredFields(ComparisonObjectWithStringId.class, "ignored");

		// To break the cycle where comparing ComparisonSuperClass.comp cycle with the Comparison class, we define a
		// specific comparison rule:
		deepComparator.addSpecificComparisonRules(ComparisonSuperClass.class, "comp", new ComparisonRule() {
			@Override
			public List<Difference> compare(Object o1, Object o2, int nbMaxDifferences) {
				ComparisonObject co1 = (ComparisonObject) o1;
				ComparisonObject co2 = (ComparisonObject) o2;
				if (co1.id != co2.id) {
					List<Difference> differences = new ArrayList<>();
					differences.add(new DeepComparator.Difference("/id", DifferenceType.VALUE, co1.id, co2.id, null));
					return differences;
				}
				return null;
			}
		});
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_ExecutedComparison() {
		String str1 = "a string";
		String str2 = "another string";
		String str3 = "something else";
		ExecutedComparison ec12 = new ExecutedComparison(str1, str2);
		ExecutedComparison ec21 = new ExecutedComparison(str2, str1);
		ExecutedComparison ec13 = new ExecutedComparison(str1, str3);
		ExecutedComparison ec31 = new ExecutedComparison(str3, str1);

		// o1 and o2 are mandatory
		assertThrows(NullPointerException.class, () -> new ExecutedComparison(null, str2));
		assertThrows(NullPointerException.class, () -> new ExecutedComparison(str2, null));

		// str1 may be stored in o1 or o2, depending on its Java id. This may change from an execution to another:
		if (ec12.o1 == str1) {
			assertTrue(ec12.o1 == str1);
			assertTrue(ec12.o2 == str2);
			assertEquals(ec12.id1, Integer.toHexString(System.identityHashCode(str1)));
			assertEquals(ec12.id2, Integer.toHexString(System.identityHashCode(str2)));
		} else {
			assertTrue(ec12.o1 == str2);
			assertTrue(ec12.o2 == str1);
			assertEquals(ec12.id1, Integer.toHexString(System.identityHashCode(str2)));
			assertEquals(ec12.id2, Integer.toHexString(System.identityHashCode(str1)));
		}

		// ec1 and ec2 must be strictly equals (the order of parameters in the constructor should not change the stored
		// result)
		assertTrue(ec12.o1 == ec21.o1);
		assertTrue(ec12.o2 == ec21.o2);
		assertEquals(ec12.id1, ec21.id1);
		assertEquals(ec12.id2, ec21.id2);

		// The result is equality
		assertEquals(ec12, ec21);
		assertNotEquals(ec12, new ExecutedComparison(str3, str1));
		assertNotEquals(ec12, new ExecutedComparison(str1, str3));

		// ExecutedComparison is allowed in TreeSet (as it implements Comparable)
		// And ec12 is equals to ec21, and ec13 is equals to ec31, so:
		Set<ExecutedComparison> set = new TreeSet<>();
		set.add(ec12);
		set.add(ec21);
		set.add(ec13);
		set.add(ec31);
		//
		assertEquals(2, set.size());
		assertTrue(set.contains(ec12));
		assertTrue(set.contains(ec21));
		assertTrue(set.contains(ec13));
		assertTrue(set.contains(ec31));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_equals() {
		ComparisonObject o1 = new ComparisonObject(1, "o1", 100L, "ignored 1");
		ComparisonObject o2 = new ComparisonObject(2, "o2", 200L, "ignored 2");

		execTestEqualsOneType(true, false);
		execTestEqualsOneType("1", "2");
		execTestEqualsOneType(1, 2);
		execTestEqualsOneType(o1, o2);
	}

	@Test
	void test_basicTypes() {
		execTestCompareOneType("QSDQSD", "sdsdq", 1);
		execTestCompareOneType(1, 2, 1);
		execTestCompareOneType(1L, 2L, 1);
		execTestCompareOneType(Short.valueOf((short) 1), Short.valueOf((short) 2), 1);
		execTestCompareOneType(Long.valueOf(1), Long.valueOf(2), 1);
		execTestCompareOneType(Double.valueOf(1), Double.valueOf(2), 1);
		execTestCompareOneType(Float.valueOf(1), Float.valueOf(2), 1);
		execTestCompareOneType(TestEnum.ENUM1, TestEnum.ENUM2, 1);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_twoClasses() {
		List<Difference> differences;

		differences = assertDiffType(1, 2L, 1, DeepComparator.DifferenceType.CLASS);
		assertEquals("", differences.get(0).path);
		assertEquals(1, differences.get(0).value1);
		assertEquals(2L, differences.get(0).value2);

		differences = assertDiffType(1, "2", 1, DeepComparator.DifferenceType.CLASS);
		assertEquals("", differences.get(0).path);
		assertEquals(1, differences.get(0).value1);
		assertEquals("2", differences.get(0).value2);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
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

		execTestCompareOneType(lst123, lst132, 0);

		// The test is non-ordered for lists: when no difference
		execTestCompareOneType(lst123, lst132, 0);

		// The test is non-ordered for lists: same size, but content is different
		differences = execTestCompareOneType(lst123, lst412, 2);
		//
		assertEquals(DeepComparator.DifferenceType.VALUE, differences.get(0).type);
		assertEquals("", differences.get(0).path);
		assertEquals(str3, differences.get(0).value1);
		assertEquals(null, differences.get(0).value2);
		assertEquals("list1 contains the following item but not list2: str3", differences.get(0).info);
		//
		assertEquals(DeepComparator.DifferenceType.VALUE, differences.get(1).type);
		assertEquals("", differences.get(1).path);
		assertEquals(null, differences.get(1).value1);
		assertEquals(str4, differences.get(1).value2);
		assertEquals("list2 contains the following item but not list1: str4", differences.get(1).info);

		// Different list size
		differences = execTestCompareOneType(lst123, lst12, 2);
		//
		assertEquals(DeepComparator.DifferenceType.LIST_SIZE, differences.get(0).type);
		assertEquals("", differences.get(0).path);
		assertEquals("o1: 3 items, o2: 2 items", differences.get(0).info);
		//
		assertEquals(DeepComparator.DifferenceType.VALUE, differences.get(1).type);
		assertEquals(str3, differences.get(1).value1);
		assertEquals(null, differences.get(1).value2);
		assertEquals("list1 contains the following item but not list2: str3", differences.get(1).info);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_ListsEnum() {
		// Preparation
		List<Difference> differences;
		List<TestEnum> lst123 = Arrays.asList(TestEnum.ENUM1, TestEnum.ENUM2, TestEnum.ENUM3);
		List<TestEnum> lst132 = Arrays.asList(TestEnum.ENUM1, TestEnum.ENUM3, TestEnum.ENUM2);
		List<TestEnum> lst412 = Arrays.asList(TestEnum.ENUM4, TestEnum.ENUM1, TestEnum.ENUM2);
		List<TestEnum> lst12 = Arrays.asList(TestEnum.ENUM1, TestEnum.ENUM2);

		// Tests

		execTestCompareOneType(lst123, lst132, 0);

		// The test is non-ordered for lists: when no difference
		execTestCompareOneType(lst123, lst132, 0);

		// The test is non-ordered for lists: same size, but content is different
		differences = execTestCompareOneType(lst123, lst412, 2);
		//
		assertEquals(DeepComparator.DifferenceType.VALUE, differences.get(0).type);
		assertEquals("", differences.get(0).path);
		assertEquals(TestEnum.ENUM3, differences.get(0).value1);
		assertEquals(null, differences.get(0).value2);
		assertEquals("list1 contains the following item but not list2: ENUM3", differences.get(0).info);
		//
		assertEquals(DeepComparator.DifferenceType.VALUE, differences.get(1).type);
		assertEquals("", differences.get(1).path);
		assertEquals(null, differences.get(1).value1);
		assertEquals(TestEnum.ENUM4, differences.get(1).value2);
		assertEquals("list2 contains the following item but not list1: ENUM4", differences.get(1).info);

		// Different list size
		differences = execTestCompareOneType(lst123, lst12, 2);
		//
		assertEquals(DeepComparator.DifferenceType.LIST_SIZE, differences.get(0).type);
		assertEquals("", differences.get(0).path);
		assertEquals("o1: 3 items, o2: 2 items", differences.get(0).info);
		//
		assertEquals(DeepComparator.DifferenceType.VALUE, differences.get(1).type);
		assertEquals(TestEnum.ENUM3, differences.get(1).value1);
		assertEquals(null, differences.get(1).value2);
		assertEquals("list1 contains the following item but not list2: ENUM3", differences.get(1).info);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
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

		execTestCompareOneType(map123, map123, 0);

		// Different map size
		differences = deepComparator.differences(map123, map12, Integer.MAX_VALUE);
		assertEquals(3, differences.size());
		//
		assertEquals(DeepComparator.DifferenceType.LIST_SIZE, differences.get(0).type);
		assertEquals("/keys", differences.get(0).path);
		assertEquals("o1: 3 items, o2: 2 items", differences.get(0).info);
		//
		assertEquals(DeepComparator.DifferenceType.VALUE, differences.get(1).type);
		assertEquals("/keys", differences.get(1).path);
		assertEquals(3, differences.get(1).value1);
		assertEquals(null, differences.get(1).value2);
		//
		assertEquals(DeepComparator.DifferenceType.VALUE, differences.get(2).type);
		assertEquals("[3]", differences.get(2).path);
		assertEquals("3", differences.get(2).value1);
		assertEquals(null, differences.get(2).value2);

		// Same map size, but difference in the values
		differences = execTestCompareOneType(map123, map123bis, 3);
		//
		Difference d = differences.get(i++);
		assertEquals(DeepComparator.DifferenceType.VALUE, d.type);
		assertEquals("[1]", d.path);
		assertEquals("1", d.value1);
		assertEquals("11", d.value2);
		//
		d = differences.get(i++);
		assertEquals(DeepComparator.DifferenceType.VALUE, d.type);
		assertEquals("[2]", d.path);
		assertEquals("2", d.value1);
		assertEquals("22", d.value2);
		//
		d = differences.get(i++);
		assertEquals(DeepComparator.DifferenceType.VALUE, d.type);
		assertEquals("[3]", d.path);
		assertEquals("3", d.value1);
		assertEquals("33", d.value2);
	}

	// @Test
	// @Execution(ExecutionMode.CONCURRENT)
	// void test_nonAddedObjects() {
	// // Preparation : we want an empty object list
	// // deepComparator.comparedClasses = new TreeSet<>();
	// deepComparator.ignoredClasses = new ArrayList<>();
	// ComparisonObject o1 = new ComparisonObject(1, "o1", 100L, "ignored 1");
	// ComparisonObject o2 = new ComparisonObject(2, "o2", 200L, "ignored 2");
	//
	// // Go, go, go
	// RuntimeException e = assertThrows(RuntimeException.class, () -> deepComparator.compare(o1, o2));
	// assertTrue(e.getMessage().contains(o1.getClass().getName() + " is not managed by this comparison"));
	// }

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void ignoredObjects() {
		// Preparation
		// deepComparator.comparedClasses = new TreeSet<>();
		deepComparator.addIgnoredClass(ComparisonObject.class);
		ComparisonObject o1 = new ComparisonObject(1, "o1", 100L, "ignored 1");
		ComparisonObject o2 = new ComparisonObject(2, "o2", 200L, "ignored 2");

		// Go, go, go
		assertEquals(0, deepComparator.differences(o1, o2, Integer.MAX_VALUE).size());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_differencesObjects() {
		// Preparation
		Difference d;
		int i = 0;
		ComparisonObject o1 = new ComparisonObject(1, "o1", 100L, "ignored 1");
		ComparisonObject o2 = new ComparisonObject(2, "o2", 200L, "ignored 2");

		// Go, go, go
		List<Difference> differences = deepComparator.differences(o1, o2, Integer.MAX_VALUE);

		// Verification
		assertEquals(4, differences.size());

		d = differences.get(i++);
		assertEquals(DeepComparator.DifferenceType.VALUE, d.type);
		assertEquals("/id", d.path);
		assertEquals(o1.id, d.value1);
		assertEquals(o2.id, d.value2);

		d = differences.get(i++);
		assertEquals(DeepComparator.DifferenceType.VALUE, d.type);
		assertEquals("/name", d.path);
		assertEquals(o1.name, d.value1);
		assertEquals(o2.name, d.value2);

		d = differences.get(i++);
		assertEquals(DeepComparator.DifferenceType.VALUE, d.type);
		assertEquals("/l", d.path);
		assertEquals(o1.l, d.value1);
		assertEquals(o2.l, d.value2);

		d = differences.get(i++);
		assertEquals(DeepComparator.DifferenceType.VALUE, d.type);
		assertEquals("/comp/id", d.path);
		assertEquals(o1.comp.id, d.value1);
		assertEquals(o2.comp.id, d.value2);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_differencesObjects_withIdField() {
		// Preparation
		Difference d;
		int i = 0;
		// The if field of ComparisonObject is marked as id. So these objects should be matched, and as their attributes
		// are different, the path for the found differences should be /ComparisonObject(id:1)
		ComparisonObject o1 = new ComparisonObject(1, "o1", 100L, "ignored 1");
		ComparisonObject o1bis = new ComparisonObject(1, "o2", 200L, "ignored 2");

		// Go, go, go
		List<Difference> differences = deepComparator.differences(o1, o1bis, Integer.MAX_VALUE);

		// Verification
		assertEquals(2, differences.size());

		d = differences.get(i++);
		assertEquals(DeepComparator.DifferenceType.VALUE, d.type);
		assertEquals("/ComparisonObject(id:1)/name", d.path);
		assertEquals(o1.name, d.value1);
		assertEquals(o1bis.name, d.value2);

		d = differences.get(i++);
		assertEquals(DeepComparator.DifferenceType.VALUE, d.type);
		assertEquals("/ComparisonObject(id:1)/l", d.path);
		assertEquals(o1.l, d.value1);
		assertEquals(o1bis.l, d.value2);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_differencesListObjects_withIdField() {
		// Preparation
		Difference d;
		int i = 0;
		// The if field of ComparisonObject is marked as id. So these objects should be matched, and as their attributes
		// are different, the path for the found differences should be /ComparisonObject(id:1)
		ComparisonObjectWithStringId o1 = new ComparisonObjectWithStringId("1", "o1", "description 1", "ignored 1");
		ComparisonObjectWithStringId o2 = new ComparisonObjectWithStringId("2", "o2", "description 2", "ignored 2");
		ComparisonObjectWithStringId o1bis = new ComparisonObjectWithStringId("1", "o1", "not description 1",
				"ignored diff 1");
		ComparisonObjectWithStringId o2bis = new ComparisonObjectWithStringId("2", "o2", "description 2",
				"ignored diff 2");
		List<ComparisonObjectWithStringId> list1 = Arrays.asList(o1, o2);
		List<ComparisonObjectWithStringId> list2 = Arrays.asList(o2bis, o1bis);

		// Go, go, go
		List<Difference> differences = deepComparator.differences(list1, list2, Integer.MAX_VALUE);

		// Verification
		assertEquals(1, differences.size());

		d = differences.get(i++);
		assertEquals(DeepComparator.DifferenceType.VALUE, d.type);
		assertEquals("[1]/ComparisonObjectWithStringId(id:1)/description", d.path);
		assertEquals("description 1", d.value1);
		assertEquals("not description 1", d.value2);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_ObjectsWithDiffInSuperClassFields() {
		// Preparation
		Difference d;
		int i = 0;
		ComparisonObject o1 = new ComparisonObject(1, "o1", 100L, "ignored 1");
		ComparisonObject o2 = new ComparisonObject(1, "o1", 100L, "ignored 1");

		// Go, go, go (exactly the same)
		assertEquals(0, deepComparator.differences(o1, o2, Integer.MAX_VALUE).size());

		// Go, go, go (o2 is another object, with the same values)
		o2 = new ComparisonObject(1, "o1", 100L, "ignored 1");
		assertEquals(0, deepComparator.differences(o1, o2, Integer.MAX_VALUE).size());

		// Go, go, go (We now change only the o2.comp field, but again another instance having the same values)
		o2.comp = new ComparisonObject(1, "o1", 100L, "ignored 1");
		assertEquals(0, deepComparator.differences(o1, o2, Integer.MAX_VALUE).size());

		// Go, go, go (We now change only the o2.comp field, but again another instance having the same values out of
		// the changed name, wich is not compared by the specific rule define in the setUp method, here above)
		o2.comp = new ComparisonObject(1, "a different name", 100L, "ignored 1");
		assertEquals(0, deepComparator.differences(o1, o2, Integer.MAX_VALUE).size());

		// Go, go, go (We now change only the o2.comp field, but again another instance having the same values out of
		// the changed id)
		o2.comp = new ComparisonObject(666, "o1", 100L, "ignored 1");
		List<Difference> differences = deepComparator.differences(o1, o2, Integer.MAX_VALUE);
		assertEquals(1, differences.size());
		d = differences.get(i++);
		assertEquals(DeepComparator.DifferenceType.VALUE, d.type);
		assertEquals("/ComparisonObject(id:1)/comp/id", d.path);
		assertEquals(o1.comp.id, d.value1);
		assertEquals(o2.comp.id, d.value2);
	}

	/**
	 * This class allows to test cycles between objects. The {@link DeepComparatorTest#test_Cycle()} test will create
	 * two instances, each one references the other. So there is a loop between them. The test will check that there is
	 * no endless loop (that is: no stack overflow)<BR/>
	 * The behavior is this one: the comparator compares the name of the two {@link Cycler} instances. But it doesn't
	 * compare the cycler field, to avoid loops.
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Cycle() {
		// Preparation
		Cycler c1 = new Cycler();
		Cycler c2 = new Cycler();
		c1.name = "c1";
		c2.name = "c2";
		c1.cycler = c2;
		c2.cycler = c1;

		// Go, go, go
		List<Difference> differences = deepComparator.differences(c1, c2, Integer.MAX_VALUE);
		//
		assertEquals(1, differences.size());
		Difference d = differences.get(0);
		assertEquals(DeepComparator.DifferenceType.VALUE, d.type);
		assertEquals("/name", d.path);
		assertEquals("c1", d.value1);
		assertEquals("c2", d.value2);

		// Go, go, go
		differences = deepComparator.differences(c2, c1, Integer.MAX_VALUE);
		//
		assertEquals(1, differences.size());
		d = differences.get(0);
		assertEquals(DeepComparator.DifferenceType.VALUE, d.type);
		assertEquals("/name", d.path);
		assertEquals("c2", d.value1);
		assertEquals("c1", d.value2);
	}

	/**
	 * Test of one type against the {@link DeepComparator#differences(Object, Object, int)} method, with two different
	 * objects of the same type.
	 * 
	 * @param o1
	 *            Must be non null
	 * @param o2
	 *            Must be non null, and different from o1
	 * @param nbDifferences
	 *            Number of expected differences between o1 and o2
	 * @return The differences list when calling
	 */
	List<Difference> execTestCompareOneType(Object o1, Object o2, int nbDifferences) {
		// Different
		assertEquals(1, deepComparator.differences(o1, null, Integer.MAX_VALUE).size());
		assertEquals(1, deepComparator.differences(null, o2, Integer.MAX_VALUE).size());
		List<Difference> differences = deepComparator.differences(o1, o2, Integer.MAX_VALUE);
		assertEquals(nbDifferences, differences.size());
		assertEquals(nbDifferences, deepComparator.differences(o2, o1, Integer.MAX_VALUE).size());

		// Equal
		assertEquals(0, deepComparator.differences(null, null, Integer.MAX_VALUE).size());
		assertEquals(0, deepComparator.differences(o1, o1, Integer.MAX_VALUE).size());
		assertEquals(0, deepComparator.differences(o2, o2, Integer.MAX_VALUE).size());

		return differences;
	}

	/**
	 * Test of one type against the {@link DeepComparator#equals(Object, Object)} method, with two different objects of
	 * the same type.
	 * 
	 * @param o1
	 *            Must be non null
	 * @param o2
	 *            Must be non null, and different from o1
	 */
	void execTestEqualsOneType(Object o1, Object o2) {
		// Different
		assertFalse(deepComparator.equals(o1, null));
		assertFalse(deepComparator.equals(null, o2));
		assertFalse(deepComparator.equals(o1, o2));
		assertFalse(deepComparator.equals(o2, o1));

		// Equal
		assertTrue(deepComparator.equals(null, null));
		assertTrue(deepComparator.equals(o1, o1));
		assertTrue(deepComparator.equals(o2, o2));
	}

	List<Difference> assertDiffType(Object o1, Object o2, int nbDifferences, DeepComparator.DifferenceType type) {
		List<Difference> differences = deepComparator.differences(o1, o2, Integer.MAX_VALUE);
		assertEquals(nbDifferences, differences.size());
		for (Difference d : differences) {
			assertEquals(type, d.type);
		}
		return differences;
	}

}
