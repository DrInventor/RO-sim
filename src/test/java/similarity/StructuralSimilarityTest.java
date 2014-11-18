package similarity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

import es.oeg.om.similarity.StructuralSimilarity;

public class StructuralSimilarityTest {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	private StructuralSimilarity similarity = new StructuralSimilarity();

	
	// -- STATEMENTS COMPARTIDOS
	
	@Test
	public void statementSimilarityDifferentsModels(){
		// Create a model and read into it from file 
		// "data.ttl" assumed to be Turtle.
		Model model = RDFDataMgr.loadModel("src/test/resources/data.ttl",Lang.TURTLE) ;
		Model modelData2 = RDFDataMgr.loadModel("src/test/resources/data2.ttl",Lang.TURTLE) ;
		double sim = similarity.statementSimilarity(model, modelData2); 
		assertTrue(sim > 0);		
		assertTrue(sim < 1);		
	}
	
	@Test
	public void statementSimilaritySameModels(){
		// Create a model and read into it from file 
		// "data.ttl" assumed to be Turtle.
		Model model = RDFDataMgr.loadModel("src/test/resources/data.ttl",Lang.TURTLE) ;
		double sim = similarity.statementSimilarity(model, model); 
		assertTrue(sim > 0);		
		assertTrue(sim == 1);		
	}
	
	@Test
	public void computeStatementSimilarityEmptyModels(){
		Model model1 = ModelFactory.createDefaultModel();
		double sim = similarity.statementSimilarity(model1, model1);
		// definition of jaccard index
		assertTrue (sim == 1);
	}
	// is in model
	
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
	
	
		
//	-- ore:aggregates test
	@Test
	public void hasAgreggatedResources(){
		Model model = RDFDataMgr.loadModel("src/test/resources/ro-sample.ttl",Lang.TURTLE) ;		
		assertTrue(similarity.hasAgreggatedResources(model));
	}
	
	@Test (expected  = NullPointerException.class)
	public void hasAgreggatedResourcesNullParameter(){
		similarity.hasAgreggatedResources(null);
	}
	
	@Test
	public void hasAgreggatedResourcesFalse(){
		Model model = RDFDataMgr.loadModel("src/test/resources/data.ttl",Lang.TURTLE) ;		
		assertFalse(similarity.hasAgreggatedResources(model));
	}
	
//	-- number of agreggates resources in model
	@Test(expected = NullPointerException.class)
	public void numberAgreggatesError(){
		assertTrue(similarity.numberOfAggregatedResources(null) < 0);		
	}
	
	@Test
	public void numberAgreggatesZero(){
		Model model = RDFDataMgr.loadModel("src/test/resources/ro-sample.ttl",Lang.TURTLE) ;
		assertTrue(similarity.numberOfAggregatedResources(model) > 0);
	}
	
	// sets of aggregates resources	
	@Test
	public void sharedAgreggatesResourcesFalse(){
		Model model1 = RDFDataMgr.loadModel("src/test/resources/ro-sample.ttl",Lang.TURTLE) ;
		Model model2 = RDFDataMgr.loadModel("src/test/resources/data.ttl",Lang.TURTLE) ;
		Set<Object> set = similarity.sharedAggregatedResources(model1,model2);
		assertNull(set);
	}
	@Test
	public void sharedAgreggatesResourcesSameModel(){
		Model model1 = RDFDataMgr.loadModel("src/test/resources/ro-sample.ttl",Lang.TURTLE) ;
		Set<Object> set = similarity.sharedAggregatedResources(model1,model1);
		assertNotNull(set);
		assertTrue(set.size() > 0);
	}
	
	@Test
	public void sharedAgreggatesResourcesTrue(){
		Model model1 = RDFDataMgr.loadModel("src/test/resources/ro-sample.ttl",Lang.TURTLE) ;
		Model model2 = RDFDataMgr.loadModel("src/test/resources/ro-folders.ttl",Lang.TURTLE) ;
		Set<Object> set = similarity.sharedAggregatedResources(model1,model2);
		assertNotNull(set);
		assertTrue(set.size() > 0);
		assertTrue(set.size() == 1);
	}
	
	// union of aggregated resources 
	@Test
	public void sizeOfDifferenceModelSameModel(){
		Model model1 = RDFDataMgr.loadModel("src/test/resources/ro-sample.ttl",Lang.TURTLE) ;
		double size = 9;
		assertTrue(similarity.unionOfAgreggatedResources(model1, model1) == size);
	}
	
	@Test
	public void sizeOfDifferenceModelDifferentModelOnly1Shared(){
		Model model1 = RDFDataMgr.loadModel("src/test/resources/ro-sample.ttl",Lang.TURTLE) ;
		Model model2 = RDFDataMgr.loadModel("src/test/resources/ro-folders.ttl",Lang.TURTLE) ;

		assertTrue(similarity.unionOfAgreggatedResources(model1, model2) > 0);		
		assertTrue(similarity.unionOfAgreggatedResources(model1, model2) == 19);		
	}
	
	
	
	// -- COMPLETE TEST SIMILARITY
		@Test
		public void computeSimilarityTotal(){
			// Create a model and read into it from file 
			// "data.ttl" assumed to be Turtle.
			Model model = RDFDataMgr.loadModel("src/test/resources/ro-sample.ttl",Lang.TURTLE) ;
			Model modelData2 = RDFDataMgr.loadModel("src/test/resources/ro-folders.ttl",Lang.TURTLE) ;		
			double sim = similarity.computeStructuralSimilarity(model, modelData2);
			System.out.println("Sim total: "+sim);	
			assertTrue(sim > 0);		
			assertTrue(sim < 1);
		}

		@Test
		public void computeSimilarityTotalSameModel(){
			// Create a model and read into it from file 
			// "data.ttl" assumed to be Turtle.
			Model model = RDFDataMgr.loadModel("src/test/resources/ro-sample.ttl",Lang.TURTLE) ;				
			double sim = similarity.computeStructuralSimilarity(model, model);
			System.out.println("Sim total: "+sim);
			assertTrue(sim > 0);		
			assertTrue(sim == 1);
		}
		
		@Test (expected  = NullPointerException.class)
		public void computeSimilarityTotalNullModel(){
			similarity.computeStructuralSimilarity(null, null);		
		}
}
