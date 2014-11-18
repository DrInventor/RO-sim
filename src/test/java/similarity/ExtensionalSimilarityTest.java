package similarity;

import static org.junit.Assert.assertTrue;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;

import es.oeg.om.similarity.ExtensionalSimilarity;

public class ExtensionalSimilarityTest {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	private ExtensionalSimilarity similarity = new ExtensionalSimilarity();

	// ----- OBJECT SIMILARITY

	@Test
	public void notIntersectionObject(){
		Model model = RDFDataMgr.loadModel("src/test/resources/data.ttl",Lang.TURTLE) ;
		Model modelData2 = RDFDataMgr.loadModel("src/test/resources/ro-sample.ttl",Lang.TURTLE) ;
		double sim = similarity.objectSimilarity(model, modelData2);
		Assert.assertTrue(sim == 0);		
	}

	@Test
	public void commonObjectsCorrect(){
		Model model = RDFDataMgr.loadModel("src/test/resources/root.ttl",Lang.TURTLE) ;
		Model modelData2 = RDFDataMgr.loadModel("src/test/resources/root.ttl",Lang.TURTLE) ;
		double sim = similarity.objectSimilarity(model, modelData2);
		logger.info("Similitud de objetos: "+sim);
		Assert.assertTrue(sim > 0);
		assertTrue(sim == 1);

	}

	@Test (expected = NullPointerException.class)
	public void onlyOneParameterObjects(){
		Model modelData2 = RDFDataMgr.loadModel("src/test/resources/root.ttl",Lang.TURTLE) ;
		similarity.objectSimilarity(null, modelData2);
	}

	@Test
	public void onlyOneParameterObjectsComparation(){
		Model modelData2 = RDFDataMgr.loadModel("src/test/resources/root.ttl",Lang.TURTLE) ;
		assertTrue(similarity.objectSimilarity(modelData2, modelData2) == 1);
	}
	
	
	
	// -----  SUBJECT SIMILARITY 

	@Test(expected = NullPointerException.class)
	public void nullParameters(){		
		similarity.subjectSimilarity((Model)null, (Model)null);
	}

	@Test
	public void subjectSimilaritynotIntersection(){
		Model model = RDFDataMgr.loadModel("src/test/resources/root.ttl",Lang.TURTLE) ;
		Model modelData2 = RDFDataMgr.loadModel("src/test/resources/ro-sample.ttl",Lang.TURTLE) ;
		
		double sim = similarity.subjectSimilarity(model, modelData2);
		Assert.assertTrue(sim == 0);		
	}
	
	@Test
	public void similaritySubjectsSameModel(){
		Model model = RDFDataMgr.loadModel("src/test/resources/root.ttl",Lang.TURTLE) ;			
		double sim = similarity.subjectSimilarity(model, model);
		logger.info("Similitud de sujetos: "+sim);
		Assert.assertTrue(sim > 0);
		Assert.assertTrue(sim == 1);
		
	}
	
	@Test (expected = NullPointerException.class)
	public void onlyOneParameter(){
		Model modelData2 = RDFDataMgr.loadModel("src/test/resources/root.ttl",Lang.TURTLE) ;
		similarity.subjectSimilarity(null, modelData2);
	}	
}
