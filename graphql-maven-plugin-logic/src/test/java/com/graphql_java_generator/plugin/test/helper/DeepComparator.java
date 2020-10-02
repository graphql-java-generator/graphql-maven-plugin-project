/**
 * 
 */
package com.graphql_java_generator.plugin.test.helper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphql_java_generator.GraphqlUtils;

/**
 * This class deep compares two objects, say a and b. It:
 * <UL>
 * <LI>Checks that a and b are of the same class</LI>
 * <LI>Compares each field of a to each field of b (the fields defined in their superclasses are also scanned):</LI>
 * <LI>If the field is an id field, and a.id.equals(b.id), then a and b are considered as being the same. The comparison
 * will check that there other attributes are equals, and if not these differences are registered as differences of the
 * same object (with the key being the value of the id field, like the key in a Map). See
 * {@link #addIdField(Class, String)} for more information on this</LI>
 * <LI>If the field is an ignored field, this field is skipped</LI>
 * <LI>If the field is of a basic type: use the equals method. All enums should be registered in the basic type
 * list.</LI>
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
 * {@link #differences(Object, Object)} method, to allow further processing. This makes it possible to correct these
 * differences.
 * 
 * 
 * @author etienne-sf
 */
public class DeepComparator implements Cloneable {

	/** The logger for this class. It can be overriden by using the relevant constructor */
	Logger logger = LoggerFactory.getLogger(this.getClass());

	GraphqlUtils graphqlUtils = GraphqlUtils.graphqlUtils;

	/**
	 * The list of basic types. For these types, the comparison is done by calling the {@link Object#equals(Object)}
	 * method
	 */
	List<Class<?>> basicClasses = new ArrayList<>();

	// /**
	// * The list of objects, that will be compared field by field. For each of these objects, the list of fields to
	// * ignore during the comparison can be set, by using the {@link #ignoredFields} map.
	// */
	// Set<String> comparedClasses = new TreeSet<>();

	/** The list of objects that will be ignored during the comparison: all fields of this type are skipped. */
	List<Class<?>> ignoredClasses = new ArrayList<>();

	/** @see #addIdField(Class, String) */
	Map<Class<?>, String> idFields = new HashMap<>();

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

	/**
	 * Contains the list of couple of instances that has been compared. This is used to avoid cycles, by identifying if
	 * this couple has already been compared.<BR/>
	 * The comparison result is stored (the list of differences), so that it can be reused each time this comparison is
	 * executed.
	 */
	Map<ExecutedComparison, List<Difference>> executedComparisons = new TreeMap<>();

	public enum DifferenceType {
		/** This difference's type means that the two objects are not of the same class */
		CLASS,
		/**
		 * This difference's type means that the two objects are of the same class, but:
		 * <UL>
		 * <LI>For Collection: there are differences in the content of the collection</LI>
		 * <LI>For other objects: at leasts one attribute has a different value</LI>
		 * </UL>
		 */
		VALUE,
		/** This difference's type means that the two objects are collections, but not of the same size */
		LIST_SIZE
	}

	public static class Difference {
		/**
		 * The path the show how the object structure has been crawled. For instance, if the comparison starts with an
		 * object o1, it compared the field <I>field1</I>, which is of type B, then its field <I>field2</I> then found a
		 * difference, then the path is <I>/field1/field2</I>
		 */
		public String path;
		public DifferenceType type;
		public Object value1;
		public Object value2;
		/** Some additional information, especially for the {@link Collection}s */
		public String info;

		public Difference(String path, DifferenceType diffenceType, Object value1, Object value2, String info) {
			this.path = path;
			this.type = diffenceType;
			this.value1 = value1;
			this.value2 = value2;
			this.info = info;
		}
	}

	/**
	 * This classes contains one pair of compared objects. When creating a new instance, with a couple of objects
	 * (obj1,obj2), the {@link ExecutedComparison#o1} attribute receives the one of (obj1,obj2) which has the lower
	 * identity hash code, and o2 the other. This allows to properly compare two instances of this class: it makes
	 * equals instances that have been created by either <I>new ExecutedComparison(o1,o2)</I> or <I>new
	 * ExecutedComparison(o2,o1)</I>.<BR/>
	 * The only aim of this class, is to be the key of the {@link #executedComparisons} comparison map. Its objective is
	 * to avoid to do two times the same comparison. One big gain here, is to avoid loops.
	 */
	static class ExecutedComparison implements Comparable<ExecutedComparison> {
		Object o1;
		Object o2;
		String id1;
		String id2;// id1 > id2

