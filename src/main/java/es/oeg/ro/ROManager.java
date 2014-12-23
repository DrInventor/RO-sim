package es.oeg.ro;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.DCTerms;

import es.oeg.ro.dao.DAOpapers;
import es.oeg.ro.transfer.ADSLabsResultsBean;
import es.oeg.ro.transfer.Author;
import es.oeg.ro.transfer.Authors;
import es.oeg.ro.transfer.Paper;

public class ROManager {

	private final Property dCTermsProperty = DCTerms.creator;

	Logger logger = LoggerFactory.getLogger(this.getClass());
	Authors listAuthors = new Authors();
	private DAOpapers daopapers = new DAOpapers();

	// from RDF
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
	
	
	public void writeResultsToFileJSON(){
		ObjectMapper mapper = new ObjectMapper();		
		//FIXME cambiar ruta de fichero y lo que se guarda
		try {	 
			// convert user object to json string, and save to a file
			mapper.writeValue(new File("d:\\user.json"), listAuthors);	 
			// display to console
			logger.info(mapper.writeValueAsString(listAuthors));
	 
		} catch (JsonGenerationException e) {	 
			e.printStackTrace();	 
		} catch (JsonMappingException e) {	 
			e.printStackTrace();	 
		} catch (IOException e) {	 
			e.printStackTrace();	 
		}
		
	}
	
	// get results from API and save authors to DB
	public void addAuthor(){
		
	}
	
	public void saveToDatabase(ADSLabsResultsBean result) {
		if (result != null){
			for(Paper p: result.getResults().get_docs()){
				logger.debug("Paper added: "+p.toString());
				daopapers.add(p);				
			}
		}


	}
}
