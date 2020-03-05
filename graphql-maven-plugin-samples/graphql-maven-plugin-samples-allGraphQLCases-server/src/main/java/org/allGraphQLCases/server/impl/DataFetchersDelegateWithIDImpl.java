/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.allGraphQLCases.server.DataFetchersDelegateWithID;
import org.allGraphQLCases.server.WithID;
import org.springframework.stereotype.Component;

/**
 * @author etienne-sf
 *
 */
@Component
public class DataFetchersDelegateWithIDImpl implements DataFetchersDelegateWithID {

	@Resource
	DataGenerator generator;

	@Override
	public List<WithID> batchLoader(List<UUID> keys) {
		return generator.generateInstanceList(WithID.class, keys.size());
	}

}
