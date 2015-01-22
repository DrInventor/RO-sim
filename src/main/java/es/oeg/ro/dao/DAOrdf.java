package es.oeg.ro.dao;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.DCTerms;

import es.oeg.ro.transfer.Author;
import es.oeg.ro.transfer.Authors;

public class DAOrdf {

	private final Property dCTermsProperty = DCTerms.creator;

	private Authors listAuthors = new Authors();	


		public void updatesAuthorInformation(Model model){
			// take the authors of the RO and update the matrix of author
			List<String> authors = getAuthors(model);
			for (String author: authors){
				// check if already exists
				
				Author auth = listAuthors.search(author); 
				if (auth == null){
					auth = new Author(author);
					// TODO -> save to some db
					listAuthors.add(auth);
					auth.incrementPublication();
				}
				else
					auth.incrementPublication();
				
			}
		}
		
		private List<String> getAuthors(Model model) {
			List<String> allAuthors = new ArrayList<String>();
			List<RDFNode> list = getDCCreators(model);
			for (RDFNode node : list){
				allAuthors.add(node.asLiteral().getString());
			}
			return allAuthors;
		}

		// retrieve all the statements with dc:creator property
		private List<RDFNode> getDCCreators(Model m) throws NullPointerException{
			if (m == null) throw new NullPointerException("Parameter cannot be null");		
			return m.listObjectsOfProperty(dCTermsProperty).toList();		
		}
		
}
