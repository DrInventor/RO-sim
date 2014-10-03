package similarity;

import java.util.Set;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;

public class StructuralSimilarityTest {

	Logger logger = LoggerFactory.getLogger(this.getClass());
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
		logger.info("Similitud de sujetos: "+sim);
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
		logger.info("Similitud de objetos: "+sim);
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
		
		Set<Statement> set = similarity.commonStatements(similarity.stmt2List(iter), similarity.stmt2List(iter2));
		logger.info("Set: "+set.toString());		
	}
	
	@Test(expected = NullPointerException.class)
	public void isInModelNullStatement(){
		Model modelData2 = RDFDataMgr.loadModel("src/test/resources/data2.ttl",Lang.TURTLE) ;		
		similarity.isInModel(modelData2, null);		
	}
	
	@Test(expected = NullPointerException.class)
	public void isInModelNullModel(){
		similarity.isInModel(null, null);		
	}
	
	@Test
	public void isInModelFalse(){
		Model modelData2 = RDFDataMgr.loadModel("src/test/resources/data2.ttl",Lang.TURTLE) ;
		Resource subject = ResourceFactory.createResource("http://somewhere/JohnSmith");
        Property predicate = ResourceFactory.createProperty("http://www.w3.org/2001/vcard-rdf/3.0#N");
        Resource objectResource = ResourceFactory.createResource();
        Statement statement = ResourceFactory.createStatement(subject, 
                                                              predicate,                                                                                                      
                                                              objectResource);
		
		Assert.assertFalse(similarity.isInModel(modelData2, statement));
	}
	@Test
	//FIXME mirar bien cómo se crean los resources asociados al modelo porque está fallando
	public void isInModelTrue(){
		Model modelData2 = RDFDataMgr.loadModel("src/test/resources/data2.ttl",Lang.TURTLE) ;	
		
//		[http://example.org/charlie, http://xmlns.com/foaf/0.1/knows, http://example.org/andreas]
		Resource subject = ResourceFactory.createResource("http://example.org/charlie");
		Property predicate = ResourceFactory.createProperty("http://xmlns.com/foaf/0.1/knows");
		Resource object = ResourceFactory.createResource("http://example.org/andreas");		
		Statement statement = modelData2.createStatement(subject, predicate, object);
		modelData2.add(statement);
		Assert.assertTrue(similarity.isInModel(modelData2, statement));
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
