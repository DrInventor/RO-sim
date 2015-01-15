package es.oeg.ro.dao;

public class DAOAuthorsNeo4j {
	private static DAOAuthorsNeo4jImp ins;
	
	public static DAOAuthorsNeo4j getInstance(){
		if (ins == null)
			ins = new DAOAuthorsNeo4jImp();
		return ins;
	}
}
