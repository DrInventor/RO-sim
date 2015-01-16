package es.oeg.om.similarity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.oeg.ro.ROManager;
import es.oeg.ro.transfer.Paper;

public class SocialSimilarity {

	Logger logger = LoggerFactory.getLogger(this.getClass());	

	private ROManager manager = new ROManager();
	
	public void socialSimilarity(Paper p1, Paper p2){
		manager.computeSocialSimilarity(p1.get_author(),p2.get_author(),1);		
		manager.computeSocialSimilarity(p1.get_author(),p2.get_author(),2);		
	}	
	
}