		ExecutedComparison(Object o1, Object o2) {
			if (o1 == null) {
				throw new NullPointerException("o1 may not be null");
			}
			if (o2 == null) {
				throw new NullPointerException("o2 may not be null");
			}
			String id1 = Integer.toHexString(System.identityHashCode(o1));
			String id2 = Integer.toHexString(System.identityHashCode(o2));
			if (id1.compareTo(id2) > 0) {
				this.o1 = o1;
				this.id1 = id1;
				this.o2 = o2;
				this.id2 = id2;
			} else {
				this.o1 = o2;
				this.id1 = id2;
				this.o2 = o1;
				this.id2 = id1;
			}
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || !(o instanceof ExecutedComparison)) {
				return false;
			}
			ExecutedComparison ec = (ExecutedComparison) o;
			// o1 and o2 are stored ordered (o1 > o2. So this comparison is enough:
			return ec.o1 == this.o1 && ec.o2 == this.o2;
		}

		@Override
		public int compareTo(ExecutedComparison ec) {
			if (ec == null) {
				throw new NullPointerException("ec may not be null");
			}

			int ret = this.id1.compareTo(ec.id1);
			if (ret == 0) {
				ret = this.id2.compareTo(ec.id2);
			}
			return ret;
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
		 * @param nbMaxDifferences
		 *            The maximum number of differences to return. This allows to limit the size of the returned list,
		 *            and to accelerate the comparison. <BR/>
		 *            Setting 1 here will stop as soon as a difference is found.<BR/>
		 *            Setting it to a negative value prevents any comparison to occur.<BR/>
		 *            Set it to Integer.MAX_VALUE to have (almost) no limit on the number of returned differences
		 * @return A non null list. If it's empty, then there is no difference between o1 and o2. The root path for the
		 *         returned {@link Difference}s is this field, starting by a slash (/). If differences are returned by
		 *         this method, they will be updated with the current path of the comparison when this method was
		 *         called.
		 */
		public List<Difference> compare(Object o1, Object o2, int nbMaxDifferences);
	}

