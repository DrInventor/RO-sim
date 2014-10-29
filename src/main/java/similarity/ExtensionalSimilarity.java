package similarity;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * The metrics implemented in this class focus on comparing the sets of individuals. 
 * The intuitive principle in instance-based technique is 
 * that when a pair of concepts is associated with 
 * the same set of objects, they are likely to be similar.
 * 
 * We compute the similarity metric in objetc and subjects following in both metrics the jaccard index approach
 */
public class ExtensionalSimilarity {

	Logger logger = LoggerFactory.getLogger(this.getClass());


	// FIXME : refactorizar jaccard index y sacar método común
	public double objectSimilarity(Model model1, Model model2) throws NullPointerException{
		if (model1 == null || model2 == null){
			logger.error("The models cannot be null");
			throw new NullPointerException("The models cannot be null");
		}

		if (model1 == null || model2 == null){
			return 0;
		}

		// jaccard index -> http://en.wikipedia.org/wiki/Jaccard_index
		List<RDFNode> list = model1.listObjects().toList();
		List<RDFNode> list2 = model2.listObjects().toList();


		if (list.size() == 0 & list2.size() == 0)
			// definition of Jaccard index
			return 1;

		double intersection;
		// union = |a| + |b| - |comunes|
		double union = list.size() + list2.size();
		list.retainAll(list2);
		intersection = list.size();
		// @see http://en.wikipedia.org/wiki/Jaccard_index
		double jaccard = intersection / (union-intersection) ;
		return jaccard;	
	}
	
	
	public double subjectSimilarity(Model model1, Model model2) throws NullPointerException{
		if (model1 == null || model2 == null){
			logger.error("The models cannot be null");
			throw new NullPointerException("The models cannot be null");
		}
		
		// jaccard index -> http://en.wikipedia.org/wiki/Jaccard_index
		List<Resource> list = model1.listSubjects().toList();
		List<Resource> list2 = model2.listSubjects().toList();
			
		if (list.size() == 0 & list2.size() == 0)
			// definition of Jaccard index
			return 1;
		
		double intersection;
		// union = |a| + |b| - |comunes|
		double union = list.size() + list2.size();
		list.retainAll(list2);
		intersection = list.size();
		// @see http://en.wikipedia.org/wiki/Jaccard_index
		double jaccard = intersection / (union-intersection) ;
		return jaccard;	
		
	}

}
