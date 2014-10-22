package util;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class XML2RO extends DefaultHandler{
	
	Logger logger = LoggerFactory.getLogger(this.getClass());


	private static final String meta = "meta";
	private static final String article = "article";
	private static final String front = "front";
	private static final String titleGroup = "title-group";
	private static final String articleTitle = "article-title";
	private static final String contribGroup = "contrib-group";
	private static final String contrib = "contrib";
	private static final String name = "name";
	
	public XML2RO() {
		parserStack = new Stack<Receiver>();
	}

	private String fileName;

	private final String ore = "http://www.openarchives.org/ore/terms/";
	private final String prov = "http://www.w3.org/ns/prov#";
	private final String ro = "http://purl.org/wf4ever/ro#";
	private final String schema = "http://schema.org/";
	private final String xhv = "http://www.w3.org/1999/xhtml/vocab#";
	private final String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

	
	private Model model;
	private Property a;
	private Property roResearchObject;
	private Property oreAggregation;
	private Resource paper;
	
	public void createModel(){
		// create an empty Model
		model = ModelFactory.createDefaultModel();

		// create the prefix for the most used properties
		model.setNsPrefix("rdf", rdf);
		model.setNsPrefix("ro", ro);
		model.setNsPrefix("ore", ore);
		
		// create the most used properties
		a = model.createProperty(rdf+"type");
		roResearchObject = model.createProperty(ro+"ResearchObject");
		oreAggregation = model.createProperty(ore+"Aggregation");		
	}
	
	public String getFileName() {
		
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/*
	 * procesamos el fichero original:
	 * hay que coger:
	 * <http://purl.org/net/ro-motifPaper> a ro:ResearchObject,
	 * dc:creator <http://delicias.dia.fi.upm.es/members/DGarijo/#me>;
		dc:title "Common Motifs in Scientific Workflows: An Empirical Analysis"@en;
	schema:creator <http://delicias.dia.fi.upm.es/members/DGarijo/#me>;
	schema:name "Common Motifs in Scientific Workflows: An Empirical Analysis"@en;
	ore:aggregates --> e indicar todos los ficheros que lo acompañan
	para cada uno de los ficheros que acompañan hay que declararlo
	<> a ro:Resource .
	 */
	
	/**
	 * Atributos
	 */
	private javax.xml.parsers.SAXParser saxParser;
	private String xmlFilename;	
	private Stack<Receiver> parserStack;
	private String contenido;
	
	
	
	public void startElement(String uri, String local, String name, Attributes attrs)  throws SAXException {
		// el elemento que está en la cima de la pila
		Receiver candidate = parserStack.peek();
		Receiver followUp = null;	

		if (name.equals(articleTitle))
			followUp = ((ArticleTittleReceiver)candidate).processData(name, attrs);
			
		else if (name.equals("doi"))
			followUp = ((DoiReceiver)candidate).processData(name, attrs);
		else
			followUp = (candidate).processData(name, attrs);

		// el elemento siguiente		
		if (followUp!=null) {
			parserStack.push(followUp);
		}
		else
			parserStack.push(candidate);    	
	}

	public void endElement(String uri, String local, String name) throws SAXException {
		logger.info("EStamos en end elemento: "+uri+" - "+local+" - "+name);		
		Receiver top = parserStack.pop();
		top.finishProcessData(name);
	} 

	/* 
	 * Esta funcion es llamada cuando ve el contenido de una etiqueta      
	 */
	public void characters(char buf[], int offset, int len) throws SAXException{
		contenido = contenido+new String(buf, offset, len);
		logger.debug("contenido de la etiqueta "+contenido);
	}

	public boolean init(String xmlFile){
		boolean _ok = false;
		// Crear la fabrica utilizar para SAX 
		SAXParserFactory factory  = SAXParserFactory.newInstance();

		try { 
			saxParser = factory.newSAXParser();
			xmlFilename = xmlFile;
			parserStack.clear();
			// Todas las inicializaciones adicionales que hace nuestro parser
			_ok = true;

		} catch (ParserConfigurationException pexc) { 
			pexc.printStackTrace();
		} catch (SAXException saxex) { 
			saxex.printStackTrace();
		} 
		return _ok;
	}

	public void parse() {
		try {
			// StartReceiver implementa Receiver y es el responsable de 
			// la primera etiqueta que envuelve a todo el XML
			parserStack.push(new StartReceiver());
			InputStream ficEntrada=null;
			if ((ficEntrada=new FileInputStream(xmlFilename)) != null)
				saxParser.parse( ficEntrada, this );

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void end() {
		// Si hay que hacer algo al finalizar
	}

	private abstract class Receiver {		

		abstract Receiver processData(String name, Attributes attrs);

		abstract void finishProcessData(String name);
	}
	
	private class StartReceiver extends Receiver{

		@Override
		Receiver processData(String name, Attributes attrs) {
			return new MetaReceiver();
		}

		@Override
		void finishProcessData(String name) {
			
		}
		
	}
	
	private class MetaReceiver extends Receiver{

		@Override
		Receiver processData(String name, Attributes attrs) {
			// TODO Auto-generated method stub
			return new DoiReceiver();
		}

		@Override
		void finishProcessData(String name) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class DoiReceiver extends Receiver{

		@Override
		Receiver processData(String name, Attributes attrs) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		void finishProcessData(String name) {
			// FIXME revisar por qué ahora no lo coge!! get the doi
			logger.info("doi del artículo: "+contenido);
			paper = model.createResource(contenido);
			paper.addProperty(a, roResearchObject);
			paper.addProperty(a, oreAggregation);			
			// si aquí lo hemos usado habría que vaciarlo
			contenido = new String();
			// añadimos a la pila un SDO
			parserStack.push(new ArticleReceiver());
		}
		
	}
	
	private class ArticleReceiver extends Receiver{

		@Override
		Receiver processData(String name, Attributes attrs) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		void finishProcessData(String name) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class FrontReceiver extends Receiver{

		@Override
		Receiver processData(String name, Attributes attrs) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		void finishProcessData(String name) {
			// TODO Auto-generated method stub
			
		}}
	
	private class TitleGroupReceiver extends Receiver{

		@Override
		Receiver processData(String name, Attributes attrs) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		void finishProcessData(String name) {
			// TODO Auto-generated method stub
			
		}}
	
	private class ArticleTittleReceiver extends Receiver {

		@Override
		Receiver processData(String name, Attributes attrs) {
			// TODO coger el nombre del fichero
			return new SentenceReceiver();
		}

		@Override
		void finishProcessData(String name) {
			// TODO Auto-generated method stub
			
		}}
	
	private class ContribGroupReceiver extends Receiver{

		@Override
		Receiver processData(String name, Attributes attrs) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		void finishProcessData(String name) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class NameReceiver extends Receiver{

		@Override
		Receiver processData(String name, Attributes attrs) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		void finishProcessData(String name) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class SentenceReceiver extends Receiver{

		@Override
		Receiver processData(String name, Attributes attrs) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		void finishProcessData(String name) {
			// TODO coger los characters
			
		}
		
	}
	
	// EJEMPLO DE USO
		public static void main(String[] args) {
			XML2RO p = new XML2RO();
			if (p.init("src/test/resources/data/A Data-driven Approach for Real-Time Clothes Simulation.xml")){
				p.parse();
			}
			p.end();
			FileWriter out = null;
			try {
			   // OR Turtle format - compact and more readable
			  out = new FileWriter( p.getFileName());
			  p.model.write( out, "Turtle" );
			} catch (IOException e) {
				p.logger.error(e.getMessage());
			}
			finally {
			  if (out != null) {
			    try {out.close();} catch (IOException ignore) {}
			  }
			}

		}
}
