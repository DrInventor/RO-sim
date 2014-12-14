package es.oeg.ro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.oeg.om.util.Locator;
import es.oeg.ro.transfer.ADSLabsResultsBean;

public class ADSLabsClient {

	private String dev_key;

	private final static String adslabs_uri ="adslabs.org/adsabs/api/";
	private final static String scheme_uri = "http";
	private final static String path_search = "search/";
	private final static String path_settings = "settings";

	Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	public ADSLabsClient(){
		// locate the dev_key
		dev_key = Locator.getDevKey(".ads/dev_key");
		logger.info("dev key : "+dev_key);
		if (dev_key == null)
			logger.error("Dev key not found");
	}
	
	public URI buildURI(String query) throws URISyntaxException{
		URI uri = new URIBuilder().setScheme(scheme_uri).setHost(adslabs_uri)
				.setPath(path_search)
				.setParameter("q", query)				
				.setParameter("dev_key", dev_key)        
				.build();
		return uri;
	}
	
	public URI buildURI(String query, String filter) throws URISyntaxException{
		URI uri = new URIBuilder()
		.setScheme(scheme_uri)
		.setHost(adslabs_uri)
		.setPath(path_search)
		.setParameter("q", query)			
		.setParameter("filter", filter)			
		.setParameter("dev_key", dev_key)        
		.build();
		return uri;
	}
	
	public ADSLabsResultsBean executeQuery(URI uri) throws ClientProtocolException, IOException{
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		
		try {
			
			HttpGet httpget = new HttpGet(uri);
			
			// en prod no es buena idea mostrar dev_key !!!
			logger.debug("Query executed: "+httpget.getURI());
			
			response =  httpclient.execute(httpget);
			
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}
			
			logger.info("Response: "+response.getStatusLine().getReasonPhrase());
			
			HttpEntity entity = response.getEntity();
			
			if (entity != null) {
				InputStream instream = entity.getContent();
				
				try {
					// do something useful
					ObjectMapper mapper =  new ObjectMapper();
					ADSLabsResultsBean map = mapper.readValue(instream, ADSLabsResultsBean.class);
					logger.info("Meta section from the response: "+map.getMeta());
					logger.info("Query results \n Size: "+map.getResults().get_docs().size());
					logger.info("List of docs retrieved: "+map.getResults().toString());						
					return map;
					
				} finally {
					instream.close();
				}
			}
			
		}  finally {
			response.close();
		}
		return null;
	}	
	
	public ADSLabsResultsBean search(String query) throws ClientProtocolException, IOException, URISyntaxException{
		URI uri = buildURI(query);
		return executeQuery(uri);		
	}
	
	public ADSLabsResultsBean search(String query, String filter) throws IOException, URISyntaxException{
		URI uri = buildURI(query,filter);
		return executeQuery(uri);
	}

	public void accessSettings() throws IOException{
		//	http://adslabs.org/adsabs/api/settings/?dev_key=...

		CloseableHttpResponse response = null;
		
		try {
			URI uri = new URIBuilder()
			.setScheme(scheme_uri)
			.setHost(adslabs_uri)
			.setPath(path_settings)						
			.setParameter("dev_key", dev_key)        
			.build();

			CloseableHttpClient httpclient = HttpClients.createDefault();			

			HttpGet httpget = new HttpGet(uri);

			// en prod no es buena idea mostrar dev_key !!!
			logger.info("Query executed: "+httpget.getURI());

			response =  httpclient.execute(httpget);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}

			logger.info("Response: "+response.getStatusLine().getReasonPhrase());

			HttpEntity entity = response.getEntity();

			BufferedReader br = new BufferedReader(
					new InputStreamReader((entity.getContent())));

			String output;
			logger.info("Output from Server ....");
			while ((output = br.readLine()) != null) {
				logger.info(output);
			}

		} catch (URISyntaxException e) {
			logger.error(e.getMessage());
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			response.close();
		}
	}

}
