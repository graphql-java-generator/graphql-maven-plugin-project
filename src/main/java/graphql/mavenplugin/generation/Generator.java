/**
 * 
 */
package graphql.mavenplugin.generation;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import graphql.language.Document;

/**
 * @author EtienneSF
 */
@Component
public class Generator {

	@Resource
	List<Document> documents;

	/**
	 * The main method of the class: it executes the generation of the given documents
	 * 
	 * @param documents
	 *            The graphql definition schema, from which the code is to be generated
	 * @return
	 */
	public int generateTargetFiles() {
		return documents.stream().mapToInt(this::generateForOneDocument).sum();
	}

	/**
	 * Generates the target classes for the given graphql schema definition
	 * 
	 * @param document
	 */
	int generateForOneDocument(Document document) {
		return 0;
	}

}
