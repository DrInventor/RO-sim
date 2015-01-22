package similarity;

import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;

import org.apache.jena.atlas.AtlasException;
import org.junit.Before;
import org.junit.Test;

import es.upm.oeg.cidercl.extraction.OntologyExtractor;
import es.upm.oeg.semanticmeasures.impl.crosslingual.CLESABetweenOntologyEntities;
import es.upm.oeg.semanticmeasures.impl.monolingual.SoftTFIDFBetweenOntologyEntities;

/*
 * pruebas de similitud usando CIDER-CL como servicio para los papers anotados con SDO
 */
public class CiderSimilarityTest {
	
	
	
//	@Test
	public void similarityBetweenTwoInstancesSameOntology(){
		
		try{
		double monoSim, clSim;
		 
//		// programm committee
//		String uriA = "http://www.example.org/sentence18988";
//
//		String ontologyA = "file:./src/test/resources/data/sdo-A01S01APowellOptimizationApproachforExampleBasedSkinningttl.ttl";
//
//		// comite de programa
//
//		String uriB = "http://www.example.org/sentence8988";
//		String ontologyB = "file:./src/test/resources/exampleCreator-2.ttl";
		
		/// EJEMPLO CIDER
		// programm committee
				String uriA = "http://www.example.org/index.html";

				String ontologyA = "file:./src/test/resources/exampleCreator.ttl";

				// comite de programa

				String uriB = "http://www.example.org/index.html";
				String ontologyB = "file:./test/exampleCreator-2.ttl";

				
				
		SoftTFIDFBetweenOntologyEntities measure = new SoftTFIDFBetweenOntologyEntities(ontologyA, ontologyA);
		monoSim = measure.getValue(OntologyExtractor.modelObtaining(ontologyA), uriA , 
				OntologyExtractor.modelObtaining(ontologyA), uriB);

		System.out.println( "---MONO-LINGUAL SIMILARITY between " + uriA + " and " + uriB + ": " + monoSim + " ---");
		}
		catch (AtlasException e){
			e.printStackTrace();			
		}
		
	}
	
	public static void main(String args[]) {
		  
		double monoSim, clSim;
		 
//		String uriA = "http://cmt_en#c-4268400-1612321";
//		String ontologyA = "file:./test/cmt-en.owl";
//		String uriB = "http://cmt_es#c-7348985-5560772";
//		String ontologyB = "file:./test/cmt-es.owl";
		String uriC = "http://sigkdd_en#c-0407849-6361536";
		String ontologyC = "file:./test/sigkdd-en.owl";
		
		// programm committee
		String uriA = "http://www.example.org/index.html";

		String ontologyA = "file:./src/test/resources/exampleCreator.ttl";

		// comite de programa

		String uriB = "http://www.example.org/index.html";
		String ontologyB = "file:./src/test/resources/exampleCreator-2.ttl";
				
//		CLESABetweenOntologyEntities clesa = new CLESABetweenOntologyEntities();
//
//		clSim = clesa.getValue(OntologyExtractor.modelObtaining(ontologyA),  uriA,  null, 
//				OntologyExtractor.modelObtaining(ontologyB) , uriB, null);
	
		SoftTFIDFBetweenOntologyEntities measure = new SoftTFIDFBetweenOntologyEntities(ontologyA, ontologyB);
//		SoftTFIDFBetweenOntologyEntities measure = new SoftTFIDFBetweenOntologyEntities(ontologyA, ontologyC);
		monoSim = measure.getValue(OntologyExtractor.modelObtaining(ontologyA), uriA , 
				OntologyExtractor.modelObtaining(ontologyB), uriB);

//		System.out.println( "---CROSS-LINGUAL SIMILARITY between " + uriA + " and " + uriB + ": " + clSim + " ---");		  			  	
		System.out.println( "---MONO-LINGUAL SIMILARITY between " + uriA + " and " + uriC + ": " + monoSim + " ---");
		
	 }// end-main	

}
