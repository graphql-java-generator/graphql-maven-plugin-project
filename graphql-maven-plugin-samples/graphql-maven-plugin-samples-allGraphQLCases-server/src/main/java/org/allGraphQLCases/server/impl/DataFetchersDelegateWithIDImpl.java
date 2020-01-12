/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.DataFetchersDelegateWithID;
import org.allGraphQLCases.server.WithID;
import org.springframework.stereotype.Component;

/**
 * @author EtienneSF
 *
 */
@Component
public class DataFetchersDelegateWithIDImpl implements DataFetchersDelegateWithID {

	@Override
	public List<WithID> batchLoader(List<UUID> keys) {
		// TODO Auto-generated method stub
		return null;
	}

}
