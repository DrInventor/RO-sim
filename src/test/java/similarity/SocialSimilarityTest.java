package similarity;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.oeg.om.similarity.SocialSimilarity;
import es.oeg.ro.communication.ADSLabsClient;
import es.oeg.ro.transfer.ADSLabsResultsBean;
import es.oeg.ro.transfer.Paper;

public class SocialSimilarityTest {
	SocialSimilarity social = new SocialSimilarity();
	ADSLabsClient adsClient = new ADSLabsClient();
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

	
	@Test
	public void testSocialAuthors() throws ClientProtocolException, IOException, URISyntaxException{
		String query ="doi:10.1007/s00033-013-0394-1";
		// necesito dos papers
		ADSLabsResultsBean result = adsClient.search(query);
		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getResults().get_docs().get(0));
		
		Paper p1 = result.getResults().get_docs().get(0);
		logger.debug("Paper 1: "+p1.toString());
		
		query = "doi:"
				+ "10.1007/s00033-013-0378-1";
		result = adsClient.search(query);
		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getResults().get_docs().get(0));
		
		Paper p2 = result.getResults().get_docs().get(0);
		logger.debug("Paper 2: "+p2.toString());
		
		social.socialSimilarity(p1, p2);
	}
	
	
}
