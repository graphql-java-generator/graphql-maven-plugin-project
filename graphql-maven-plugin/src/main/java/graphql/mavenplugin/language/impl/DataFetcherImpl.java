/**
 * 
 */
package graphql.mavenplugin.language.impl;

import graphql.mavenplugin.language.DataFetcher;
import graphql.mavenplugin.language.DataFetcherDelegate;
import graphql.mavenplugin.language.Field;
import lombok.Data;

/**
 * @author EtienneSF
 */
@Data
public class DataFetcherImpl implements DataFetcher {

	private Field field;

	private DataFetcherDelegate dataFetcherDelegate;

	/**
	 * 
	 * @param field
	 *            The field that this data fetcher must fill
	 */
	public DataFetcherImpl(Field field) {
		this.field = field;
	}

	@Override
	public String getName() {
		return field.getOwningType().getName() + field.getPascalCaseName();
	}

}
