package RO;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.oeg.ro.ADSLabsClient;
import es.oeg.ro.transfer.ADSLabsResultsBean;

public class ADSLabsCrawlerTest {
	
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
}
