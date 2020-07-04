/**
 * 
 */
package com.graphql_java_generator.plugin.test.helper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphql_java_generator.GraphqlUtils;

/**
 * This class deep compares two objects, say a and b. It:
 * <UL>
 * <LI>Checks that a and b are of the same class</LI>
 * <LI>Compares each field of a to each field of b (the fields defined in their superclasses are also scanned):</LI>
 * <LI>If the field is an ignored field, this field is skipped</LI>
 * <LI>If the field is of a basic type: use the equals method</LI>
 * <LI>If the field is a List: do a deep non-ordered comparison. Each list must be of the same size, and each object in
 * a's field must be deep equal to one object of the b's list. To do this, for each item in a's list, a recursive
 * deepEquals call is done on each item of the b's list until this deepEquals call returns true. If no such call returns
 * true, then this item of the a's list is not present in the b's list: they are different</LI>
 * <LI>If the field is a known object type (see parameters): a recursive call to the deep comparison is done, to compare
 * the a's field to the b's field.</LI>
 * <LI>Otherwise an error is thrown (non managed type for comparison). This insure that the proper comparison mode is
 * used</LI>
 * </UL>
 * All differences are logged if the level is debug or lower. They are also returned by the
 * {@link #compare(Object, Object)} method, to allow further processing. This makes it possible to correct these
 * differences.
 * 
 * @author etienne-sf
 */
public class DeepComparator {

	/** The logger for this class. It can be overriden by using the relevant constructor */
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	GraphqlUtils graphqlUtils = GraphqlUtils.graphqlUtils;

	/**
	 * The list of basic types. For these types, the comparison is done by calling the {@link Object#equals(Object)}
	 * method
	 */
	List<Class<?>> basicClasses = new ArrayList<>();

	/**
	 * The list of objects, that will be compared field by field. For each of these objects, the list of fields to
	 * ignore during the comparison can be set, by using the {@link #ignoredFields} map.
	 */
	Set<String> comparedClasses = new TreeSet<>();

	/** The list of objects that will be ignored during the comparison: all fields of this type are skipped. */
	List<Class<?>> ignoredClasses = new ArrayList<>();

	/**
	 * The list of fields that will be ignored, for each class. The key is the class. The value is the list of ignored
	 * field names for this class. The ignored fields should contain all ignored fields for this class, including those
	 * which are actually defined in its superclasses. This allow to choose if a field is ignored or not per subclass
	 * (if there are two subclasses for one superclasse).
	 */
	Map<Class<?>, Set<String>> ignoredFields = new HashMap<>();

	/**
	 * This maps allows to define comparison rules, for the given fields of the given classes. The key of this map is
	 * the class. The value is also a map, where: the key is the field name (including field declared in superclasses),
	 * and the value is an instance of {@link ComparisonRule}, whose {@link ComparisonRule#compare(Object, Object)}
	 * method will be called for each such field of such class that is encountered during the deep comparison.<BR/>
	 * This allows to break cycles (and avoid Stack Overflow exception): if the A.b field is of class B, and the B.a
	 * field is of class A, then you can define specific comparison rule in the {@link #comparisonPredicates} map, so
	 * that when comparing the B.a field, you don't cycle back into the A class. This comparison can be a non operation
	 * method, or a any comparison of you own.
	 * 
	 */
	Map<Class<?>, Map<String, ComparisonRule>> specificComparisonRules = new HashMap<>();

	public enum DiffenceType {
		TYPE, VALUE, LIST_SIZE
	}

	public static class Difference {
		/**
		 * The path the show how the object structure has been crawled. For instance, if the comparison starts with an
		 * object o1, it compared the field <I>field1</I>, which is of type B, then its field <I>field2</I> then found a
		 * difference, then the path is <I>/field1/field2</I>
		 */
		String path;
		DiffenceType type;
		Object value1;
		Object value2;
		/** Some additional information, especially for the {@link Collection}s */
		String info;

		public Difference(String path, DiffenceType diffenceType, Object value1, Object value2, String info) {
			this.path = path;
			this.type = diffenceType;
			this.value1 = value1;
			this.value2 = value2;
			this.info = info;
		}
	}

	/**
	 * This interface allows to define a class that will execute specific comparison. It's use in the
	 * {@link #comparisonPredicates} map, to define special behavior, including breaking cycles: if the A.b field is of
	 * class B, and the B.a field is of class A, then you can define specific comparison rule in the
	 * {@link #comparisonPredicates} map, so that when comparing the B.a field, you don't cycle back into the A class.
	 * This comparison can be a non operation method, or a any comparison of you own.
	 * 
	 * @author etienne-sf
	 */
	public interface ComparisonRule {
		/**
		 * Compares two objects, and returns the list of found difference.
		 * 
		 * @param o1
		 *            may not be null (this will be checked before calling this method)
		 * @param o2
		 *            may not be null (this will be checked before calling this method)
		 * @return A non null list. If it's empty, then there is no difference between o1 and o2. The root path for the
		 *         returned {@link Difference}s is this field, starting by a slash (/). If differences are returned by
		 *         this method, they will be updated with the current path of the comparison when this method was
		 *         called.
		 */
		public List<Difference> compare(Object o1, Object o2);
	}

