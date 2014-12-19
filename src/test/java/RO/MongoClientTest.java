package RO;

import java.net.UnknownHostException;
import java.util.Set;

import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class MongoClientTest {

	@Test
	public void basicTest() throws UnknownHostException {
		// connect to the local database server
		MongoClient mongoClient = new MongoClient();
		/*
		// Authenticate - optional
		MongoCredential credential = MongoCredential.createMongoCRCredential(userName, database, password);
		MongoClient mongoClient = new MongoClient(new ServerAddress(), Arrays.asList(credential));
		 */
		
		// get handle to "mydb"
		DB db = mongoClient.getDB("mydb");
		
		// get a list of the collections in this database and print them out
		Set<String> collectionNames = db.getCollectionNames();
		for (final String s : collectionNames) {
			System.out.println(s);
		}
		
		BasicDBObject doc = new BasicDBObject("name", "MongoDB")
        .append("type", "database")
        .append("count", 1)
        .append("info", new BasicDBObject("x", 203).append("y", 102));
		DBCollection coll = db.getCollection("mydb");
		coll.insert(doc);
		
	}
}

