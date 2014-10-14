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
	
	// TODO compare the number of aggregations (ro:aggregatedAnnotation)
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
	public Set<Object> sharedAggregatedResources(Model model1, Model model2){
		if (model1 == null || model2 == null){
			logger.error("Parameter cannot be null");
			throw new NullPointerException("Parameter cannot be null");	
		}
		if (hasAgreggatedResources(model1) & hasAgreggatedResources(model2)){
			Property p = ResourceFactory.createProperty(ore_aggregates);
			// FIXME �se puede mejorar con model.difference() ?? -> de momento funciona
			// model.containAny() -> no funciona porque compara statements
			Set<Object> set = SetUtils.intersection(model1.listObjectsOfProperty(p).toSet(), model2.listObjectsOfProperty(p).toSet());
			logger.debug("Conjunto intersecci�n: "+set.toString());
			return set;
		}
		logger.debug("There aren't ore:aggregates in both model");
		return null;
	}
	
	
	// FIXME : separar claramente la parte estructural de la parte extensional
	public double computeStructuralSimilarity(Model model, Model model2){
//		StmtIterator iter = model.listStatements();
//		StmtIterator iter2 = model2.listStatements();
//		double subjects = subjectSimilarity(iter, iter2);
//		logger.debug("Subject similarity: "+subjects);
//		iter = model.listStatements();
//		iter2 = model2.listStatements();
//		double objects = objectSimilarity(iter, iter2);
//		logger.debug("Object similarity: "+objects);
		
		List<Statement> list= stmt2List(model.listStatements());
		List<Statement> list2 = stmt2List(model2.listStatements());		
		double statements = statementSimilarity(list, list2);		
		logger.debug("Statement similarity: "+statements);
		
		int model1SizeProperty = numberOfAggregatedResources(model);
		int model2SizeProperty = numberOfAggregatedResources(model2);
		// FIXME : cambiar la m�trica de similitud para el mismo conjunto si sumamos el tama�o la similitud siempre es 2
		// @see http://en.wikipedia.org/wiki/Jaccard_index
		// la similitud m�xima con jaccard index es 0.5
		double union, intersection;
		union = (model1SizeProperty+model2SizeProperty);
		intersection = sharedAggregatedResources(model, model2).size();
		double aggregatedSimilarity =  (intersection / union) ;
		double alpha = 0.50;
		
		return 0.50*statements + (1-alpha)*aggregatedSimilarity;
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

}
