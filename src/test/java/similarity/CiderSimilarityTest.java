package similarity;

import org.junit.Test;

import es.upm.oeg.cidercl.extraction.OntologyExtractor;
import es.upm.oeg.semanticmeasures.impl.monolingual.SoftTFIDFBetweenOntologyEntities;

/*
 * pruebas de similitud usando CIDER-CL como servicio para los papers anotados con SDO
 */
public class CiderSimilarityTest {
	
	@Test
	public void similarityBetweenTwoInstancesSameOntology(){
		
		double monoSim, clSim;
		 
		// programm committee
		String uriA = "http://www.example.org/index.html";

		String ontologyA = "file:./src/test/resources/exampleCreator.ttl";

		// comite de programa

		String uriB = "http://www.example.org/index.html";
		String ontologyB = "file:./src/test/resources/exampleCreator-2.ttl";

		SoftTFIDFBetweenOntologyEntities measure = new SoftTFIDFBetweenOntologyEntities(ontologyA, ontologyB);
		monoSim = measure.getValue(OntologyExtractor.modelObtaining(ontologyA), uriA , 
				OntologyExtractor.modelObtaining(ontologyB), uriB);

		System.out.println( "---MONO-LINGUAL SIMILARITY between " + uriA + " and " + uriB + ": " + monoSim + " ---");
		
	}

}
