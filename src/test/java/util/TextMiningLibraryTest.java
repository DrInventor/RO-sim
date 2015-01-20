package util;

import java.util.List;

import org.junit.Test;

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

	@Test
	public void getContributors(){

		// Process a PDF file stored at the path: /my/file/path/PDF_file_name.pdf and get the Dr. Inventor Document instance
		String PDF_FILE_PATH = "/my/file/path/PDF_file_name.pdf";
		Document doc;
		try {
			doc = Factory.getPDFimporter().parsePDF(PDF_FILE_PATH);

			/* LOAD PREVIOUSLY STORED FILE CONTAINING A SERIALIZED XML DRI DOCUMENT */
			String serializedDRIdocumentPath = "/my/file/path/dri_serialized_document.xml";
			Document docLoaded;

			docLoaded = Factory.createNewDocument(serializedDRIdocumentPath);

			// Geth the sentence number 50 in the document
			Sentence sentDoc10 = docLoaded.extractSentences(SentenceSelectorENUM.ALL).get(10);

			// Get the COMPACT graph generated from the first sentence of the document (sentenceList)
			Graph grph = doc.extractSentenceGraph(sentDoc10.getId(), GraphTypeENUM.COMPACT);

			// Print the compact graph as a TREE
			System.out.print(grph.graph_AsString(GraphToStringENUM.TREE));

			// Get the extractive summary made of the top 10 TFIDF scored sentences.
			List<Sentence> summarySentences = docLoaded.extractSummary(10, SummaryTypeENUM.TOP_TFIDF);

		} catch (DRIexception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
