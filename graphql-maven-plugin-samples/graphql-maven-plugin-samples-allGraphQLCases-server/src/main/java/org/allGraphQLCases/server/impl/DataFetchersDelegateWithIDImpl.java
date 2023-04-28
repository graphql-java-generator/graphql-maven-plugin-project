/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.allGraphQLCases.server.DataFetchersDelegateWithID;
import org.allGraphQLCases.server.SIP_WithID_SIS;
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
	public List<SIP_WithID_SIS> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		return generator.generateInstanceList(SIP_WithID_SIS.class, keys.size());
	}

}
