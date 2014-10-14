package similarity;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class ExtensionalSimilarityTest {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	private ExtensionalSimilarity similarity = new ExtensionalSimilarity();

	// ----- OBJECT SIMILARITY



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
		logger.info("Similitud de objetos: "+sim);
		Assert.assertTrue(sim > 0);

	}

	@Test (expected = NullPointerException.class)
	public void onlyOneParameterObjects(){
		Model modelData2 = RDFDataMgr.loadModel("src/test/resources/root.ttl",Lang.TURTLE) ;
		StmtIterator iter2 = modelData2.listStatements();
		similarity.objectSimilarity(null, iter2);
	}
	
	// -----  SUBJECT SIMILARITY 

	@Test(expected = NullPointerException.class)
	public void nullParameters(){		
		similarity.subjectSimilarity(null, null);
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
		logger.info("Similitud de sujetos: "+sim);
		Assert.assertTrue(sim > 0);
		
	}
	
	@Test (expected = NullPointerException.class)
	public void onlyOneParameter(){
		Model modelData2 = RDFDataMgr.loadModel("src/test/resources/root.ttl",Lang.TURTLE) ;
		StmtIterator iter2 = modelData2.listStatements();
		similarity.subjectSimilarity(null, iter2);
	}	
}
