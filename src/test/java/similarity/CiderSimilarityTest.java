package similarity;

import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;

import org.apache.jena.atlas.AtlasException;
import org.junit.Test;

import es.upm.oeg.cidercl.extraction.OntologyExtractor;
import es.upm.oeg.semanticmeasures.impl.monolingual.SoftTFIDFBetweenOntologyEntities;

/*
 * pruebas de similitud usando CIDER-CL como servicio para los papers anotados con SDO
 */
public class CiderSimilarityTest {
	
	@Test
	public void similarityBetweenTwoInstancesSameOntology(){
		
		try{
		double monoSim, clSim;
		 
		// programm committee
		String uriA = "http://www.example.org/sentence18988";

		String ontologyA = "file:./src/test/resources/data/sdo-A01S01APowellOptimizationApproachforExampleBasedSkinningttl.ttl";

		// comite de programa

		String uriB = "http://www.example.org/sentence8988";
		String ontologyB = "file:./src/test/resources/exampleCreator-2.ttl";

		SoftTFIDFBetweenOntologyEntities measure = new SoftTFIDFBetweenOntologyEntities(ontologyA, ontologyA);
		monoSim = measure.getValue(OntologyExtractor.modelObtaining(ontologyA), uriA , 
				OntologyExtractor.modelObtaining(ontologyA), uriB);

		System.out.println( "---MONO-LINGUAL SIMILARITY between " + uriA + " and " + uriB + ": " + monoSim + " ---");
		}
		catch (AtlasException e){
			e.printStackTrace();			
		}
		
	}

}
