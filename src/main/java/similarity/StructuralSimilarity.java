package similarity;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class StructuralSimilarity {
	
	// vector de pesos para ponderar cada elemento
	private Vector<Double> weights;

	// common subjects
	public double subjectIntersection(StmtIterator modelIterator, StmtIterator modelIterator2){
		Set<Resource> set1 = getAllSubjects(modelIterator);		
		Set<Resource> set2 = getAllSubjects(modelIterator2);
		
		// cast to resource
		Set<Object> inter = intersection(set1, set2);
		// unión de los conjuntos / intersección de los conjuntos
		double sim = (set1.size()+set2.size())/inter.size();
		return sim;		
	}
	
	// common objects
	public double objectIntersection(StmtIterator modelIterator, StmtIterator modelIterator2){
		
		Set<RDFNode> set1 = getAllObjects(modelIterator);		
		Set<RDFNode> set2 = getAllObjects(modelIterator2);
		
		// cast to rdfnode
		Set<Object> inter = intersection(set1, set2);
//		unión de los conjuntos / intersección de los conjuntos
		double sim = (set1.size()+set2.size())/inter.size();
		return sim;		
	}
	
	// subset of statements
	public void isInModel(){}
	
	
	// common predicates
	public void predicateIntersection(StmtIterator modelIterator, StmtIterator modelIterator2){
//		los predicados (propiedades) no son interesantes --> deberían compartirlos casi todos
		Set<Resource> setPredicates1 = getAllPredicates(modelIterator);
		Set<Resource> setPredicates2 = getAllPredicates(modelIterator2);

		Set commonPredicates = intersection(setPredicates1, setPredicates2);		
		
	}
	
	
	// common prefix
	public void prefixIntersection(){
		
	}
	
	public double computeSimilarity(){
		return 0;
	}
	
	public Vector<Double> getWeights() {
		return weights;
	}
	public void setWeights(Vector<Double> weights) {
		this.weights = weights;
	}
	
	public void addWeight(Double weight){
		this.weights.add(weight);
	}
	
	private Set<Resource> getAllPredicates(StmtIterator modelIterator){
		Set<Resource> set = new HashSet<Resource>();
		while (modelIterator.hasNext()){
			Statement stmt = modelIterator.next();		
			set.add(stmt.getPredicate());
		}		
		return set;
	}
	
	private Set<Resource> getAllSubjects(StmtIterator modelIterator){
		Set<Resource> set = new HashSet<Resource>();
		while (modelIterator.hasNext()){
			Statement stmt = modelIterator.next();
			set.add(stmt.getSubject());
		}
		return set;
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
        System.out.println("Total number of common: "+ count);
        return common;
	}
}
