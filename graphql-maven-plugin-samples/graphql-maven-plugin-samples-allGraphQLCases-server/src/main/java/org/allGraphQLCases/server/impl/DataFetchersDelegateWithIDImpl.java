/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.allGraphQLCases.server.WithID;
import org.allGraphQLCases.server.util.DataFetchersDelegateWithID;
import org.dataloader.BatchLoaderEnvironment;
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
	public List<WithID> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		return generator.generateInstanceList(WithID.class, keys.size());
	}

}
