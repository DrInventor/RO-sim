package similarity;

import java.util.Set;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Assert;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class StructuralSimilarityTest {

	private StructuralSimilarity similarity = new StructuralSimilarity();
	
	// -----  SUBJECT SIMILARITY 

	@Test(expected = NullPointerException.class)
	public void nullParameters(){		
		similarity.objectSimilarity(null, null);
	}

	@Test
	public void notIntersection(){
		// Create a model and read into it from file 
		// "data.ttl" assumed to be Turtle.
		Model model = RDFDataMgr.loadModel("src/test/resources/root.ttl",Lang.TURTLE) ;
		StmtIterator iter = model.listStatements();
		
		Model modelData2 = RDFDataMgr.loadModel("src/test/resources/ro-sample.ttl",Lang.TURTLE) ;
		StmtIterator iter2 = modelData2.listStatements();
		
		double sim = similarity.subjectSimilarity(iter, iter2);
		Assert.assertTrue(sim == 0);		
	}
	
	@Test
	public void commonSubjectsCorrect(){
		// Create a model and read into it from file 
		// "data.ttl" assumed to be Turtle.
		Model model = RDFDataMgr.loadModel("src/test/resources/root.ttl",Lang.TURTLE) ;
		StmtIterator iter = model.listStatements();
		
		Model modelData2 = RDFDataMgr.loadModel("src/test/resources/root.ttl",Lang.TURTLE) ;
		StmtIterator iter2 = modelData2.listStatements();
		
		double sim = similarity.subjectSimilarity(iter, iter2);
		System.out.println("Similitud de sujetos: "+sim);
		Assert.assertTrue(sim > 0);
		
	}
	
	@Test (expected = NullPointerException.class)
	public void onlyOneParameter(){
		Model modelData2 = RDFDataMgr.loadModel("src/test/resources/root.ttl",Lang.TURTLE) ;
		StmtIterator iter2 = modelData2.listStatements();
		similarity.subjectSimilarity(null, iter2);
	}	
	
	// ----- OBJECT SIMILARITY

	@Test(expected = NullPointerException.class)
	public void nullObjectParameters(){		
		similarity.subjectSimilarity(null, null);
	}

	@Test
	public void notIntersectionObject(){
		// Create a model and read into it from file 
		// "data.ttl" assumed to be Turtle.
		Model model = RDFDataMgr.loadModel("src/test/resources/data.ttl",Lang.TURTLE) ;
		StmtIterator iter = model.listStatements();
		
		Model modelData2 = RDFDataMgr.loadModel("src/test/resources/ro-sample.ttl",Lang.TURTLE) ;
		StmtIterator iter2 = modelData2.listStatements();
		
		double sim = similarity.objectSimilarity(iter, iter2);
		Assert.assertTrue(sim == 0);		
	}
	
	@Test
	public void commonObjectsCorrect(){
		// Create a model and read into it from file 
		// "data.ttl" assumed to be Turtle.
		Model model = RDFDataMgr.loadModel("src/test/resources/root.ttl",Lang.TURTLE) ;
		StmtIterator iter = model.listStatements();
		
		Model modelData2 = RDFDataMgr.loadModel("src/test/resources/root.ttl",Lang.TURTLE) ;
		StmtIterator iter2 = modelData2.listStatements();
		
		double sim = similarity.objectSimilarity(iter, iter2);
		System.out.println("Similitud de objetos: "+sim);
		Assert.assertTrue(sim > 0);
		
	}
	
	@Test (expected = NullPointerException.class)
	public void onlyOneParameterObjects(){
		Model modelData2 = RDFDataMgr.loadModel("src/test/resources/root.ttl",Lang.TURTLE) ;
		StmtIterator iter2 = modelData2.listStatements();
		similarity.subjectSimilarity(null, iter2);
	}
	
	// --- PREDICATE SIMILARITY
	
	// -- STATEMENTS COMPARTIDOS
	
	@Test
	public void commonStatements(){
		// Create a model and read into it from file 
		// "data.ttl" assumed to be Turtle.
		Model model = RDFDataMgr.loadModel("src/test/resources/data.ttl",Lang.TURTLE) ;
		StmtIterator iter = model.listStatements();
		
		Model modelData2 = RDFDataMgr.loadModel("src/test/resources/data2.ttl",Lang.TURTLE) ;
		StmtIterator iter2 = modelData2.listStatements();
		
		Set set = similarity.commonStatements(iter, iter2);
		System.out.println("Set: "+set.toString());
		
		
	}
	
	// -- COMPLETE TEST SIMILARITY
	@Test
	public void computeSimilarityTotal(){
		// Create a model and read into it from file 
		// "data.ttl" assumed to be Turtle.
		Model model = RDFDataMgr.loadModel("src/test/resources/data.ttl",Lang.TURTLE) ;
		Model modelData2 = RDFDataMgr.loadModel("src/test/resources/data2.ttl",Lang.TURTLE) ;
		
		double sim = similarity.computeStructuralSimilarity(model, modelData2);
		System.out.println("Sim total: "+sim);
		
		
	}
	
	
}