	public DeepComparator() {
		// Let's configure the default list of basic types (they will be compared using the equals method)
		basicClasses.add(char.class);
		basicClasses.add(Character.class);
		basicClasses.add(int.class);
		basicClasses.add(Integer.class);
		basicClasses.add(long.class);
		basicClasses.add(Long.class);
		basicClasses.add(float.class);
		basicClasses.add(Float.class);
		basicClasses.add(double.class);
		basicClasses.add(Double.class);
		basicClasses.add(short.class);
		basicClasses.add(Short.class);
		basicClasses.add(String.class);
	}

	public DeepComparator(Logger logger) {
		this();
		this.logger = logger;
	}

	/**
	 * Executes a deep comparison between the two given objects.
	 * 
	 * @param o1
	 * @param o2
	 * @return The list of differences. Always non null. If this list is empty, then the two objects are identical
	 */
	public List<Difference> compare(Object o1, Object o2) {
		return compare(o1, o2, new ArrayList<>(), "");
	}

	/**
	 * Executes a deep comparison between the two given objects.
	 * 
	 * @param o1
	 * @param o2
	 * @param differences
	 *            The current list of differences. All found differences will be added to this list
	 * @param path
	 *            The current path. When recursing, the field name will be added to the path.
	 * @return The list of differences. Always non null. If this list is empty, then the two objects are identical
	 */
	List<Difference> compare(Object o1, Object o2, List<Difference> differences, String path) {

		if (o1 == null) {
			if (o2 == null) {
				// No comparison to do. We're done.
				return differences;
			} else {
				return addDifference(differences, path, DiffenceType.VALUE, o1, o2, null);
			}
		} else if (o2 == null) {
			return addDifference(differences, path, DiffenceType.VALUE, o1, o2, null);
		}

		// Both objects are non null.

		// They must be of the same type
		if (o1.getClass() != o2.getClass()) {
			return addDifference(differences, path, DiffenceType.TYPE, o1, o2, null);
		}

		// Should we really compare these two objects ?
		if (ignoredClasses.contains(o1.getClass())) {
			// No comparison. We stop here.
			return differences;
		}

		// Basic types are compared by using the equals method
		if (basicClasses.contains(o1.getClass())) {
			if (o1.equals(o2)) {
				// No additional difference
				return differences;
			} else {
				return addDifference(differences, path, DiffenceType.VALUE, o1, o2, null);
			}
		}

		// Collection are tested in a non-ordered way
		if (o1 instanceof Collection<?>) {
			return compareNonOrderedCollection((Collection<?>) o1, (Collection<?>) o2, differences, path);
		}

		// Objects are compared field by field: let's recurse down one level

		// This class is not ignored. We still check that we should execute the comparison.
		// Otherwise, there is a bad configuration somewhere.
		if (!comparedClasses.contains(o1.getClass().getName())) {
			throw new RuntimeException("The " + o1.getClass().getName()
					+ " is not managed by this comparison. Please add this class to one of these lists: basicTypes, objectTypes or ignoredTypes");
		}

		// Ok, let's execute the comparison.
		return compareClasses(o1, o2, o1.getClass(), differences, path);
	}

	/**
	 * Compares two {@link Collection}s. They must be of the same size. And each item of the first collection must be in
	 * the second collection. Here "must be" means that a call to {@link #compare(Object, Object)} returns no
	 * difference.
	 * 
	 * @param o1
	 *            The first collection to compare
	 * @param o2
	 *            The second collection to compare
	 * @param differences
	 *            The current list of differences
	 * @param path
	 *            The current path
	 * @return The updated list of differences. That is: the given list, where the found differences during this
	 *         collection comparison, if any
	 */
	List<Difference> compareNonOrderedCollection(Collection<?> o1, Collection<?> o2, List<Difference> differences,
			String path) {
		if (o1.size() != o2.size()) {
			return addDifference(differences, path, DiffenceType.LIST_SIZE, o1, o2,
					"o1: " + o1.size() + " items, o2: " + o2.size() + " items");
		}

		// Both list have the same size.
		for (Object item1 : o1) {
			// Each item of o1 must exist in o2.
			boolean found = false;
			for (Object item2 : o2) {
				if (compare(item1, item2).size() == 0) {
					found = true;
					break;
				}
			}

			if (!found) {
				// Too bad, item1 was not found in o2.
				differences = addDifference(differences, path, DiffenceType.VALUE, o1, o2,
						"list1 contains the following item but not list2: " + item1.toString());
			}
		}

		return differences;
	}

