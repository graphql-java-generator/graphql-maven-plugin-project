package org.allGraphQLCases.server.impl;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Resource;

import org.allGraphQLCases.server.DataFetchersDelegateReservedJavaKeywordAllFieldCases;
import org.allGraphQLCases.server.SIP_WithID_SIS;
import org.allGraphQLCases.server.STP_Human_STS;
import org.allGraphQLCases.server.STP_ReservedJavaKeywordAllFieldCases_STS;
import org.allGraphQLCases.server.SUP_AnyCharacter_SUS;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateReservedJavaKeywordAllFieldCasesImpl
		implements DataFetchersDelegateReservedJavaKeywordAllFieldCases {

	@Resource
	DataGenerator generator;

	@Override
	public CompletableFuture<SIP_WithID_SIS> _implements(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, SIP_WithID_SIS> dataLoader, STP_ReservedJavaKeywordAllFieldCases_STS origin) {
		return CompletableFuture.completedFuture(this.generator.generateInstance(SIP_WithID_SIS.class));
	}

	@Override
	public CompletableFuture<STP_Human_STS> _int(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, STP_Human_STS> dataLoader, STP_ReservedJavaKeywordAllFieldCases_STS origin) {
		return CompletableFuture.completedFuture(this.generator.generateInstance(STP_Human_STS.class));
	}

	@Override
	public SUP_AnyCharacter_SUS _interface(DataFetchingEnvironment dataFetchingEnvironment,
			STP_ReservedJavaKeywordAllFieldCases_STS origin) {
		return this.generator.generateInstance(SUP_AnyCharacter_SUS.class);
	}

	@Override
	public CompletableFuture<SIP_WithID_SIS> nonJavaKeywordField(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, SIP_WithID_SIS> dataLoader, STP_ReservedJavaKeywordAllFieldCases_STS origin) {
		return CompletableFuture.completedFuture(this.generator.generateInstance(SIP_WithID_SIS.class));
	}

}
