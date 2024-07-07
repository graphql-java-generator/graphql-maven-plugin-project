/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.allGraphQLCases.server.DataFetchersDelegateCharacter;
import org.allGraphQLCases.server.SEP_Episode_SES;
import org.allGraphQLCases.server.SIP_Character_SIS;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author etienne-sf
 *
 */
@Component
public class DataFetchersDelegateCharacterImpl implements DataFetchersDelegateCharacter {

	@Resource
	DataGenerator generator;

	@Override
	public List<SIP_Character_SIS> friends(DataFetchingEnvironment dataFetchingEnvironment, SIP_Character_SIS source) {
		return this.generator.generateInstanceList(SIP_Character_SIS.class, 4);
	}

	@Override
	public List<SEP_Episode_SES> appearsIn(DataFetchingEnvironment dataFetchingEnvironment, SIP_Character_SIS source) {
		return this.generator.generateInstanceList(SEP_Episode_SES.class, 2);
	}

	@Override
	public List<SIP_Character_SIS> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		return this.generator.generateInstanceList(SIP_Character_SIS.class, keys.size());
	}

}
