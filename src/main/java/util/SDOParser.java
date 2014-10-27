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
import com.hp.hpl.jena.vocabulary.DCTerms;


public class SDOParser extends DefaultHandler {

	/**
	 * Interfaz para implementar una pila de callbacks en SAX
	 * Extraido de http://www.gamasutra.com/view/feature/2678/efficient_xml_file_reading_for_.php
	 * @author almudena ruiz-iniesta (almudenaris@gmail.com)
	 *
	 */	
	Logger logger = LoggerFactory.getLogger(this.getClass());



	private abstract class Receiver {		

		abstract Receiver processData(String name, Attributes attrs);

		abstract void finishProcessData(String name);
	}

	private class SDOReceiver extends Receiver{

		@Override
		Receiver processData(String name, Attributes attrs) {
			return new SDOReceiver();
		}

		@Override
		void finishProcessData(String name) {
			// hacemos el procesamiento al final ya que será cuando tengamos el contenido de la etiqueta
			String label = null;
			//FIXME esto no se corresponde con SDO por las anotaciones que nos han pasado
			if (name.equals(DRI_Approach)){
				label = "Motivation";
			}
			else if (name.equals(DRI_Background)){
				label = "Background";
			}
			else if (name.equals(DRI_Challenge)){
				label = "Contribution";
			}
			else if (name.equals(DRI_Challenge_Goal)){
				label = "Goal";
			}
			else if (name.equals(DRI_Challenge_Hypothesis)){
				label = "Hypothesis";
			}
			else if (name.equals(DRI_FutureWork)){
				label = "FutureWork";	
			}
			else if (name.equals(DRI_Outcome)){
				label = "Motivation";
			}
			else if (name.equals(DRI_Outcome_Contribution)){
				label = "Contribution";
			}
			else if (name.equals(DRI_Unspecified)){
				label = "Thing";
			}			
			else 
			{
				label = "Thing";
			}
			Property sdoP = model.createProperty(sdo+label);			
			// create the resource
			Resource sentence = model.createResource(":sentence"+contador);			
			sentence.addProperty(sdoP, contenido);
			contador++;
			contenido = new String();
		}

	}

	private class StartReceiver extends Receiver{

		public void finishProcessData(String name) {

		}

		public Receiver processData(String name, Attributes attrs) {			

			logger.info("Empezamos a procesar el documento");

			logger.info(this.toString()+" name: "+name+" attributes: "+attrs.toString());
			for (int i = 0; i< attrs.getLength(); i++){
				logger.info("Atributo: "+attrs.getValue(i));
			}
			String nameFile = attrs.getValue("name");
			logger.info("Nombre del documento: "+nameFile);
			fileName = attrs.getValue("name")+".ttl";

			// create an empty Model
			model = ModelFactory.createDefaultModel();

			a = model.createProperty(rdf+"type");
			model.setNsPrefix("rdf", rdf);

			biboArticle = model.createProperty(bibo+"Article");
			model.setNsPrefix("bibo", bibo);

			Property sdoDiscourse = model.createProperty(sdo+"ScientificDiscourse");
			model.setNsPrefix("sdo", sdo);

			// create the paper
			Resource fileResource = model.createResource(":paper");			
			fileResource.addProperty(a, biboArticle);
			fileResource.addProperty(a, sdoDiscourse);	

			model.write(System.out, "TURTLE");

			return new ArticleReceiver();
		}		
	}	


	private class ArticleReceiver extends Receiver {

		@Override
		Receiver processData(String name, Attributes attrs) {
			// al comienzo no es necesario hacer nada
			logger.info("Estamos en article receiver");
			logger.info(this.toString()+" name: "+name+" attributes: "+attrs.toString());	
			return null;
		}

		@Override
		void finishProcessData(String name) {
			// create the title
			String nameFile = contenido;
			Resource titleResource = model.createResource(":title");			
			Property docoTitle = model.createProperty(doco+"Title");
			model.setNsPrefix("doco", doco);
			titleResource.addProperty(a, docoTitle);
			titleResource.addProperty(DCTerms.description,nameFile);
			model.write(System.out, "TURTLE");
			// añadimos a la pila un SDO
			parserStack.push(new SDOReceiver());
		}

	}	


	/**
	 * Atributos
	 */
	private javax.xml.parsers.SAXParser saxParser;
	private String xmlFilename;	
	private Stack<Receiver> parserStack;
	private Model model;

	public SDOParser() {
		parserStack = new Stack<Receiver>();
	}

	public void startElement(String uri, String local, String name, Attributes attrs)  throws SAXException {
		// el elemento que está en la cima de la pila
		Receiver candidate = parserStack.peek();
		Receiver followUp = null;	

		if (name.equals(title))
			followUp = ((ArticleReceiver)candidate).processData(name, attrs);
		else if (name.equals(DRI_Approach) || name.equals(DRI_Background) || name.equals(DRI_Challenge) ||
				name.equals(DRI_Challenge_Goal) || name.equals(DRI_Challenge_Hypothesis) || 
				name.equals(DRI_FutureWork) || name.equals(DRI_Outcome) || name.equals(DRI_Outcome_Contribution) ||
				name.equals(DRI_Unspecified) || name.equals(Sentence))
			followUp = ((SDOReceiver)candidate).processData(name, attrs);		
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
		// necessary to link together the whole sentence 
		//(if there are some \n or . the function brokes up in several characters)
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
			parserStack.push(new StartReceiver());
			InputStream ficEntrada=null;
			if ((ficEntrada=new FileInputStream(xmlFilename)) != null)
				saxParser.parse( ficEntrada, this );

		} catch (SAXException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	public void end() {
		// Si hay que hacer algo al finalizar
	}


	// EJEMPLO DE USO
	public static void main(String[] args) {
		SDOParser p = new SDOParser();
		if (p.init("src/test/resources/data/A31_C01_A_Data-driven_Approach_for_Real-Time_Clothes_Simulation__CORPUS__v3.xml")){
			p.parse();
		}
		p.end();
		FileWriter out = null;
		try {
		   // OR Turtle format - compact and more readable
		  // use this variant if you're not sure which to use!
		  out = new FileWriter("src/test/resources/data/"+ p.getFileName());
		  p.model.write( out, "Turtle" );
			p.logger.debug("total number of sentences: "+p.contador);
		} catch (IOException e) {
			p.logger.error(e.getMessage());
		}
		finally {
		  if (out != null) {
		    try {out.close();} catch (IOException ignore) {}
		  }
		}

	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	// fields for SDO annotation
	private final String DRI_Background ="DRI_Background";
	private final String DRI_Challenge ="DRI_Challenge";
	private final String DRI_Challenge_Goal ="DRI_Challenge_Goal";
	private final String DRI_Outcome ="DRI_Outcome";
	private final String DRI_Outcome_Contribution ="DRI_Outcome_Contribution";
	private final String DRI_Approach ="DRI_Approach";
	private final String DRI_Unspecified ="DRI_Unspecified";
	private final String DRI_FutureWork ="DRI_FutureWork";
	private final String DRI_Challenge_Hypothesis ="DRI_Challenge_Hypothesis";
	private final String Sentence ="Sentence";
	private final String title = "article-title";
	//	private final String document ="Document";

	// prefix for ontology
	private final String doco = "http://purl.org/spar/doco/";
	private final String deo = "http://purl.org/spar/deo/";
	private final String bibo = "http://purl.org/ontology/bibo/";
	private final String sdo = "http://purl.org/drinventor/sci-doc";
	private String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	protected Property a;
	protected Property biboArticle;
	private String contenido;	
	private int contador = 0;
	
	private String fileName;

}
