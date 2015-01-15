package es.oeg.ro.dao;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.oeg.ro.transfer.Author;

// see -> http://neo4j.com/docs/stable/tutorials-java-embedded-new-index.html
// see also -> 
// pasar a Singleton para que pueda hacer test tranquilamente
public class DAOAuthorsNeo4jImp extends DAOAuthorsNeo4j{

	private static final String PUBLICATIONS = "publications";

	Logger logger = LoggerFactory.getLogger(this.getClass());	

	private static final String MATRIX_DB = "target/matrix-new-db";

	/** The Property key to use for names of persons */
	public static final String AUTHOR_NAME = "name";

	public enum RelTypes implements RelationshipType{
		CO_AUTHOR    
	}

	private IndexDefinition indexDefinition;
	private GraphDatabaseService graphDb;

	public DAOAuthorsNeo4jImp(){
		
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( MATRIX_DB );
	}
	
	public void init(){
		registerShutdownHook();
		// indexamos por nombre de autor
		try ( Transaction tx = graphDb.beginTx() )
		{
		    Schema schema = graphDb.schema();
		    indexDefinition = schema.indexFor( DynamicLabel.label( "Author" ) )
		            .on( "username" )
		            .create();
		    tx.success();
		}
	}	

	public void end(){
		logger.info( "Shutting down database ..." );
		graphDb.shutdown();

	}


	private void registerShutdownHook(){
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running example before it's completed)
		Runtime.getRuntime().addShutdownHook( new Thread()	{
			@Override
			public void run()
			{
				graphDb.shutdown();
			}
		} );
	}

	public void addAuthors(List<String> get_author) {

		try ( Transaction tx = graphDb.beginTx() ){
			List<Node> authors = new ArrayList<>();			
			for (String s: get_author){
				// create the nodes
				authors.add(createNode(s));
				logger.info("User created succesfuly");
			}
			// TODO pasar a recursivo
//			Node primer = authors.remove(0);			
			for(int i=0; i<authors.size(); i++)
				for (int j= i; j<authors.size(); j++){
					if (j != i) 
						makeFriends(authors.get(i),authors.get(j));					
				}
						
			tx.success();
			tx.close();
		}
		
	}

	//FIXME update to the new version
	private boolean areCoauthors(Node node1, Node node2){
//		PathFinder<Path> finder = GraphAlgoFactory.shortestPath(
//		        Traversal.expanderForTypes( RelTypes.CO_AUTHOR ), 1 );
//		Iterable<Path> paths = finder.findAllPaths( node1, node2 );
		

		Evaluation evaluationIfMatch = Evaluation.INCLUDE_AND_CONTINUE;
		
		Evaluation evaluationIfNoMatch = Evaluation.EXCLUDE_AND_CONTINUE;
		
		TraversalDescription td = graphDb.traversalDescription()
//				.depthFirst()
				.breadthFirst()
				.relationships( RelTypes.CO_AUTHOR )
				.evaluator( Evaluators.excludeStartPosition() )
				.evaluator(Evaluators.endNodeIs(evaluationIfMatch, evaluationIfNoMatch , node2))
				.evaluator(Evaluators.atDepth(1));		
		Traverser friendsTraverser = td.traverse( node1 );
		return friendsTraverser.iterator().hasNext();
	}
	
	private void makeFriends(Node node1, Node node2) {
		if (!areCoauthors(node1, node2)){			
			Relationship rel = node1.createRelationshipTo(node2, RelTypes.CO_AUTHOR);
			// number of coauthors
			rel.setProperty( PUBLICATIONS, 1 );
		}
		else{
			Iterable<Relationship> it = node1.getRelationships();
			for (Relationship rel: it){
				if (rel.getOtherNode(node1).equals(node2)){
					int num = (int) rel.getProperty(PUBLICATIONS);
					num++;
					rel.setProperty(PUBLICATIONS, num);
				}
			}
		}
		
	}


	//crear el nodo si no existe, sino recuperarlo
	public Node createNode(String s) {		
		
		Label label = DynamicLabel.label( "Author" );

		Node personNode = graphDb.findNode(label, AUTHOR_NAME, s);
		
		if (personNode == null){
			personNode = graphDb.createNode(label);
			personNode.setProperty(AUTHOR_NAME, s);			
			logger.debug("Node created: "+personNode.toString()+" with id: "+personNode.getId());			
		}		
		else
			logger.debug("Node retrieved: "+personNode+" with id: "+personNode.getId());
			
		return personNode;
	}
	
	// print the relations of co-authority

	public Node getNode(long id){
		return graphDb.getNodeById( id );
	}
	
	private Traverser getCoauthors(final Node person ){
		TraversalDescription td = graphDb.traversalDescription()
				.breadthFirst()
				.relationships( RelTypes.CO_AUTHOR )
				.evaluator( Evaluators.excludeStartPosition() );
		return td.traverse( person );
	}
	
