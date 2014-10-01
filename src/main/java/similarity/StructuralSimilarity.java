package similarity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

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
			// TODO mejorar esta métrica
			// unión de los conjuntos / intersección de los conjuntos
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
			//		unión de los conjuntos / intersección de los conjuntos
			//TODO mejorar esta métrica
			double sim = ((set1.size()+set2.size())/inter.size())*0.1;
			return sim;		
		}
		else return 0;
	}
	// statements compartidos por dos modelos
	public Set<Statement> commonStatements(List<Statement> modelIterator, List<Statement> modelIterator2){
		List<Statement> list= new ArrayList<Statement>();
		for (Statement st: modelIterator )
			list.add(st);
		
						
		List<Statement> list2 = new ArrayList<Statement>();
		for (Statement st: modelIterator2 )
			list2.add(st);
		
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
	public double statementSimilarity(List<Statement> modelIterator, List<Statement> modelIterator2){
		int size = modelIterator.size() ;		
		int size2 = modelIterator2.size() ;		
		Set<Statement> set = commonStatements(modelIterator, modelIterator2);
		if (set.size() > 0){
			double sim = ((size+size2)/set.size())*0.1;
			return sim;
		}
		return 0;
	}
	
	// subset of statements
	/**
	 * 
	 * @param model
	 * @param st
	 * @return true if the st is in the @model false in other case
	 * @throws NullPointerException if the param is null
	 */
	public boolean isInModel(Model model, Statement st){
		if (st == null || model == null) 
			throw new NullPointerException("Paramenter cannot be null");
		//FIXME mirar por qué no fuciona model.listStatements(st.getSubject(), st.getPredicate(), st.getObject())
		StmtIterator iterator = model.listStatements();
		while(iterator.hasNext()){
			Statement stIter = iterator.next();
			logger.debug("Statement recuperado "+stIter.getSubject()+" "+stIter.getPredicate()+" "+stIter.getObject());
			if (st.getSubject().equals(stIter.getSubject()) && 
					st.getPredicate().equals(stIter.getPredicate()) && st.getObject().equals(stIter.getObject()))
				return true;
		}
		return false;
	}
	
	
	// TODO common predicates --> hacer si se considera interesante
	public void predicateIntersection(StmtIterator modelIterator, StmtIterator modelIterator2){
		
	}
	
	
	// common prefix
	public void prefixIntersection(){
		
	}
	

	public double computeStructuralSimilarity(Model model, Model model2){
		StmtIterator iter = model.listStatements();
		StmtIterator iter2 = model2.listStatements();
		double subjects = subjectSimilarity(iter, iter2);
		logger.debug("Subject similarity: "+subjects);
		iter = model.listStatements();
		iter2 = model2.listStatements();
		double objects = objectSimilarity(iter, iter2);
		logger.debug("Object similarity: "+objects);
		List<Statement> list= stmt2List(model.listStatements());
		List<Statement> list2 = stmt2List(model2.listStatements());
		
		double statements = statementSimilarity(list, list2);		
		logger.debug("Statement similarity: "+statements);
		return 0.50*statements + 0.30*subjects + 0.20*objects;
	}
	
	protected List<Statement> stmt2List(StmtIterator listStatements) {
		// 
		List<Statement> list = new ArrayList<Statement>();
		while (listStatements.hasNext()){
			list.add(listStatements.next());
		}
		return list;
		
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
        logger.debug("Total number of common: "+ count);
        return common;
	}
}
