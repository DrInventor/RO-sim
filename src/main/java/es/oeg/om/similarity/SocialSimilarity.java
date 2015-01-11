package es.oeg.om.similarity;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.oeg.ro.ROManager;
import es.oeg.ro.transfer.AuthorBSON;
import es.oeg.ro.transfer.Paper;

public class SocialSimilarity {

	Logger logger = LoggerFactory.getLogger(this.getClass());	

	private ROManager manager = new ROManager();
	
	public double socialSimilarity(Paper p1, Paper p2){
		// recuperar cada uno de los autores
		List<String> authorsP1 = p1.get_author();
		for (String s: authorsP1){
			AuthorBSON author = manager.getAuthor(s);
			logger.info("Author :"+author.toString());
		}
		
		List<String> authorsP2 = p2.get_author();
		for (String s: authorsP2){
			AuthorBSON author = manager.getAuthor(s);
			if (author != null)
				logger.info("Author :"+author.toString());
			else
				logger.info("Author: "+s+" not found");
		}
		//FIXME hay que tener cuidado con papers sin autores -> no habría que añadirlos
		
		return -1;
	}
}
