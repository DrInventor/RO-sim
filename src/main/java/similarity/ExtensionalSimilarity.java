package similarity;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/*
 * comparing the sets of individuals. 
 * The intuitive principle in instance-based technique is 
 * that when a pair of concepts is associated with 
 * the same set of objects, they are likely to be similar.
 */
public class ExtensionalSimilarity {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	// common objects
	public double objectSimilarity(StmtIterator modelIterator, StmtIterator modelIterator2){

		if (modelIterator == null || modelIterator2== null){
			logger.error("Parameters cannot be null ");
			throw new NullPointerException();
		}

		Set<RDFNode> set1 = getAllObjects(modelIterator);		
		Set<RDFNode> set2 = getAllObjects(modelIterator2);

		// cast to rdfnode
		Set<Object> inter = intersection(set1, set2);
		if (inter.size() > 0){
			//		unión de los conjuntos / intersección de los conjuntos
			//TODO ¿cómo conseguir mejores valores?
			double sim = ((set1.size()+set2.size())/inter.size())*0.1;
			return sim;		
		}
		else return 0;
	}

	// common subjects
		public double subjectSimilarity(StmtIterator modelIterator, StmtIterator modelIterator2){
			
			if (modelIterator == null || modelIterator2== null){
				logger.error("Parameters cannot be null ");
				throw new NullPointerException();
			}
			Set<Resource> set1 = getAllSubjects(modelIterator);		
			Set<Resource> set2 = getAllSubjects(modelIterator2);
			
			// cast to resource
			Set<Object> inter = intersection(set1, set2);
			if (inter.size() > 0){
				// TODO ¿cómo conseguir mejores valores?
				// unión de los conjuntos / intersección de los conjuntos
				// normalized
				double sim = ((set1.size()+set2.size())/inter.size())*0.1;
				return sim;
			}
			else return 0;
					
		}
		
	private Set<RDFNode> getAllObjects(StmtIterator modelIterator){
		Set<RDFNode> set = new HashSet<RDFNode>();
		while (modelIterator.hasNext()){
			Statement stmt = modelIterator.next();
			set.add(stmt.getObject());
		}
		return set;
	}
	
	@SuppressWarnings("unchecked")
	private Set<Object> intersection(Set set1, Set set2){
		Set<Object> a;
		Set<Object> b,common = new HashSet<Object>();
		
		if (set1.size() <= set2.size()) {
            a = set1;
            b = set2;           
        } else {
            a = set2;
            b = set1;
        }
        int count = 0;
        for (Object e : a) {
            if (b.contains(e)) {
            	common.add(e);
                count++;
            }           
        }
        logger.debug("Total number of common: "+ count);
        return common;
	}
	
	private Set<Resource> getAllSubjects(StmtIterator modelIterator){
		// TODO : refactorizar para model.listStatements(null,null, null);
		Set<Resource> set = new HashSet<Resource>();
		while (modelIterator.hasNext()){
			Statement stmt = modelIterator.next();
			set.add(stmt.getSubject());
		}
		return set;
	}

}
