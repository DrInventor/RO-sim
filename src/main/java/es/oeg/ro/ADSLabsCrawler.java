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

import es.oeg.ro.transfer.ADSLabsResultsBean;

public class ADSLabsCrawler {

	// FIXME poner el verdadro dev_key
	// TODO pasar a fichero de properties
	private final static String dev_key = "abc123";

	private final static String adslabs_uri ="adslabs.org/adsabs/api/";
	private final static String scheme_uri = "http";
	private final static String path_search = "search/";

	Logger logger = LoggerFactory.getLogger(this.getClass());


	public void executeQuery(String query, String filter) throws IOException{


		CloseableHttpClient httpclient = HttpClients.createDefault();

		CloseableHttpResponse response = null;

		try {
			URI uri = new URIBuilder()
			.setScheme(scheme_uri)
			.setHost(adslabs_uri)
			.setPath(path_search)
			.setParameter("q", query)
			.setParameter("filter", filter)			
			.setParameter("dev_key", dev_key)        
			.build();

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

			if (entity != null) {
				InputStream instream = entity.getContent();
				
				try {
					// do something useful
					ObjectMapper mapper =  new ObjectMapper();				

					ADSLabsResultsBean map = mapper.readValue(instream, ADSLabsResultsBean.class);

					logger.info("Meta section from the response: "+map.getMeta());
					logger.info("Query results \n Size: "+map.getResults().get_docs().size());
					logger.info("List of docs retrieved: "+map.getResults().toString());		
					

				} finally {
					instream.close();
				}
			}




		} catch (URISyntaxException e) {
			e.printStackTrace();

		} finally {
			response.close();
		}
	}

	public void accessSettings() throws IOException{
		//		http://adslabs.org/adsabs/api/settings/?dev_key=...

		CloseableHttpClient httpclient = HttpClients.createDefault();

		CloseableHttpResponse response = null;

		try {
			URI uri = new URIBuilder()
			.setScheme(scheme_uri)
			.setHost(adslabs_uri)
			.setPath("settings")						
			.setParameter("dev_key", dev_key)        
			.build();

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
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				logger.info(output);
			}

		} catch (URISyntaxException e) {
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			response.close();
		}
	}

	public static void main (String[] args) throws IOException{
		ADSLabsCrawler crawler = new ADSLabsCrawler();
		//		# Simple search for "black holes", restricted to astronomy content
		//		http://adslabs.org/adsabs/api/search/?q=black+holes&filter=database:astronomy&dev_key=abc123

		//		crawler.executeQuery("transiting exoplanets", "database:astronomy");		
//		crawler.executeQuery("author:Accomazzi, Alberto", null);		
		crawler.accessSettings();		
//				crawler.executeQuery("transiting exoplanets", "database:astronomy");		
	}
}