	public void printCoauthority(){
		try ( Transaction tx = graphDb.beginTx() ){
			ResourceIterable<Node> it = GlobalGraphOperations.at(graphDb).getAllNodes();
			for (Node node : it){
				//				Node neoNode = getNode(id);
				int numberOfFriends = 0;
				logger.info( node.getProperty(AUTHOR_NAME) + "'s co-authors");
				Traverser friendsTraverser = getCoauthors( node );
				for ( Path friendPath : friendsTraverser ){
					logger.info( "At depth " + friendPath.length() + " => "
							+ friendPath.endNode().getProperty(AUTHOR_NAME) + 
							" number of common publications: "+friendPath.lastRelationship().getProperty(PUBLICATIONS).toString()
							);
					numberOfFriends++;
				}
				logger.info("Number of friends found: " + numberOfFriends);
				tx.success();
			}
			tx.close();
		}
	}
	
	public String printFriends(String author){
		if (author == null)
			return null;
		String s = "";
		try ( Transaction tx = graphDb.beginTx() ){
			Label label = DynamicLabel.label( "Author" );
			Node personNode = graphDb.findNode(label, AUTHOR_NAME, author);
			int numberOfFriends = 0;
			logger.info( personNode.getProperty(AUTHOR_NAME) + "'s co-authors");
			Traverser friendsTraverser = getCoauthors( personNode );
			for ( Path friendPath : friendsTraverser ){
				s+= "At depth " + friendPath.length() + " => "
						+ friendPath.endNode().getProperty(AUTHOR_NAME) + 
						" number of common publications: "+friendPath.lastRelationship().getProperty(PUBLICATIONS).toString()
						;
				numberOfFriends++;
				s+="\n";
			}
			s+=("Number of friends found: " + numberOfFriends);
			s+="\n";
			tx.success();
			tx.close();
		}
		return s;
	}

	public Author findAuthor(String name) {
		if (name == null)
			return null;

		try ( Transaction tx = graphDb.beginTx() ){
			Label label = DynamicLabel.label( "Author" );

			Node personNode = graphDb.findNode(label, AUTHOR_NAME, name);

			Author author = new Author((String) personNode.getProperty(AUTHOR_NAME));
			author.setId(Long.toString(personNode.getId()));
			tx.success();
			tx.close();		
			return author;
		}

	}

	/**
	 * search in the graph the author1 and then return the sum of all the publications shared with other authors
	 * @param author1
	 * @return
	 */
	public double numberOfTotalSharedPublications(String author1) {
		try ( Transaction tx = graphDb.beginTx() ){
			Label label = DynamicLabel.label( "Author" );
			Node nodeAuthor = graphDb.findNode(label, AUTHOR_NAME, author1);
			if (nodeAuthor != null)
			{
				logger.debug("Author : "+nodeAuthor.getId()+ " name: "+nodeAuthor.getProperty(AUTHOR_NAME));

				//TODO para futuro: mejorar con traverser
				Iterable<Relationship> it = nodeAuthor.getRelationships(RelTypes.CO_AUTHOR);
				int numPub = 0;
				for (Relationship rel: it){
					logger.debug("Relation: "+rel.getId()+" end Node: "+rel.getEndNode().getId()+
							" publications: "+rel.getProperty(PUBLICATIONS));
					numPub += (int) rel.getProperty(PUBLICATIONS);
				}
				tx.success();
				return numPub;
			}
			tx.success();	
			logger.debug("Author not found");
			return 0;
		}
	}

	public double sharedPublications(String author1, String author2, int depth) {
		/*
		 * look into a traversal/shortest-path starting at n1 and ending at n2 with a max_depth of 1.
		 */
		
		try ( Transaction tx = graphDb.beginTx() ){
			Label label = DynamicLabel.label( "Author" );

			Node nodeAuthor = graphDb.findNode(label, AUTHOR_NAME, author1);
			Node nodeAuthor2 = graphDb.findNode(label, AUTHOR_NAME, author2);
			
			if (nodeAuthor.equals(nodeAuthor2)){
				logger.info("The two authors are the same: "+author1+" and "+author2);
			}
			
			Evaluation evaluationIfMatch = Evaluation.INCLUDE_AND_CONTINUE;
			
			Evaluation evaluationIfNoMatch = Evaluation.EXCLUDE_AND_CONTINUE;
			
			TraversalDescription td = graphDb.traversalDescription()
					.breadthFirst()
					.relationships( RelTypes.CO_AUTHOR )
					.evaluator( Evaluators.excludeStartPosition() )
					.evaluator(Evaluators.endNodeIs(evaluationIfMatch, evaluationIfNoMatch , nodeAuthor2))
					;
//					.evaluator(Evaluators.atDepth(depth));
			
			
			Traverser friendsTraverser = td.traverse( nodeAuthor );
			
			for ( Path friendPath : friendsTraverser ){
				logger.info( "At depth " + friendPath.length() + " => "
						+ friendPath.endNode().getProperty(AUTHOR_NAME) + 
						" number of common publications: "+friendPath.lastRelationship().getProperty(PUBLICATIONS).toString()
						);
				return (int) friendPath.lastRelationship().getProperty(PUBLICATIONS);
			}
			// fin de traverse
//			for (Relationship neighbor : nodeAuthor.getRelationships(Direction.OUTGOING, RelTypes.CO_AUTHOR)) {
//				if (neighbor.getEndNode().equals(nodeAuthor2))
//					return (int) neighbor.getProperty(PUBLICATIONS);
//			}
			tx.success();		
		}
		catch (NullPointerException nullAuthor){
			logger.debug("Author not found");						
			return 0;
		}
		return 0;
	}

}
