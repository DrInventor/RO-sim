package similarity;

import static org.junit.Assert.*;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;

import es.oeg.om.similarity.LexicalSimilarity;

public class LexicalSimilarityTest {
	private LexicalSimilarity lexicalSimilarity = new LexicalSimilarity();
	
	@Test
	public void getAllDcAuthorsPrint(){
		Model m = RDFDataMgr.loadModel("src/test/resources/exampleCreator.ttl",Lang.TURTLE) ;
		assertNotNull(lexicalSimilarity.getDCCreators(m));
	}
	
	@Test(expected=NullPointerException.class)
	public void getAllDcAuthorsNullParameterthrowsException(){
		assertNotNull(lexicalSimilarity.getDCCreators(null));		
	}
	
	@Test
	public void getAllDcAuthorsNullResults(){
		Model m = RDFDataMgr.loadModel("src/test/resources/data.ttl",Lang.TURTLE) ;
		assertTrue(lexicalSimilarity.getDCCreators(m).isEmpty());
		assertTrue(lexicalSimilarity.getDCCreators(m).size() == 0);
		
	}
	
	// test related to shared creators
	
	@Test
	public void sharesDCCreatorYesSameModel(){
		Model m1 = RDFDataMgr.loadModel("src/test/resources/exampleCreator.ttl",Lang.TURTLE) ;
		Model m2 = RDFDataMgr.loadModel("src/test/resources/exampleCreator.ttl",Lang.TURTLE) ;
		assertTrue(lexicalSimilarity.sharesCreators(m1, m2));
	}
	
	@Test
	public void sharesDCCreatorYes(){
		Model m1 = RDFDataMgr.loadModel("src/test/resources/exampleCreator.ttl",Lang.TURTLE) ;
		Model m2 = RDFDataMgr.loadModel("src/test/resources/ro-sample.ttl",Lang.TURTLE) ;
		assertTrue(lexicalSimilarity.sharesCreators(m1, m2));
	}
	
	@Test
	public void shareDCCreatorNotFoundPropertyInModel(){
		Model m1 = RDFDataMgr.loadModel("src/test/resources/exampleCreator.ttl",Lang.TURTLE) ;
		Model m2 = RDFDataMgr.loadModel("src/test/resources/data.ttl",Lang.TURTLE) ;
		assertFalse(lexicalSimilarity.sharesCreators(m1, m2));
	}
	
	@Test
	public void shareDCCreatorNot(){
		Model m1 = RDFDataMgr.loadModel("src/test/resources/manifest.ttl",Lang.TURTLE) ;
		Model m2 = RDFDataMgr.loadModel("src/test/resources/ro-sample.ttl",Lang.TURTLE) ;
		assertFalse(lexicalSimilarity.sharesCreators(m1, m2));
	}
	
	@Test
	public void completeTestSimilarityNoSharesAuthorNoSimilar(){
		Model m1 = RDFDataMgr.loadModel("src/test/resources/manifest.ttl",Lang.TURTLE) ;
		Model m2 = RDFDataMgr.loadModel("src/test/resources/ro-sample.ttl",Lang.TURTLE) ;
		assertTrue(lexicalSimilarity.authorSimilarity(m1, m2) == 0);
	}
	
	@Test
	public void completeTestSimilaritySameAuthor(){
		Model m1 = RDFDataMgr.loadModel("src/test/resources/exampleCreator.ttl",Lang.TURTLE) ;
		Model m2 = RDFDataMgr.loadModel("src/test/resources/ro-sample.ttl",Lang.TURTLE) ;
		assertTrue(lexicalSimilarity.authorSimilarity(m1, m2) > 0);
		assertTrue(lexicalSimilarity.authorSimilarity(m1, m2) == 1);
	}
	@Test
	public void completeTestSimilarityAuthors(){
		Model m1 = RDFDataMgr.loadModel("src/test/resources/exampleCreator-2.ttl",Lang.TURTLE) ;
		Model m2 = RDFDataMgr.loadModel("src/test/resources/ro-sample.ttl",Lang.TURTLE) ;
		assertTrue(lexicalSimilarity.authorSimilarity(m1, m2) > 0);
		assertTrue(lexicalSimilarity.authorSimilarity(m1, m2) < 1);
	}
}

