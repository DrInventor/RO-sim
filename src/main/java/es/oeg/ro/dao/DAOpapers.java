package es.oeg.ro.dao;

import java.net.UnknownHostException;

import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import es.oeg.ro.transfer.ADSLabsResultsBean;

public class DAOpapers {

	Logger logger = LoggerFactory.getLogger(this.getClass());	

	
	private DBCollection dbCollection;
	
	public DAOpapers(){
		// connect to the local database server
		MongoClient mongoClient;
		try {
			mongoClient = new MongoClient();
			// get handle to "mydb"
			DB db = mongoClient.getDB("mydb");
			dbCollection = db.createCollection("papers",null); 
		} catch (UnknownHostException e) {
			logger.error(e.getMessage());
		}				
	}
	
	public void add(ADSLabsResultsBean toAdd){
		
		JacksonDBCollection<ADSLabsResultsBean, String> coll = JacksonDBCollection.wrap(dbCollection, ADSLabsResultsBean.class,
				String.class);
		WriteResult<ADSLabsResultsBean, String> result = coll.insert(toAdd);
		String id = result.getSavedId();
		logger.info("Id of the saved paper: "+id);
		ADSLabsResultsBean savedObject = coll.findOneById(id);
		logger.info("Objeto: "+savedObject.toString());
	}

}
