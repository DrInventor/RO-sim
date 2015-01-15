package RO;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.oeg.ro.communication.ADSLabsClient;
import es.oeg.ro.transfer.ADSLabsResultsBean;

public class ADSLabsClientTest {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	ADSLabsClient crawler = new ADSLabsClient();

	@Test
	public void executeQueryWithTerms() throws IOException, URISyntaxException{
		//		# Simple search for "black holes", restricted to astronomy content
		//		http://adslabs.org/adsabs/api/search/?q=black+holes&filter=database:astronomy&dev_key=abc123
		ADSLabsResultsBean beanResults = crawler.search("black holes");
		assertNotNull(beanResults);
		assertNotNull(beanResults.getMeta());
		assertNotNull(beanResults.getResults());
		assertTrue(beanResults.getResults().get_docs().size() > 0);		
	}
	
	@Test
	public void executeQueryWithFilter() throws ClientProtocolException, IOException, URISyntaxException{
		ADSLabsResultsBean beanResults = crawler.search("transiting exoplanets", "database:astronomy");
		assertNotNull(beanResults);
		assertNotNull(beanResults.getMeta());
		assertNotNull(beanResults.getResults());
		assertTrue(beanResults.getResults().get_docs().size() > 0);		
	}
	
	@Test
	public void executeQueryWithAuthorFilter() throws IOException, URISyntaxException{
		ADSLabsResultsBean beanResults = crawler.search("author:Accomazzi, Alberto");		
		assertNotNull(beanResults);
		assertNotNull(beanResults.getMeta());
		assertNotNull(beanResults.getResults());
		assertTrue(beanResults.getResults().get_docs().size() > 0);		
	}
	
	@Test
	public void checkPermissions() throws IOException{
		crawler.accessSettings();
	}

	@Test
	public void searchCGpaper() throws ClientProtocolException, IOException, URISyntaxException{
		crawler.searchByKeyword();
	}
	
	@Test
	public void searchPaperFromyears() throws ClientProtocolException, IOException, URISyntaxException{
		crawler.search(null,"2006","2006");
	}
	
	@Test
	public void searchByDoi(){
		String doi = "10.1086/345794";
		String query="doi:"
				+ doi;
		try {
			ADSLabsResultsBean bean = crawler.search(query);
			logger.info("Retrieved paper: "+bean.toString());
			// tengo el origen, ahora hay que sugerir
			// similitud de autores -> recuperar la
		} catch (IOException | URISyntaxException e) {
			logger.error("Something went wrong: "+e.getMessage());
		}
	}
	@Test
	public void searchByAuthor(){
		String author = "\"Jarrett, T. H.\"";
		String query="author:"
				+ author;
		try {
			ADSLabsResultsBean bean = crawler.search(query);
			logger.info("Retrieved paper: "+bean.toString());
			// tengo el origen, ahora hay que sugerir
			// similitud de autores -> recuperar la
		} catch (IOException | URISyntaxException e) {
			logger.error("Something went wrong: "+e.getMessage());
		}
	}
}
