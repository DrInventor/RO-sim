package es.oeg.ro.dao;

import java.net.UnknownHostException;
import java.util.List;

import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import es.oeg.ro.transfer.AuthorBSON;
import es.oeg.ro.transfer.Paper;

public class DAOauthors {
	Logger logger = LoggerFactory.getLogger(this.getClass());	

	
	private DBCollection dbCollection;
	
	public DAOauthors(){
		// connect to the local database server
		MongoClient mongoClient;
		try {
			mongoClient = new MongoClient();
			// get handle to "mydb"
			DB db = mongoClient.getDB("mydb");
			dbCollection = db.createCollection("authors",null); 
		} catch (UnknownHostException e) {
			logger.error(e.getMessage());
		}				
	}

	public void add(List<String> authors){
		JacksonDBCollection<AuthorBSON, String> coll = JacksonDBCollection.wrap(dbCollection, AuthorBSON.class,
				String.class);
		AuthorBSON author = null;
		// me pasan la lista de autores de un paper y tengo que crear el BSON
		WriteResult<AuthorBSON, String> result = coll.insert(author);
	}
}
