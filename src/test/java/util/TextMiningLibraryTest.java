package util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.upf.taln.dri.lib.Factory;
import edu.upf.taln.dri.lib.exception.DRIexception;
import edu.upf.taln.dri.lib.model.Document;
import edu.upf.taln.dri.lib.model.ext.Graph;
import edu.upf.taln.dri.lib.model.ext.GraphToStringENUM;
import edu.upf.taln.dri.lib.model.ext.GraphTypeENUM;
import edu.upf.taln.dri.lib.model.ext.Sentence;
import edu.upf.taln.dri.lib.model.ext.SentenceSelectorENUM;
import edu.upf.taln.dri.lib.model.ext.SummaryTypeENUM;

public class TextMiningLibraryTest {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	
	// path where the PDF are stored
	public static final String PDF_FILE_PATH = "src/test/resources/exampleDRI.pdf";
	public static final String SERIALIZED_FILE_PATH = "src/test/resources/exampleDRIv3.xml";

	// variable for all the files
	private List<File> list = new ArrayList<File>();

	/** 
	 * Index all the DRI files that ends with .xml or v3.xml
	 * all the files to be indexed are in @directory
	 * 
	 * @param directory
	 * @return
	 */
	private List<File> indexAllFilesInDirectory(Path directory){

		if(directory== null) 
			return null;

		File file = directory.toFile();
		if (!file.exists()) {
			System.out.println(directory + " does not exist.");
		}
		else 
			if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					indexAllFilesInDirectory(f.toPath());
				}
			} else {
				String filename = file.getName().toLowerCase();		      
				// Only index xml files with annotations	      
				if (filename.endsWith(".xml") && filename.endsWith("v3.xml")) {
					list.add(file);
				} else {
					System.out.println("Skipped " + filename);
				}
			}
		return list;
	}
	
	
	@Test
	public void testExtractSentencesPDF() throws DRIexception{

		// Process a PDF file stored at the path
		Document doc;
		
		doc = Factory.getPDFimporter().parsePDF(PDF_FILE_PATH);
		assertNotNull(doc);
//		

		// Geth the sentence number 50 in the document
		Sentence sentDoc10 = doc.extractSentences(SentenceSelectorENUM.ALL).get(10);
		assertNotNull(sentDoc10);
		logger.info(sentDoc10.getSectionName());
		logger.info(sentDoc10.getText());
		//TODO  INFO [main] (TextMiningLibraryTest.java:86) - IMPLEMENTED_IN_FUTURE_VERSIONS_OF_THE_LIB
		// function not implemented yet
		logger.info(sentDoc10.getRhethoricalClass().name());
		
	}
	
	@Test
	public void testExtractSentencesSerializedDocument() throws DRIexception{
		
		// Process a PDF file stored at the path
		Document docLoaded;
		
	
		docLoaded = Factory.createNewDocument(SERIALIZED_FILE_PATH);
		assertNotNull(docLoaded);
		
		assertNotNull(docLoaded.getXMLString());
		logger.info(docLoaded.getXMLString());
		
		// Geth the sentence number 50 in the document		
		Sentence sentDoc10Serialized = docLoaded.extractSentences(SentenceSelectorENUM.ALL).get(50);
		assertNotNull(sentDoc10Serialized);		
	}
	
	
	@Test
	public void testExtractGraphFromPDF() throws DRIexception{
		// Process a PDF file stored at the path
		Document doc;
		
		doc = Factory.getPDFimporter().parsePDF(PDF_FILE_PATH);
		assertNotNull(doc);
		// Geth the sentence number 10 in the document
		Sentence sentDoc10 = doc.extractSentences(SentenceSelectorENUM.ALL).get(10);
		assertNotNull(sentDoc10);
		
		// Get the graph of this sentence
		Graph grph = doc.extractSentenceGraph(sentDoc10.getId(), GraphTypeENUM.COMPACT);
		assertNotNull(grph);
		
		// Print the compact graph as a TREE
		logger.info(grph.graph_AsString(GraphToStringENUM.TREE));
	}
	
	@Test
	public void testExtractGraphFromXML() throws DRIexception{
		Document docLoaded;
		docLoaded = Factory.createNewDocument(SERIALIZED_FILE_PATH);
		assertNotNull(docLoaded);
		Sentence sentDoc10Serialized = docLoaded.extractSentences(SentenceSelectorENUM.ALL).get(1);
		assertNotNull(sentDoc10Serialized);
		// Get the COMPACT graph generated from the first sentence of the document (sentenceList)
		Graph grphSerialized = docLoaded.extractSentenceGraph(sentDoc10Serialized.getId(), GraphTypeENUM.COMPACT);
		assertNotNull(grphSerialized);
	}
	
	@Test
	public void testExtractSummaryFromPDF() throws DRIexception{
		Document doc;
		
		doc = Factory.getPDFimporter().parsePDF(PDF_FILE_PATH);
		assertNotNull(doc);
		List<Sentence> summarySentences = doc.extractSummary(10, SummaryTypeENUM.TOP_TFIDF);
		assertNotNull(summarySentences);
		assertTrue(summarySentences.size() > 0);
		
		for(Sentence s: summarySentences)
			logger.info(s.getText());

	}
	
	@Test
	public void testExtractSummaryFromXML() throws DRIexception{
		Document docLoaded;
		//		
		docLoaded = Factory.createNewDocument(SERIALIZED_FILE_PATH);
		assertNotNull(docLoaded);
		
		// Get the extractive summary made of the top 10 TFIDF scored sentences.
		List<Sentence> summarySentencesSerialized = docLoaded.extractSummary(5, SummaryTypeENUM.TOP_TFIDF);
		assertNotNull(summarySentencesSerialized);
		assertTrue(summarySentencesSerialized.size() > 0);		
	}
	
	@Test
	public void testExtractAuthorsFromPDF() throws DRIexception{
		Document doc;
		
		doc = Factory.getPDFimporter().parsePDF(PDF_FILE_PATH);
		String contributors = doc.extractContributors(); 
		assertNotNull(contributors);
		
		logger.info(contributors);
	}
	
}
