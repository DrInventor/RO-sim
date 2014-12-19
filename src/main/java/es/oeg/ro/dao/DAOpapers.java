package es.oeg.ro.dao;

import java.net.UnknownHostException;

import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import es.oeg.ro.transfer.ADSLabsResultsBean;
import es.oeg.ro.transfer.Paper;

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
	
	public void addResult(ADSLabsResultsBean toAdd){
		
		JacksonDBCollection<ADSLabsResultsBean, String> coll = JacksonDBCollection.wrap(dbCollection, ADSLabsResultsBean.class,
				String.class);
		WriteResult<ADSLabsResultsBean, String> result = coll.insert(toAdd);
		String id = result.getSavedId();
		logger.info("Id of the saved paper: "+id);
//		ADSLabsResultsBean savedObject = coll.findOneById(id);
//		logger.info("Objeto: "+savedObject.toString());
		DBCursor<ADSLabsResultsBean> cursor = coll.find();
        int i=1;
        while (cursor.hasNext()) { 
           logger.debug("Inserted Document: "+i); 
           logger.debug(cursor.next().toString()); 
           i++;
        }
	}
	
	public void add(Paper paper){
		JacksonDBCollection<Paper, String> coll = JacksonDBCollection.wrap(dbCollection, Paper.class,
				String.class);
		WriteResult<Paper, String> result = coll.insert(paper);
//		String id = result.getSavedId();
//		logger.info("Id of the saved paper: "+id);
//		
	}

}
