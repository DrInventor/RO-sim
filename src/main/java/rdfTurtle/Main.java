package rdfTurtle;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class Main {

	public static void main(String args[]){
		// Create a model and read into it from file 
		// "data.ttl" assumed to be Turtle.
		
		Model model = RDFDataMgr.loadModel("src/test/resources/root.ttl",Lang.TURTLE) ;
		StmtIterator iter = model.listStatements();
		
		Map<String,String> map = model.getNsPrefixMap();

		System.out.println(map.toString());
		System.out.println("Lista de keys: "+map.keySet());
		
		Model modelData2 = RDFDataMgr.loadModel("src/test/resources/ro-sample.ttl",Lang.TURTLE) ;
		StmtIterator iterData2 = modelData2.listStatements();
		
		Main main = new Main();
		
		// intersección de sujetos
		Set<Resource> set1 = main.getAllSubjects(iter);
		System.out.println("Total number of subjects: "+ set1.size());
		System.out.println(" subjects: "+ set1.toString());
		Set<Resource> set2 = main.getAllSubjects(iterData2);		
		System.out.println("Total number of subjects: "+ set2.size());
		System.out.println(" subjects: "+ set2.toString());
		Set commonSubjects = main.intersection(set1, set2);
		System.out.println(commonSubjects.toString());
		
//		los predicados (propiedades) no son interesantes --> deberían compartirlos casi todos
//		iter = model.listStatements();
//		iterData2 = modelData2.listStatements();
//		Set<Resource> setPredicates1 = main.getAllPredicates(iter);
//		System.out.println("Total number of predicates: "+ set.size());
//		Set<Resource> setPredicates2 = main.getAllPredicates(iterData2);
//		System.out.println("Total number of predicates: "+ set.size());
//		Set commonPredicates = main.intersection(setPredicates1, setPredicates2);		
//		System.out.println(commonPredicates.toString());
//		
		// intersección de objetos
		iter = model.listStatements();
		iterData2 = modelData2.listStatements();
		Set<RDFNode> setObjects1 = main.getAllObjects(iter);
		System.out.println("Total number of objects: "+ setObjects1.size());
		System.out.println(" objects: "+ setObjects1.toString());
		Set<RDFNode> setObjects2 = main.getAllObjects(iterData2);
		System.out.println("Total number of objects: "+ setObjects2.size());
		System.out.println(" objects: "+ setObjects2.toString());
		Set commonObjects = main.intersection(setObjects1, setObjects2);		
		System.out.println(commonObjects.toString());
		
		Resource subject = ResourceFactory.createResource("http://mayor2.dia.fi.upm.es/oeg-upm/files/dgarijo/motifAnalysis/WfCatalogue-AdditionalVistrailsWfsWithDomainsRevisited.xlsx");
		Set statements = main.getStatementFrom(modelData2, subject);
		System.out.println(statements.toString());
		
//		
//		iter = model.listStatements();
//		iterData2 = modelData2.listStatements();
//		main.printAll(iter);
//		main.printAll(iterData2);
	}
	
	public String printAll(StmtIterator iter){
		String str = null;
		try {
			while ( iter.hasNext() ) {
				Statement stmt = iter.next();
				System.out.print(stmt);
				Resource s = stmt.getSubject();				
				if ( s.isURIResource() ) {
					System.out.print("URI");
				} else if ( s.isAnon() ) {
					// anonynous 
					System.out.print("blank");
				}
				Resource p = stmt.getPredicate();
				if ( p.isURIResource() )
					System.out.print(" URI ");
				
				RDFNode o = stmt.getObject();
				if ( o.isURIResource() ) {
					System.out.print("URI");
				} else if ( o.isAnon() ) {
					System.out.print("blank");
				} else if ( o.isLiteral() ) {
					System.out.print("literal");
				}
				System.out.println();
			}
		} finally {
			if ( iter != null ) iter.close();
		}
		return str;
	}
	
	public Set<Resource> getAllPredicates(StmtIterator modelIterator){
		Set<Resource> set = new HashSet<Resource>();
		while (modelIterator.hasNext()){
			Statement stmt = modelIterator.next();		
			set.add(stmt.getPredicate());
		}		
		return set;
	}
	
	public Set<Resource> getAllSubjects(StmtIterator modelIterator){
		Set<Resource> set = new HashSet<Resource>();
		while (modelIterator.hasNext()){
			Statement stmt = modelIterator.next();
			set.add(stmt.getSubject());
		}
		return set;
	}
	
	public Set<RDFNode> getAllObjects(StmtIterator modelIterator){
		Set<RDFNode> set = new HashSet<RDFNode>();
		while (modelIterator.hasNext()){
			Statement stmt = modelIterator.next();
			set.add(stmt.getObject());
		}
		return set;
	}
	
	@SuppressWarnings("unchecked")
	public Set<Object> intersection(Set set1, Set set2){
		Set<Object> a;
		Set<Object> b,common = new HashSet<Object>();
		
		if (set1.size() <= set2.size()) {
            a = set1;
            b = set2;           
        } else {
            a = set2;
            b = set1;
        }
        int count = 0;
        for (Object e : a) {
            if (b.contains(e)) {
            	common.add(e);
                count++;
            }           
        }
        System.out.println("Total number of common: "+ count);
        return common;
	}
	
	// TODO devuelve un statement a partir del sujeto
	//TODO refactorizar para que sea genérico el método y devuelva los statements a partir del elemento  que se pase
	public Set<Statement> getStatementFrom(Model model, Resource subject){
		Set<Statement> set = new HashSet<Statement>();
		StmtIterator iter = model.listStatements(subject, (Property)null, (RDFNode)null);
		while (iter.hasNext()){
			// match the subjects
			Statement st = iter.next();
			if (st.getSubject().equals(subject)){
				System.out.println("SÍ! Sujetos iguales: "+"origen: "+subject + 
						" destino: "+st.getSubject().toString());
				System.out.println("statement: "+st.asTriple().toString());
				set.add(st);
			}
			else{
				System.out.println("NO! Sujetos distintos: "+"origen: "+subject + " destino: "+st.getSubject().toString());
				
			}
		}
		return set;
	}

}
