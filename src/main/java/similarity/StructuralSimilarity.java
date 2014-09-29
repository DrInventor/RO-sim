package similarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections4.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class StructuralSimilarity {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// vector de pesos para ponderar cada elemento
	private Vector<Double> weights;

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
			// TODO mejorar esta m�trica
			// uni�n de los conjuntos / intersecci�n de los conjuntos
			// normalized
			double sim = ((set1.size()+set2.size())/inter.size())*0.1;
			return sim;
		}
		else return 0;
				
	}
	
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
			//		uni�n de los conjuntos / intersecci�n de los conjuntos
			//TODO mejorar esta m�trica
			double sim = ((set1.size()+set2.size())/inter.size())*0.1;
			return sim;		
		}
		else return 0;
	}
	// statements compartidos por dos modelos
	public Set<Statement> commonStatements(StmtIterator modelIterator, StmtIterator modelIterator2){
		//FIXME ��por qu� no lo pasa a list??
		List<Statement> list= new ArrayList<Statement>();
		while (modelIterator.hasNext())
			list.add(modelIterator.next());
		
						
		List<Statement> list2 = new ArrayList<Statement>();
		while (modelIterator2.hasNext())
			list2.add(modelIterator2.next());
		
		Set<Statement> set = new HashSet<Statement>();
		
		for (Statement st: list){
			for (Statement st2: list2){
				if (st2.equals(st)){
					set.add(st);
				}
			}
		}
		return set;
	}
	
	// statement similarity
	public double statementSimilarity(StmtIterator modelIterator, StmtIterator modelIterator2){
		//FIXME ��c�mo podemos saber el n�mero de statements??
		int size = 0 ;//modelIterator.toList().size();
		
		int size2 =0 ;//modelIterator2.toList().size();
		
		Set<Statement> set = commonStatements(modelIterator, modelIterator2);
		if (set.size() > 0){
			double sim = ((size+size2)/set.size())*0.1;
			return sim;
		}
		return 0;
	}
	
	// subset of statements
	public void isInModel(){}
	
	
	// common predicates
	public void predicateIntersection(StmtIterator modelIterator, StmtIterator modelIterator2){
//		los predicados (propiedades) no son interesantes --> deber�an compartirlos casi todos
		Set<Resource> setPredicates1 = getAllPredicates(modelIterator);
		Set<Resource> setPredicates2 = getAllPredicates(modelIterator2);

		Set commonPredicates = intersection(setPredicates1, setPredicates2);		
		
	}
	
	
	// common prefix
	public void prefixIntersection(){
		
	}
	

	public double computeStructuralSimilarity(Model model, Model model2){
		StmtIterator iter = model.listStatements();
		StmtIterator iter2 = model2.listStatements();
		double subjects = subjectSimilarity(iter, iter2);
		iter = model.listStatements();
		iter2 = model2.listStatements();
		double objetcts = objectSimilarity(iter, iter2);
		iter = model.listStatements();
		iter2 = model2.listStatements();
		//FIXME  ��C�mo pasar el tama�o de la lista??
		double statements = statementSimilarity(iter, iter2);		
		return 0.50*statements + 0.30*subjects + 0.20*objetcts;
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