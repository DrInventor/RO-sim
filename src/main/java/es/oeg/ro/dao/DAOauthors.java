package es.oeg.ro.dao;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
//import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import es.oeg.ro.transfer.AuthorBSON;
import es.oeg.ro.transfer.Colleague;

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
		//		if (authors == null || authors.size() <= 0)
		//			throw new NullPointerException();

		if (authors != null && authors.size() > 0){


			JacksonDBCollection<AuthorBSON, String> coll = JacksonDBCollection.wrap(dbCollection, AuthorBSON.class,
					String.class);		

			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("name", authors.get(0));
			DBCursor<AuthorBSON> result = coll.find(whereQuery);
			if (result.size() > 0)
				while(result.hasNext()) {
					AuthorBSON object = result.next();
					// are the same colleagues?
					if (object.getColl() != null && object.getColl().size() > 0){
						logger.debug("update coauthors");
						object = updateColleagues(coll, object, authors);
					}
					else{
						logger.debug("Not co-authors yet");
						object = addColleagues(object,authors);
					}
				}
			else{
				//EL AUTOR PRINCIPAL NO ESTÁ
				AuthorBSON author = new AuthorBSON();
				author.setName(authors.get(0));
				// FIXME hay que mirar si están los coautores
				author = addColleagues(author, authors);			
				// me pasan la lista de autores de un paper y tengo que crear el BSON
				WriteResult<AuthorBSON, String> inserted = coll.insert(author);
				logger.debug(inserted.getSavedId());
			}
			//FIXME falta actualizar la relación contraria (auth2 es principal auth1 no)
		}
	}

	private AuthorBSON updateColleagues(JacksonDBCollection<AuthorBSON, String> coll, 
									AuthorBSON object, List<String> authors) {
		if (authors.size() > 1){
			// para cada coautor ver si está en la lista
			List<Colleague> list = object.getColl();
			for (String a: authors){
				for (Colleague c: list){
					if (c.getName().equals(a)){
						int n = c.getNum().intValue();
						n++;
						c.setNum(n);
					}
				}								
			}
		}
		return object;
	}

	private AuthorBSON addColleagues(AuthorBSON object, List<String> authors) {
		if (authors.size() > 1)
		{
			List<Colleague> colleagues = new ArrayList<Colleague>();
			for (int i=1; i<authors.size(); i++){
				Colleague c = new Colleague();
				c.setName(authors.get(i));
				c.setNum(1);
				colleagues.add(c);
			}
			object.setColl(colleagues);
		}
		else {
			logger.debug("No co-authors");
		}
		return object;
	}

	public AuthorBSON getByName(String s) {
		JacksonDBCollection<AuthorBSON, String> coll = JacksonDBCollection.wrap(dbCollection, AuthorBSON.class,
				String.class);		
		
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("name", s);
		DBCursor<AuthorBSON> result = coll.find(whereQuery);
		return result.next();
	}
}
