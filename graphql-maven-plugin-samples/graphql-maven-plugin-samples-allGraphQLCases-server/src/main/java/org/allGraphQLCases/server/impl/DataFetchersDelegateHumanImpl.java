package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.allGraphQLCases.server.SIP_Character_SIS;
import org.allGraphQLCases.server.SEP_Episode_SES;
import org.allGraphQLCases.server.STP_Human_STS;
import org.allGraphQLCases.server.SINP_HumanInput_SINS;
import org.allGraphQLCases.server.DataFetchersDelegateHuman;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author etienne-sf
 *
 */
@Component
public class DataFetchersDelegateHumanImpl implements DataFetchersDelegateHuman {

	@Resource
	DataGenerator generator;

	@Override
	public CompletableFuture<SIP_Character_SIS> bestFriend(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, SIP_Character_SIS> dataLoader, STP_Human_STS source) {
		UUID key = generator.generateInstance(UUID.class);
		return dataLoader.load(key);
	}

	@Override
	public SIP_Character_SIS bestFriend(DataFetchingEnvironment dataFetchingEnvironment, STP_Human_STS origin) {
		return generator.generateInstance(SIP_Character_SIS.class);
	}

	@Override
	public List<SIP_Character_SIS> friends(DataFetchingEnvironment dataFetchingEnvironment, STP_Human_STS source) {
		return generator.generateInstanceList(SIP_Character_SIS.class, 6);
	}

	@Override
	public List<String> comments(DataFetchingEnvironment dataFetchingEnvironment, STP_Human_STS source) {
		return generator.generateInstanceList(String.class, 10);

	}

	@Override
	public List<SEP_Episode_SES> appearsIn(DataFetchingEnvironment dataFetchingEnvironment, STP_Human_STS source) {
		//////////////////////////////////////////////////////////////////////////////////////////
		// If a GraphQL variable of name "appearsIn" exists, we use it. It's stored as a list of Strings
		@SuppressWarnings("unchecked")
		List<String> appearsIn = (List<String>) dataFetchingEnvironment.getVariables().get("appearsIn");

		if (appearsIn != null)
			return appearsIn.stream()//
					.map(s -> SEP_Episode_SES.fromGraphQlValue(s))//
					.collect(Collectors.toList());
		else
			return generator.generateInstanceList(SEP_Episode_SES.class, 2);
	}

	@Override
	public List<STP_Human_STS> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		return generator.generateInstanceList(STP_Human_STS.class, keys.size());
	}

}
