/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.allGraphQLCases.server.SIP_Character_SIS;
import org.allGraphQLCases.server.STP_Droid_STS;
import org.allGraphQLCases.server.SEP_Episode_SES;
import org.allGraphQLCases.server.util.DataFetchersDelegateDroid;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author etienne-sf
 *
 */
@Component
public class DataFetchersDelegateDroidImpl implements DataFetchersDelegateDroid {

	@Resource
	DataGenerator generator;

	@Override
	public List<SIP_Character_SIS> friends(DataFetchingEnvironment dataFetchingEnvironment, STP_Droid_STS source) {
		return generator.generateInstanceList(SIP_Character_SIS.class, 5);
	}

	@Override
	public List<SEP_Episode_SES> appearsIn(DataFetchingEnvironment dataFetchingEnvironment, STP_Droid_STS source) {
		return generator.generateInstanceList(SEP_Episode_SES.class, 2);
	}

	@Override
	public List<STP_Droid_STS> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		return generator.generateInstanceList(STP_Droid_STS.class, keys.size());
	}

}
