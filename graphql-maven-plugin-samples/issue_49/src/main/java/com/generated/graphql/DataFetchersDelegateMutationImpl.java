package com.generated.graphql;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateMutationImpl implements DataFetchersDelegateMutation {

	@Override
	public Account signupForAccount(DataFetchingEnvironment dataFetchingEnvironment, SignupForAccountInput input) {
		Account account = new Account();
		switch (input.getTitle()) {
		case DR:
			account.setId(1);
			break;
		case MR:
			account.setId(2);
			break;
		case MRS:
			account.setId(3);
			break;
		case MS:
			account.setId(4);
			break;
		}

		return account;
	}

	@Override
	public List<Account> signupForListAccount(DataFetchingEnvironment dataFetchingEnvironment,
			SignupForListAccountInput input) {
		List<Account> listAccount = new ArrayList<>();

		for (Title title : input.getTitles()) {
			int id = 0;
			switch (title) {
			case DR:
				id = 1;
				break;
			case MR:
				id = 2;
				break;
			case MRS:
				id = 3;
				break;
			case MS:
				id = 4;
				break;
			}
			Account a = new Account();
			a.setId(id);
			listAccount.add(a);
		}
		return listAccount;
	}

}