	/**
	 * Executes a field by field comparison between these two instances of the same class. Every field of every
	 * superclass will also be compared. Fields that are in the {@link #ignoredFields} for this class (whether or not
	 * they are actually defined in this class or in one of its superclasses) will be ignored.
	 * 
	 * @param o1
	 * @param o2
	 * @param differences
	 * @param path
	 * @return
	 */
	List<Difference> compareClasses(Object o1, Object o2, Class<?> clazz, List<Difference> differences, String path) {
		// Let's check the given class
		if (!clazz.isInstance(o1)) {
			throw new RuntimeException("The o1 object is an instance of " + o1.getClass()
					+ ", and is not an instance of the given class: " + clazz.getName());
		}

		// Let's go through all fields declared in this class
		for (Field field : clazz.getDeclaredFields()) {

			// Inner classes contain a strange "this$0" field. Let's ignore it
			if (field.getName().equals("this$0")) {
				continue;
			}

			// Should we ignore this field ?
			Set<String> fields = ignoredFields.get(clazz);
			if (fields != null && fields.contains(field.getName())) {
				// We have to skip this field
				continue;
			}

			// Let's compare this field between the two given objects
			Object val1 = graphqlUtils.invokeGetter(o1, field.getName());
			Object val2 = graphqlUtils.invokeGetter(o2, field.getName());

			// Is there any specific rule for this field ?
			Map<String, ComparisonRule> fieldRulesMap = specificComparisonRules.get(clazz);
			if (fieldRulesMap != null && fieldRulesMap.keySet().contains(field.getName())) {
				// Let's execute this specific rule
				differences = executeSpecificComparisonRule(val1, val2, differences, path + "/" + field.getName(),
						fieldRulesMap.get(field.getName()));
			} else {
				// Let's execute the standard field comparison
				differences = compare(val1, val2, differences, path + "/" + field.getName());
			}
		} // for

		Class<?> superclass = clazz.getSuperclass();
		if (!superclass.getName().equals("java.lang.Object")) {
			// Let's recurse, and compare the superclass's fields
			compareClasses(o1, o2, superclass, differences, path);
		}

		return differences;
	}

	/**
	 * Execute the given comparison rule, on these two values. The comparisonRule will be called only if none of val1
	 * and val2 is null.
	 * 
	 * @param val1
	 * @param val2
	 * @param differences
	 * @param path
	 * @param comparisonRule
	 * @return
	 */
	List<Difference> executeSpecificComparisonRule(Object val1, Object val2, List<Difference> differences, String path,
			ComparisonRule comparisonRule) {
		if (val1 == null && val2 == null) {
			// It's Ok
			return differences;
		} else if (val1 == null || val2 == null) {
			// Only one of the values is null
			return addDifference(differences, path, DiffenceType.VALUE, val1, val2, null);
		}

		// Ok, both values are non null. We can execute the given rule.
		List<Difference> diffs = comparisonRule.compare(val1, val2);

		if (diffs != null) {
			// Then we complete the received differences
			for (Difference d : diffs) {
				d.path = path + d.path;
			}
			differences.addAll(diffs);
		}

		return differences;
	}

	/** Add a {@link Difference} to the given list, from its attribute */
	protected List<Difference> addDifference(List<Difference> differences, String path, DiffenceType diffenceType,
			Object o1, Object o2, String info) {
		differences.add(new Difference(path, diffenceType, o1, o2, info));
		return differences;
	}

	/**
	 * Add a Class as an basic class, that is a class that is compared by using the {@link Object#equals(Object)} method
	 * 
	 * @param clazz
	 */
	public void addBasicClass(Class<?> clazz) {
		basicClasses.add(clazz);
	}

	/**
	 * Add a Class as an object, that is a class that should be compared field by field, not by the
	 * {@link Object#equals(Object)} method
	 * 
	 * @param clazz
	 */
	public void addComparedClass(Class<?> clazz) {
		comparedClasses.add(clazz.getName());
	}

	/**
	 * Add a Class as an ignored class, that is that if a field to compare is of this class, the comparison is skipped
	 * 
	 * @param clazz
	 */
	public void addIgnoredClass(Class<?> clazz) {
		ignoredClasses.add(clazz);
	}

	/**
	 * Add a field of a class as ignored. That is: when comparing this class as an object, this field will be skipped.
	 * 
	 * @param clazz
	 * @param fieldName
	 */
	public void addIgnoredFields(Class<?> clazz, String fieldName) {
		Set<String> fields = ignoredFields.get(clazz);
		if (fields == null) {
			fields = new TreeSet<>();
			ignoredFields.put(clazz, fields);
		}
		fields.add(fieldName);
	}

	/**
	 * Add a comparison rule, for the given field of the given classe. This {@link ComparisonRule} will be called for
	 * each such field of such class that is encountered during the deep comparison.<BR/>
	 * This allows to break cycles (and avoid Stack Overflow exception): if the A.b field is of class B, and the B.a
	 * field is of class A, then you can define specific comparison rule in the {@link #comparisonPredicates} map, so
	 * that when comparing the B.a field, you don't cycle back into the A class. This comparison can be a non operation
	 * method, or a any comparison of you own.
	 * 
	 * @param clazz
	 * @param fieldName
	 * @param comparator
	 */
	public void addSpecificComparisonRules(Class<?> clazz, String fieldName, ComparisonRule comparator) {
		Map<String, ComparisonRule> rules = specificComparisonRules.get(clazz);
		if (rules == null) {
			rules = new HashMap<>();
			specificComparisonRules.put(clazz, rules);
		}
		rules.put(fieldName, comparator);
	}
}
