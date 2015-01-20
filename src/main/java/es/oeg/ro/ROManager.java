package es.oeg.ro;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.DoubleStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.vocabulary.DCTerms;

import es.oeg.ro.dao.DAOAuthorsNeo4j;
import es.oeg.ro.dao.DAOAuthorsNeo4jImp;
import es.oeg.ro.dao.DAOauthors;
import es.oeg.ro.dao.DAOpapers;
import es.oeg.ro.transfer.ADSLabsResultsBean;
import es.oeg.ro.transfer.Author;
import es.oeg.ro.transfer.AuthorBSON;
import es.oeg.ro.transfer.Authors;
import es.oeg.ro.transfer.Paper;

public class ROManager {


	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Authors listAuthors = new Authors();	
	private DAOpapers daopapers = new DAOpapers();
	private DAOauthors daoauthors = new DAOauthors();

	private DAOAuthorsNeo4j socialGraph;
	
	public ROManager(){
		socialGraph =  DAOAuthorsNeo4j.getInstance();
//		FIXME que hacer para crear los indices
//		((DAOAuthorsNeo4jImp)socialGraph).init();	
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
	
	public void saveToDatabase(ADSLabsResultsBean result) {
		if (result != null){
			for(Paper p: result.getResults().get_docs()){
				logger.debug("Paper added: "+p.toString());
				//TODO comentado para no volver a añadir más cosas a la base de datos de mongo
//				daopapers.add(p);				
//				logger.debug("Authors information added");
//				daoauthors.add(p.get_author());
				//TODO añadir al grafo
				if (p.get_author() != null)
					((DAOAuthorsNeo4jImp) socialGraph).addAuthors(p.get_author());
				else{
					logger.info("This paper has not authors");
				}
			}
		}


	}

	public AuthorBSON getAuthor(String s) {
		if (s == null || s.isEmpty())
			return null;
		return daoauthors.getByName(s);
		
	}

	// buscar usuarios y calcular la similitud
	// similitud a partir del índice de jaccard
	public double computeSocialSimilarity(List<String> get_author, List<String> get_author2, int depth) {
		if (get_author == null || get_author2== null || get_author.size()==0 || get_author2.size() == 0){
			logger.error("The authors are null or empty");
			return -1;
		}

		double[] similarities = new double[get_author.size()*get_author2.size()];
		DAOAuthorsNeo4jImp daoNeo4j = ((DAOAuthorsNeo4jImp)DAOAuthorsNeo4j.getInstance());
		
		int i=0;
		double numberTotalSharedPublicationsAuthor1,sharedPublications,numberTotalSharedPublicationsAuthor2,e;
		
		for (String author1: get_author){
			// calcular la similitud con el resto
			numberTotalSharedPublicationsAuthor1 = daoNeo4j.numberOfTotalSharedPublications(author1);
			
			for (String author2: get_author2){
				
				Author auth1 = daoNeo4j.findAuthor(author1);
				Author auth2 = daoNeo4j.findAuthor(author2);
				if (auth1 == null || auth2 == null){
					logger.info(author1+" OR "+author2+" not found in DB");
					return -1;
				}
				else if (auth1.getId().equals(auth2.getId())){
					logger.debug("Similarity between: "+author1+" and "+author2+" is : "+1.0);
					similarities[i] = 1.0;
				}
				else{
					sharedPublications = daoNeo4j.sharedPublications(author1, author2,depth);				
					numberTotalSharedPublicationsAuthor2 = daoNeo4j.numberOfTotalSharedPublications(author2);
					
					// para no contar dos veces
					numberTotalSharedPublicationsAuthor1 -= sharedPublications;
					numberTotalSharedPublicationsAuthor2 -= sharedPublications;				
					
					e = (sharedPublications/(numberTotalSharedPublicationsAuthor1+numberTotalSharedPublicationsAuthor2-sharedPublications));
					logger.debug("Similarity between: "+author1+" and "+author2+" is : "+e);
					
					//				similarities = daoNeo4j.similarity(author1,author2);
					similarities[i] = e;
					numberTotalSharedPublicationsAuthor1 += sharedPublications;
				}
				i++;				
			}
		}
		double sum = DoubleStream.of(similarities).sum();
		double finalSimilarity = sum / similarities.length;

		logger.debug("Similarity between: "+get_author.toString()+" and "+get_author2.toString()+" is: "+finalSimilarity);
		return finalSimilarity;
	}
}
