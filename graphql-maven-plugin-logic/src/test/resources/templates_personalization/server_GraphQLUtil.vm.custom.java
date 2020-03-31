/** This template is custom **/
package ${pluginConfiguration.packageName};

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class GraphQLUtil {

	/**
	 * Transform an {@link Iterable} (which can be a {@link List}), into a {@link List} of items of the same type. It's
	 * usefull to transform the native type from Spring Data repositories (which needs concrete class to map into) into
	 * the list of relevant GraphQL interface
	 * 
	 * @param <I>
	 * @param iterable
	 * @return
	 */
	public <I> List<I> iterableToList(Iterable<I> iterable) {
		List<I> ret = new ArrayList<I>();
		for (I i : iterable) {
			ret.add(i);
		}
		return ret;
	}

	/**
	 * Transform an {@link Iterable} (which can be a {@link List}) of a concrete class, into a {@link List} of the I
	 * interface or superclass. It's usefull to transform the native type from Spring Data repositories (which needs
	 * concrete class to map into) into the list of relevant GraphQL interface
	 * 
	 * @param <I>
	 * @param iterable
	 * @return
	 */
	public <I> List<I> iterableConcreteClassToListInterface(Iterable<? extends I> iterable) {
		List<I> ret = new ArrayList<I>();
		for (I i : iterable) {
			ret.add(i);
		}
		return ret;
	}

	/**
	 * Transform an {@link Optional}, as returned by Spring Data repositories, into a standard Java, which is null if
	 * there is no value.
	 * 
	 * @param optional
	 * @return
	 */
	public <T> T optionalToObject(Optional<T> optional) {
		return optional.isPresent() ? optional.get() : null;
	}

}
