package similarity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.SetUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class StructuralSimilarity {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// vector de pesos para ponderar cada elemento
	private Vector<Double> weights;

	private static final String ore_aggregates = "http://www.openarchives.org/ore/terms/aggregates";

	// statements compartidos por dos modelos
	@Deprecated
	public Set<Statement> sharedStatements(List<Statement> modelIterator, List<Statement> modelIterator2){
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
	@Deprecated // inefficient way
	public double statementSimilarity(List<Statement> modelIterator, List<Statement> modelIterator2){
		double size = modelIterator.size() ;		
		double size2 = modelIterator2.size() ;		
		Set<Statement> set = sharedStatements(modelIterator, modelIterator2);
		if (set.size() > 0){
			double sim = (set.size()/(size+size2));
			return sim;
		}
		return 0;
	}
	
	public double statementSimilarity(Model model1, Model model2){
		if (model1 == null || model2 == null){
			return 0;
		}
		if (model1.size() == 0 & model2.size() == 0)
			// definition of Jaccard index
			return 1;
		double intersection = model1.intersection(model2).size();
		// union = |a| + |b| - |comunes|
		double union = model1.difference(model2).size() + model2.difference(model1).size() + model1.intersection(model2).size();
		// @see http://en.wikipedia.org/wiki/Jaccard_index
		double jaccard = intersection / union ;
		return jaccard;		
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
		return model.listStatements(st.getSubject(), st.getPredicate(), st.getObject()).hasNext();
	}
	
	
	// TODO common predicates --> hacer si se considera interesante
	public void predicateIntersection(StmtIterator modelIterator, StmtIterator modelIterator2){
		
	}
	
	public boolean hasAgreggatedResources(Model model){
		if (model == null){
			logger.error("Parameter cannot be null");
			throw  new NullPointerException("Parameter cannot be null");
		}
		Property p = ResourceFactory.createProperty(ore_aggregates);
		return model.contains(null, p);		
	}
	
	public int numberOfAggregatedResources(Model model){
		if (model == null){
			logger.error("Parameter cannot be null");
			throw new NullPointerException("Parameter cannot be null");
		}
		if (hasAgreggatedResources(model)){
			logger.debug("the model has: "+ore_aggregates);
			Property p = ResourceFactory.createProperty(ore_aggregates);
			List<RDFNode> list =	model.listObjectsOfProperty(p).toList();
			logger.debug("list of. "+list.toString());
			logger.debug("size: "+list.size());
			return list.size();
		}
		logger.debug("The model hasn't :"+ ore_aggregates);
		return -1;
	}
	
//	number of common aggregated resources
	/**
	 * 
	 * @param model1
	 * @param model2
	 * @return a set with the shared objects that are shared by the two models
	 */
	public Set<Object> sharedAggregatedResources(Model model1, Model model2){
		if (model1 == null || model2 == null){
			logger.error("Parameter cannot be null");
			throw new NullPointerException("Parameter cannot be null");	
		}
		if (hasAgreggatedResources(model1) & hasAgreggatedResources(model2)){
			Property p = ResourceFactory.createProperty(ore_aggregates);
			// FIXME ¿se puede mejorar con model.difference() ?? -> de momento funciona
			// model.containAny() -> no funciona porque compara statements
			Set<Object> set = SetUtils.intersection(model1.listObjectsOfProperty(p).toSet(), model2.listObjectsOfProperty(p).toSet());
			logger.debug("Conjunto intersección: "+set.toString());
			return set;
		}
		logger.debug("There aren't ore:aggregates in both model");
		return null;
	}
	
	public double computeStructuralSimilarity(Model model, Model model2) throws NullPointerException{
		if (model == null || model2 == null){
			logger.error("Parameters cannot be null");
			throw new NullPointerException("The models cannot be null");
		}
		double statements = statementSimilarity(model, model2);		
		logger.debug("Statement similarity: "+statements);
		
		double aggregatedSimilarity = aggregatedResourcesObjectsSimilarity(
				model, model2);
		logger.debug("Agreggated resources objects similarity: "+aggregatedSimilarity);
		// weighted metric for compute similarity 
		double alpha = 0.50;		
		return 0.50*statements + (1-alpha)*aggregatedSimilarity;
	}

	// TODO hacer test de este método
	public double aggregatedResourcesObjectsSimilarity(Model model, Model model2) {
		if (model == null || model2 == null){
			logger.error("Parameters cannot be null");
			return 0;
		}
		// @see http://en.wikipedia.org/wiki/Jaccard_index
		double union, intersection;
		union = unionOfAgreggatedResources(model,model2);
		intersection = sharedAggregatedResources(model, model2).size();
		double aggregatedSimilarity =  (intersection / union) ;
		return aggregatedSimilarity;
	}
	
	// queremos la union de los ore:aggregates 
	public double unionOfAgreggatedResources(Model model, Model model2) throws NullPointerException{
		if (model == null || model2 == null){
			logger.error("Parameter cannot be null");
			throw new NullPointerException("Parameter cannot be null");
		}
		if (hasAgreggatedResources(model) & hasAgreggatedResources(model2)){
			logger.debug("the model has: "+ore_aggregates);
			Property p = ResourceFactory.createProperty(ore_aggregates);
			List<RDFNode> list =	model.listObjectsOfProperty(p).toList();
			List<RDFNode> list2 =	model2.listObjectsOfProperty(p).toList();			
			logger.debug("list of. "+list.toString());
			logger.debug("list of. "+list2.toString());
			logger.debug("size: "+list.size());
			logger.debug("size: "+list2.size());
			// union A u B = |A| + |B| - |A ^ B|
			double union = list.size() + list2.size();
			list.retainAll(list2);			
			return (union - list.size());
		}
		logger.debug("The model hasn't :"+ ore_aggregates);
		return 0;		
	}

	protected List<Statement> stmt2List(StmtIterator listStatements) {
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
	
	
}