	public DeepComparator() {
		// Let's configure the default list of basic types (they will be compared using the equals method)
		basicClasses.add(boolean.class);
		basicClasses.add(Boolean.class);
		basicClasses.add(char.class);
		basicClasses.add(Character.class);
		basicClasses.add(Enum.class);
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
	 * @return true if no differences have been found, during the deep comparison. False if at least one difference has
	 *         been found.
	 */
	public boolean equals(Object o1, Object o2) {
		// We start a new comparison.
		DeepComparator deepComparator = clone();

		return deepComparator.differences(o1, o2, 1, "").size() == 0;
	}

	/**
	 * Executes a deep comparison between the two given objects. All differences are reported.<BR/>
	 * If you just want to know if these two objects are different, you can use the {@link #differences(Object, Object)}
	 * method, which will stop the comparison as soon as a difference is found. It may be much faster.
	 * 
	 * @param o1
	 * @param o2
	 * @param nbMaxDifferences
	 *            The maximum number of differences to return. This allows to limit the size of the returned list, and
	 *            to accelerate the comparison. <BR/>
	 *            Setting 1 here will stop as soon as a difference is found.<BR/>
	 *            Setting it to a negative value prevents any comparison to occur.<BR/>
	 *            Set it to Integer.MAX_VALUE to have (almost) no limit on the number of returned differences
	 * @return The list of differences. Always non null. If this list is empty, then the two objects are identical
	 */
	public List<Difference> differences(Object o1, Object o2, int nbMaxDifferences) {
		// We start a new comparison.
		DeepComparator deepComparator = clone();

		return deepComparator.differences(o1, o2, nbMaxDifferences, "");
	}

	/**
	 * Executes a deep comparison between the two given objects. This method is internal: it used when recursing into
	 * objects of a collection. The path parameter allows to log where the comparison is located, when in debug log
	 * level.<BR/>
	 * This method stops after the first difference found.
	 * 
	 * @param o1
	 * @param o2
	 * @return true if no differences have been found, during the deep comparison. False if at least one difference has
	 *         been found.
	 */
	public boolean compare(Object o1, Object o2) {
		return differences(o1, o2, 1).size() == 0;
	}

	/**
	 * Executes a deep comparison between the two given objects. This method is internal: it used when recursing into
	 * objects of a collection. The path parameter allows to log where the comparison is located, when in debug log
	 * level.<BR/>
	 * This method stops after the first difference found.
	 * 
	 * @param o1
	 * @param o2
	 * @param differences
	 *            The current list of differences. All found differences will be added to this list
	 * @param nbMaxDifferences
	 *            The maximum number of differences to return. This allows to limit the size of the returned list, and
	 *            to accelerate the comparison. <BR/>
	 *            Setting 1 here will stop as soon as a difference is found.<BR/>
	 *            Setting it to a negative value prevents any comparison to occur.<BR/>
	 *            Set it to Integer.MAX_VALUE to have (almost) no limit on the number of returned differences
	 * @param path
	 *            The current path. When recursing, the field name will be added to the path.
	 * @return The list of differences. Always non null. If this list is empty, then the two objects are identical
	 */
	private List<Difference> differences(Object o1, Object o2, int nbMaxDifferences, String path) {

		if (logger.isTraceEnabled()) {
			logger.trace("Starting comparison between " + o1 + " and " + o2 + " (on path: " + path
					+ ", with nbMaxDifferences=" + nbMaxDifferences + ")");
		}

		// Let's first check the case where one or the two objects are null
		if (o1 == null) {
			if (o2 == null) {
				// No comparison to do. We're done.
				return new ArrayList<>();
			} else {
				return addDifference(null, path, DifferenceType.VALUE, o1, o2, null);
			}
		} else if (o2 == null) {
			return addDifference(null, path, DifferenceType.VALUE, o1, o2, null);
		}

		// Both objects are non null.

		String object1 = null;
		String object2 = null;
		if (logger.isDebugEnabled()) {
			try {
				object1 = o1.getClass().getSimpleName() + "(name=" + (String) graphqlUtils.invokeGetter(o1, "name")
						+ ")";
				object2 = o2.getClass().getSimpleName() + "(name=" + (String) graphqlUtils.invokeGetter(o2, "name")
						+ ")";
			} catch (RuntimeException e) {
				// There is no name attribute in this object
				object1 = o1.getClass().getSimpleName();
				object2 = o2.getClass().getSimpleName();
			}
		}

		// They must be of the same type
		if (o1.getClass() != o2.getClass()) {
			return addDifference(null, path, DifferenceType.CLASS, o1, o2, null);
		}

		// Should we really compare these two objects ?
		if (ignoredClasses.contains(o1.getClass())) {
			// No comparison. We stop here.
			return new ArrayList<>();
		}

		// Basic types are compared by using the equals method
		if (basicClasses.contains(o1.getClass()) || o1 instanceof Enum) {
			if (o1.equals(o2)) {
				// No additional difference
				return new ArrayList<>();
			} else {
				return addDifference(null, path, DifferenceType.VALUE, o1, o2, null);
			}
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////// Ok, we are in a case of deep comparison. Shall we continue? ///////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////

		// Has this couple already been compared ?
		ExecutedComparison theCurrentComparison = new ExecutedComparison(o1, o2);
		List<Difference> alreadyFoundDifferences = executedComparisons.get(theCurrentComparison);
		if (alreadyFoundDifferences != null) {
			if (logger.isTraceEnabled()) {
				logger.trace("Comparison already done for " + object1 + " and " + object2);
			}
			return alreadyFoundDifferences;
		}

		// Let's write this couple in the comparison map, to avoid cycles. We set the differences list as an empty list,
		// and we'll correct this at the end of this method, once we actually know the differences.
		executedComparisons.put(theCurrentComparison, new ArrayList<>());

		////////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////// Ok, we need to execute the deep comparison ////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////

		List<Difference> additionalDifferences;

		// Collection are tested in a non-ordered way
		if (o1 instanceof Collection<?>) {
			additionalDifferences = compareNonOrderedCollection((Collection<?>) o1, (Collection<?>) o2,
					nbMaxDifferences, path);
		}

		// Maps are a special beast
		else if (o1 instanceof Map<?, ?>) {
			additionalDifferences = compareMap((Map<?, ?>) o1, (Map<?, ?>) o2, nbMaxDifferences, path);
		}

		// Objects are compared field by field: let's recurse down one level

		// // This class is not ignored. We still check that we should execute the comparison.
		// // Otherwise, there is a bad configuration somewhere.
		// if (!comparedClasses.contains(o1.getClass().getName())) {
		// throw new RuntimeException("The " + o1.getClass().getName()
		// + " is not managed by this comparison. Please add this class to one of these lists: basicTypes, objectTypes
		// or ignoredTypes");
		// }

		// Ok, let's execute the field by field comparison.
		else {
			additionalDifferences = compareObjects(o1, o2, o1.getClass(), nbMaxDifferences, path);
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Ok, we've done the deep comparison. And all newly found differences are in additionalDifferences
		////////////////////////////////////////////////////////////////////////////////////////////////////

		// Let's add this result to reuse it, and to avoid to do it again
		if (logger.isDebugEnabled()) {
			logger.debug("nb diff=" + additionalDifferences.size() + " - Comparison path: " + path + "  -  Comparing "
					+ object1 + " to " + object2);
		}

		// Let's write this couple in the comparison map, to avoid cycles, and to reuse it if possible
		// This should not add a new item in the map, but replace the previous one (which maps to an amepty list)
		int nbExecutedComparison = executedComparisons.keySet().size();
		executedComparisons.put(theCurrentComparison, additionalDifferences);
		if (executedComparisons.keySet().size() != nbExecutedComparison) {
			throw new RuntimeException(
					"[Internal error] A new ExecutedComparison has been added, instead of replacing the previous one (with o1="
							+ o1 + " and o2=" + o2 + ")");
		}

		return additionalDifferences;
	}

	/**
	 * Compares two {@link Collection}s. They must be of the same size. And each item of the first collection must be in
	 * the second collection. Here "must be" means that a call to {@link #differences(Object, Object)} returns no
	 * difference.
	 * 
	 * @param o1
	 *            The first collection to compare
	 * @param o2
	 *            The second collection to compare
	 * @param nbMaxDifferences
	 *            The maximum number of differences to return. This allows to limit the size of the returned list, and
	 *            to accelerate the comparison. <BR/>
	 *            Setting 1 here will stop as soon as a difference is found.<BR/>
	 *            Setting it to a negative value prevents any comparison to occur.<BR/>
	 *            Set it to Integer.MAX_VALUE to have (almost) no limit on the number of returned differences
	 * @param path
	 *            The current path
	 * @return The updated list of differences. That is: the given list, where the found differences during this
	 *         collection comparison, if any
	 */
	List<Difference> compareNonOrderedCollection(Collection<?> o1, Collection<?> o2, int nbMaxDifferences,
			String path) {

		List<Difference> differences = new ArrayList<>();

		if (o1.size() != o2.size()) {
			differences.add(new Difference(path, DifferenceType.LIST_SIZE, o1, o2,
					"o1: " + o1.size() + " items, o2: " + o2.size() + " items"));
			nbMaxDifferences -= 1;
		}

		// Let's look for all items in o1 that doesn't exist in o2
		if (nbMaxDifferences > 0) {
			for (Object item1 : o1) {
				// Each item of o1 must exist in o2.
				boolean found = false;
				for (Object item2 : o2) {
					if (compare(item1, item2)) {
						found = true;
						break;
					}
				}

				if (!found) {
					// Too bad, item1 was not found in o2.
					differences.add(new Difference(path, DifferenceType.VALUE, item1, null,
							"list1 contains the following item but not list2: " + item1.toString()));

					if (--nbMaxDifferences <= 0) {
						// We've reached the limit, in the number of expected differences. Let's stop here.
						break;
					}
				}
			} // for
		}

		// Let's look for all items in o2 that doesn't exist in o1
		if (nbMaxDifferences > 0) {
			for (Object item2 : o2) {
				boolean found = false;
				for (Object item1 : o1) {
					if (compare(item1, item2)) {
						found = true;
						break;
					}
				}

				if (!found) {
					// Too bad, item1 was not found in o2.
					differences.add(new Difference(path, DifferenceType.VALUE, null, item2,
							"list2 contains the following item but not list1: " + item2.toString()));

					if (--nbMaxDifferences <= 0) {
						// We've reached the limit, in the number of expected differences. Let's stop here.
						break;
					}
				}
			} // for
		}

		return differences;
	}

	/**
	 * Comparison between two {@link Map}s
	 * 
	 * @param o1
	 * @param o2
	 * @param nbMaxDifferences
	 *            The maximum number of differences to return. This allows to limit the size of the returned list, and
	 *            to accelerate the comparison. <BR/>
	 *            Setting 1 here will stop as soon as a difference is found.<BR/>
	 *            Setting it to a negative value prevents any comparison to occur.<BR/>
	 *            Set it to Integer.MAX_VALUE to have (almost) no limit on the number of returned differences
	 * @param path
	 * @return
	 */
	List<Difference> compareMap(Map<?, ?> o1, Map<?, ?> o2, int nbMaxDifferences, String path) {
		List<Difference> differences = new ArrayList<>();

		// First step: compare the keys
		differences.addAll(differences(o1.keySet(), o2.keySet(), nbMaxDifferences, path + "/keys"));

		// Then, let's give a try on the values.
		for (Object key : o1.keySet()) {
			if (--nbMaxDifferences <= 0) {
				// We've reached the limit, in the number of expected differences. Let's stop here.
				break;
			}
			Object val1 = o1.get(key);
			Object val2 = o2.get(key);
			differences.addAll(differences(val1, val2, nbMaxDifferences, path + "[" + key + "]"));
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
	 * @param nbMaxDifferences
	 *            The maximum number of differences to return. This allows to limit the size of the returned list, and
	 *            to accelerate the comparison. <BR/>
	 *            Setting 1 here will stop as soon as a difference is found.<BR/>
	 *            Setting it to a negative value prevents any comparison to occur.<BR/>
	 *            Set it to Integer.MAX_VALUE to have (almost) no limit on the number of returned differences
	 * @param clazz
	 *            The class on which the comparison should be executed: this method should be called with the real o1
	 *            class. The this method iterates through all it superclasses. This allows to use the
	 *            {@link Class#getDeclaredFields()} method.
	 * @param path
	 * @return
	 */
	List<Difference> compareObjects(Object o1, Object o2, Class<?> clazz, int nbMaxDifferences, String path) {
		List<Difference> differences = new ArrayList<>();

		// Let's check the given class
		if (!clazz.isInstance(o1)) {
			throw new RuntimeException("[Internal Error] The o1 object is an instance of " + o1.getClass()
					+ ", and is not an instance of the given class: " + clazz.getName());
		}
		if (!clazz.isInstance(o2)) {
			throw new RuntimeException("[Internal Error] The o2 object is an instance of " + o2.getClass()
					+ ", and is not an instance of the given class: " + clazz.getName());
		}

		// Does this class have an id field declared ?
		String idField = idFields.get(clazz);
		if (idField != null) {
			Object val1 = graphqlUtils.invokeGetter(o1, idField);
			Object val2 = graphqlUtils.invokeGetter(o2, idField);
			if (compare(val1, val2)) {
				// The id field are identical. We add this information in the current path, so that differences (if any)
				// are seen as attribute in this same object.
				path = path + "/" + clazz.getSimpleName() + "(" + idField + ":" + val1 + ")";
			}
		}

		// Let's go through all fields declared in this class
		for (Field field : clazz.getDeclaredFields()) {

			// We ignore constants
			if (Modifier.isFinal(field.getModifiers())) {
				// We skip the constants
				continue;
			}

			// Inner non static classes contain a strange "this$0" field. Let's ignore it
			if (field.getName().equals("this$0")) {
				continue;
			}

			// Should we ignore this field ?
			Set<String> ignoreFields = ignoredFields.get(o1.getClass());
			if (ignoreFields != null && ignoreFields.contains(field.getName())) {
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
				differences.addAll(executeSpecificComparisonRule(val1, val2, nbMaxDifferences,
						path + "/" + field.getName(), fieldRulesMap.get(field.getName())));
			} else {
				// Let's execute the standard field comparison
				differences.addAll(differences(val1, val2, nbMaxDifferences, path + "/" + field.getName()));
			}
		} // for

		Class<?> superclass = clazz.getSuperclass();
		if (!superclass.getName().equals("java.lang.Object")) {
			if (nbMaxDifferences > differences.size()) {
				// Let's recurse, and compare the superclass's fields
				differences.addAll(compareObjects(o1, o2, superclass, nbMaxDifferences - differences.size(), path));
			}
		}

		return differences;
	}

	/**
	 * Execute the given comparison rule, on these two values. The comparisonRule will be called only if none of val1
	 * and val2 is null.
	 * 
	 * @param val1
	 * @param val2
	 * @param nbMaxDifferences
	 *            The maximum number of differences to return. This allows to limit the size of the returned list, and
	 *            to accelerate the comparison. <BR/>
	 *            Setting 1 here will stop as soon as a difference is found.<BR/>
	 *            Setting it to a negative value prevents any comparison to occur.<BR/>
	 *            Set it to Integer.MAX_VALUE to have (almost) no limit on the number of returned differences
	 * @param path
	 * @param comparisonRule
	 * @return
	 */
	List<Difference> executeSpecificComparisonRule(Object val1, Object val2, int nbMaxDifferences, String path,
			ComparisonRule comparisonRule) {
		List<Difference> diffs = new ArrayList<>();

		if (val1 == null && val2 == null) {
			// It's Ok
			return diffs;
		} else if (val1 == null || val2 == null) {
			// Only one of the values is null
			return addDifference(diffs, path, DifferenceType.VALUE, val1, val2, null);
		} else {
			// Ok, both values are non null. We can execute the given rule.
			diffs = comparisonRule.compare(val1, val2, nbMaxDifferences);

			if (diffs == null) {
				// The return of this method may not be null
				diffs = new ArrayList<>();
			} else {
				// Then we complete the received differences, to have the correct path, related to the current
				// comparison step
				for (Difference d : diffs) {
					d.path = path + d.path;
				}
			}

			return diffs;
		}

	}

	/** Add a {@link Difference} to the given list, from its attribute */
	private List<Difference> addDifference(List<Difference> differences, String path, DifferenceType diffenceType,
			Object o1, Object o2, String info) {
		if (differences == null) {
			differences = new ArrayList<>();
		}
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

	// /**
	// * // * Add a Class as an object, that is a class that should be compared field by field, not by the // *
	// * {@link Object#equals(Object)} method // * // * @param clazz //
	// */
	// public void addComparedClass(Class<?> clazz) {
	// comparedClasses.add(clazz.getName());
	// }

	/**
	 * Add a Class as an ignored class, that is that if a field to compare is of this class, the comparison is skipped
	 * 
	 * @param clazz
	 */
	public void addIgnoredClass(Class<?> clazz) {
		ignoredClasses.add(clazz);
	}

	/**
	 * Add an idField for a class. An idField is a field that identify an instance of a given class.Two objects with the
	 * same value in their id field are same (for instance two persons of the same name), but may not be equals (other
	 * attributes differ). This allows to trace differences in two objects that are the same.<BR/>
	 * For classes that has such a field, the rules are :
	 * <UL>
	 * <LI>If the id field of the two instances are differents, generates a difference with the current difference's
	 * path</LI>
	 * <LI>If the id field of the two instances are equals, add /Xxx(idField:id) to the difference's path (where
	 * <I>Xxx</I> is the classname, <I>idField</I> is name if the field/attribute that has been compared and <I>id</I>
	 * is the value for this id field as a String), and loop for the other attributes</LI>
	 * </UL>
	 * 
	 * When such a field is defined for a class, when compariring tw will be ignored, for each class. The key is the
	 * class. The value is the list of ignored field names for this class. The ignored fields should contain all ignored
	 * fields for this class, including those which are actually defined in its superclasses. This allow to choose if a
	 * field is ignored or not per subclass (if there are two subclasses for one superclasse).
	 * 
	 * @param clazz
	 * @param fieldName
	 */
	public void addIdField(Class<?> clazz, String fieldName) {
		idFields.put(clazz, fieldName);
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

	/**
	 * Returns a fresh comparator, that is ready to execute a new Comparison. The {@link #executedComparisons} map is
	 * free. The various configuration lists are set in the clone with the exact same list as the original one. That
	 * means that added (for instance) a basic class, will add it for both the clone and the original object.<BR/>
	 * But the {@link #executedComparisons} remains specific to each instance.
	 */
	@Override
	protected DeepComparator clone() {
		DeepComparator ret = new DeepComparator();
		ret.basicClasses = basicClasses;
		ret.ignoredClasses = ignoredClasses;
		ret.idFields = idFields;
		ret.ignoredFields = ignoredFields;
		ret.specificComparisonRules = specificComparisonRules;
		// The executedComparisons is not cloned: this allows to start a new comparison, with the same parameters.
		return ret;
	}

}
