package RO;

import java.net.UnknownHostException;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoClientTest {

	// CHECKSTYLE:OFF
	/**
	 * Run this main method to see the output of this quick example.
	 *
	 * @param args takes no args
	 * @throws UnknownHostException if it cannot connect to a MongoDB instance at localhost:27017
	 */
	public static void main(final String[] args) throws UnknownHostException {
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

