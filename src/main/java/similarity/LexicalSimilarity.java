package similarity;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;

public class LexicalSimilarity {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	private final Property dCTermsProperty = DCTerms.creator;
	
	// retrieve all the statements with dc:creator property
	public List<RDFNode> getDCCreators(Model m){
		if (m == null) throw new NullPointerException("Parameter cannot be null");
		
		ResIterator resources = m.listResourcesWithProperty(dCTermsProperty);
		if (!resources.hasNext()){
			logger.info("Not found property "+dCTermsProperty.toString());
			return null;
		}
		List<RDFNode> list = new ArrayList<RDFNode>();
		while(resources.hasNext()){
			Resource r = resources.next();			
			logger.info(r.getURI());
			// now we get the object
			RDFNode object = r.getProperty(dCTermsProperty).getObject();
			logger.info(object.toString());
			list.add(object);
		}
		return list;
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
	
	
	// dc:creator ; dc:author ;
	public void authorSimilarity(){
		
	}
	
	

}
