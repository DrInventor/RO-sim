package es.oeg.om.similarity;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * This class computes the similarity employing different characteristic of the RO model. 
 * Currently, we employ the dc:creator information to produce relations among other authors.
 * 
 */
public class NameBasedSimilarity {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	private final Property dCTermsProperty = DCTerms.creator;
	private HashMap<String, HashMap<String,Integer>> mapAuthors = new HashMap<String,HashMap<String,Integer>>();
	
	// retrieve all the statements with dc:creator property
	public List<RDFNode> getDCCreators(Model m) throws NullPointerException{
		if (m == null) throw new NullPointerException("Parameter cannot be null");		
		return m.listObjectsOfProperty(dCTermsProperty).toList();		
	}
	
	// compare two sets of dc:creator
	public boolean sharesCreators(Model m1, Model m2){
		List<RDFNode> list1 = getDCCreators(m1);
		List<RDFNode> list2 = getDCCreators(m2);
		if (list1 == null || list2 == null){
			logger.info("One of the models doesn't contain the property "+dCTermsProperty.toString());
			return false;
		}
		list1.retainAll(list2);
		return list1.size() > 0;		
	}
	
	//update the information about the author and colleagues
	public void updateAuthorMatrix(Model m1){
		List<RDFNode> list1 = getDCCreators(m1);
		for (RDFNode node: list1){
			for (RDFNode node2: list1)
				if (node.isURIResource() && node2.isURIResource()){
					// añadirlo
				}
				else{
					logger.warn("The author isn't an URI");
					HashMap<String,Integer> RowVector;  
			        int count;  
			        String auth1,auth2;
			        auth1 = node.asResource().getURI();
			        auth2 = node2.asResource().getURI();
			        
			        if(mapAuthors.containsKey(auth1)) {  
			            RowVector = mapAuthors.get(auth1);  
			            if(RowVector.containsKey(auth2)) {  
			               count = RowVector.get(auth2);  
			               RowVector.put(auth2, ++count);  
			            } else {  
			                RowVector.put(auth2, 1);  
			            }  
			        } else {  
			            RowVector = new HashMap<String, Integer>();  
			            RowVector.put(auth2, 1);              
			        } 			          
			        mapAuthors.put(auth1,RowVector);
				}
		}
	}
	
	
	// dc:creator ; dc:author ;
	public double authorSimilarity(Model m1, Model m2){
		if (m1 == null || m2 == null){
			return 0;
		}
		if (!sharesCreators(m1,m2))
			return 0;
		
		List<RDFNode> list1 = getDCCreators(m1);
		List<RDFNode> list2 = getDCCreators(m2);
		logger.info("creators of model1: "+list1.toString());
		logger.info("creators of model2: "+list2.toString());
		if (list1.size() == 0 & list2.size() == 0)
			// definition of Jaccard index
			return 1;
		double intersection;
		// union = |a| + |b| - |comunes|
		double union = list1.size() + list2.size();
		list1.retainAll(list2);
		intersection = list1.size();
		// @see http://en.wikipedia.org/wiki/Jaccard_index
		double jaccard = intersection / (union-intersection) ;
		logger.info("Jaccard index: similarity value: "+jaccard);
		return jaccard;		
	}
	
	

}
