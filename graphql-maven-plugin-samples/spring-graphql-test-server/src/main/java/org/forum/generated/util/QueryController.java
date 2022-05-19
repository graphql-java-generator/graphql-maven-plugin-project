/**
 * 
 */
package org.forum.generated.util;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author etienne-sf
 *
 */
@Controller
public class QueryController {

	@Autowired
	DataFetchersDelegateQuery dataFetchersDelegateQuery;

	/**
	 * This method loads the data for Query.boards. <BR/>
	 * 
	 * @param dataFetchingEnvironment
	 *            The GraphQL {@link DataFetchingEnvironment}. It gives you access to the full GraphQL context for this
	 *            DataFetcher
	 * @throws NoSuchElementException
	 *             This method may return a {@link NoSuchElementException} exception. In this case, the exception is
	 *             trapped by the calling method, and the return is consider as null. This allows to use the
	 *             {@link Optional#get()} method directly, without caring of whether or not there is a value. The
	 *             generated code will take care of the {@link NoSuchElementException} exception.
	 */
	@QueryMapping("boards")
	public List<org.forum.generated.Board> boards(DataFetchingEnvironment dataFetchingEnvironment) {
		return dataFetchersDelegateQuery.boards(dataFetchingEnvironment);
	}

	/**
	 * This method loads the data for Query.nbBoards. <BR/>
	 * 
	 * @param dataFetchingEnvironment
	 *            The GraphQL {@link DataFetchingEnvironment}. It gives you access to the full GraphQL context for this
	 *            DataFetcher
	 * @throws NoSuchElementException
	 *             This method may return a {@link NoSuchElementException} exception. In this case, the exception is
	 *             trapped by the calling method, and the return is consider as null. This allows to use the
	 *             {@link Optional#get()} method directly, without caring of whether or not there is a value. The
	 *             generated code will take care of the {@link NoSuchElementException} exception.
	 */
	@QueryMapping("nbBoards")
	public java.lang.Integer nbBoards(DataFetchingEnvironment dataFetchingEnvironment) {
		return dataFetchersDelegateQuery.nbBoards(dataFetchingEnvironment);
	}

	/**
	 * This method loads the data for Query.topics. <BR/>
	 * 
	 * @param dataFetchingEnvironment
	 *            The GraphQL {@link DataFetchingEnvironment}. It gives you access to the full GraphQL context for this
	 *            DataFetcher
	 * @param boardName
	 *            The input parameter sent in the query by the GraphQL consumer, as defined in the GraphQL schema.
	 * @throws NoSuchElementException
	 *             This method may return a {@link NoSuchElementException} exception. In this case, the exception is
	 *             trapped by the calling method, and the return is consider as null. This allows to use the
	 *             {@link Optional#get()} method directly, without caring of whether or not there is a value. The
	 *             generated code will take care of the {@link NoSuchElementException} exception.
	 */
	@QueryMapping("topics")
	public List<org.forum.generated.Topic> topics(DataFetchingEnvironment dataFetchingEnvironment,
			@Argument("boardName") java.lang.String boardName) {
		return dataFetchersDelegateQuery.topics(dataFetchingEnvironment, boardName);
	}

	/**
	 * This method loads the data for Query.findTopics. <BR/>
	 * 
	 * @param dataFetchingEnvironment
	 *            The GraphQL {@link DataFetchingEnvironment}. It gives you access to the full GraphQL context for this
	 *            DataFetcher
	 * @param boardName
	 *            The input parameter sent in the query by the GraphQL consumer, as defined in the GraphQL schema.
	 * @param keyword
	 *            The input parameter sent in the query by the GraphQL consumer, as defined in the GraphQL schema.
	 * @throws NoSuchElementException
	 *             This method may return a {@link NoSuchElementException} exception. In this case, the exception is
	 *             trapped by the calling method, and the return is consider as null. This allows to use the
	 *             {@link Optional#get()} method directly, without caring of whether or not there is a value. The
	 *             generated code will take care of the {@link NoSuchElementException} exception.
	 */
	@QueryMapping("findTopics")
	public List<org.forum.generated.Topic> findTopics(DataFetchingEnvironment dataFetchingEnvironment,
			@Argument("boardName") java.lang.String boardName, @Argument("keyword") List<java.lang.String> keyword) {
		return dataFetchersDelegateQuery.findTopics(dataFetchingEnvironment, boardName, keyword);
	}

}
