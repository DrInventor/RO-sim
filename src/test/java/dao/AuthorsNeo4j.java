package dao;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.oeg.ro.dao.DAOAuthorsNeo4jImp;

public class AuthorsNeo4j {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());	


	private static final String DB_PATH = "target/matrix-new-db";


	DAOAuthorsNeo4jImp db;

	@Test
	public void newMatrix(){
		deleteFileOrDirectory( new File( DB_PATH ) );
		db = new DAOAuthorsNeo4jImp();
		db.init();
		List<String> get_author = new ArrayList<String>();
		get_author.add("Almudena"); get_author.add("Maria"); get_author.add("Esther");
		db.addAuthors(get_author );		
		// check if we can add the same author
		get_author.remove(2);
		db.addAuthors(get_author);
		db.printCoauthority();
		db.end();		
	}
	
	@Test
	public void deleteData()
	{
		deleteFileOrDirectory(new File(DB_PATH));
	}
	
	@Test
	public void printAllGraph(){
		db = new DAOAuthorsNeo4jImp();
		db.printCoauthority();
		db.end();	
	}
	
	@Test
	public void numberOfTotalSharedPublications(){
		db = new DAOAuthorsNeo4jImp();
		String[] author1 = {"da Costa, F. P.",
		 "da Costa, F. P.",
		 "Pinto, J. T.",
		 "Cavalcanti, M. M.",
		 "Bruguier, Olivier","Blömker, Dirk"};
		for (int i=0; i<author1.length; i++){
			double number = db.numberOfTotalSharedPublications(author1[i]);
			logger.info("Number of shared Publications: "+number);
		}
		db.end();
	}
	
	@Test
	public void testSharedPublications(){
		db = new DAOAuthorsNeo4jImp();
		String[] author1 = {"da Costa, F. P.",		
		 "Pinto, J. T.", "Sasportes, R.",
		 "Cavalcanti, M. M.",
		 "Bruguier, Olivier","Blömker, Dirk"};
		for (int i=0; i<author1.length-1; i++){
			logger.info("Authors: "+author1[i]+" "+ author1[i+1]);
			double number = db.sharedPublications(author1[i], author1[i+1],1);
			logger.info("Number of shared Publications: "+number);
		}
		db.end();
	}
	
	@Test
	public void sharedPublicationsWithDepth2(){
		db = new DAOAuthorsNeo4jImp();
		logger.info("Authors: Mu, Chunlai, Zhou, Shouming");
		double number = db.sharedPublications("Mu, Chunlai", "Zhou, Shouming",1);
		logger.info("Number of shared Publications: "+number);		
		assertTrue(number == 0);
		db.end();
	}
	
	@Test
	public void sharedPublicationsWithDepth1(){
		db = new DAOAuthorsNeo4jImp();
		logger.info("Authors: López-Val, D., Robens, T.");
		double number = db.sharedPublications("López-Val, D.", "Robens, T.",1);
		logger.info("Number of shared Publications: "+number);		
		assertTrue(number == 1);
		db.end();
	}
	
	@Test
	public void printAuthorsFriendsNullParameter(){
		db = new DAOAuthorsNeo4jImp();
		String s = db.printFriends(null);
		db.end();
		assertNull(s);		
	}
	
	@Test
	public void printAuthorsFriends(){
		db = new DAOAuthorsNeo4jImp();
//		String s = db.printFriends("Terashima, A.");
		String s2 = db.printFriends("Antoja, T.");
		db.end();
//		assertNotNull(s);
//		logger.info(s);
		logger.info(s2);
		
		
	}

	private static void deleteFileOrDirectory( File file ){
		if ( file.exists() ){
			if ( file.isDirectory() ){
				for ( File child : file.listFiles() ){
					deleteFileOrDirectory( child );
				}
			}
			file.delete();
		}
	}
}
