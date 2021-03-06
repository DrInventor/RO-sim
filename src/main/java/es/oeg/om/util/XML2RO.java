package es.oeg.om.util;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

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
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;

import es.oeg.om.util.exceptions.BreakParsingException;

/**
 * Parser the XML files produced by PDFX (@see http://pdfx.cs.man.ac.uk/) and produces an RO in RDF
 * with the aggregated resources (i.e. the pdf and the file with the sdo annotations)
 * 
 * @author Almudena Ruiz-Iniesta almudenari@fi.upm.es
 *
 */
public class XML2RO extends DefaultHandler{

	private static final String SRC_TEST_RESOURCES_DATA_RO = "src/test/resources/data/ro-";


	Logger logger = LoggerFactory.getLogger(this.getClass());


	private static final String meta = "meta";
	private static final String article = "article";
	private static final String front = "front";
	private static final String titleGroup = "title-group";
	private static final String articleTitle = "article-title";
	private static final String contribGroup = "contrib-group";
	private static final String contrib = "contrib";
	private static final String name = "name";


	public static final String URITitle = "http://rohub.linkeddata.es/drinventor/";

	public XML2RO() {
		parserStack = new Stack<Receiver>();
		
	}

	// resultados
	private String fileName;
	private String doiPaper;
	private String titlePaper;
	private List<String> authors = new ArrayList<String>();

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

	public String getFileName() {

		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Atributos
	 */
	private javax.xml.parsers.SAXParser saxParser;
	private String xmlFilename;	
	private Stack<Receiver> parserStack;
	private String contenido;



	public void startElement(String uri, String local, String name, Attributes attrs)  throws SAXException {
		// el elemento que est� en la cima de la pila
		contenido = new String(); // lo vaciamos para coger todo lo de este elemento
		logger.debug("Entramos en el elemento: "+name);
		logger.debug("estado de la pila "+parserStack.toString());
		Receiver candidate = parserStack.peek();
		Receiver followUp = null;	

		if (name.equals("doi") ||
				name.equals("s") || name.equals(XML2RO.name) || name.equals(articleTitle) || 
				name.equals(meta) || name.equals(article) || name.equals(front) || name.equals(titleGroup) ||
				name.equals(contribGroup) || name.equals(contrib) || name.equals("pdfx") ){
			followUp = candidate.processData(name, attrs);			
		}
		// el elemento siguiente		
		if (followUp!=null) {
			parserStack.push(followUp);
		}
		
		logger.debug("estado de la pila "+parserStack.toString());

	}

	public void endElement(String uri, String local, String name) throws SAXException {
		logger.debug("EStamos en end elemento: "+uri+" - "+local+" - "+name);
		if (name.equals("doi") ||
				name.equals("s") || name.equals("name") || name.equals(articleTitle) || 
				name.equals(meta) || name.equals(article) || name.equals(front) || name.equals(titleGroup) ||
				name.equals(contribGroup) || name.equals(contrib) || name.equals("pdfx") ){

			Receiver top = parserStack.pop();
			top.finishProcessData(name);
			logger.debug("se elimina de la pila. resultado de la pila: "+parserStack.toString());			
		}
		if (name.equals(contribGroup))
			throw new BreakParsingException("We have reached the final of "+name);


	} 

	/* 
	 * Esta funcion es llamada cuando ve el contenido de una etiqueta      
	 */
	public void characters(char buf[], int offset, int len) throws SAXException{
		contenido = contenido+new String(buf, offset, len);
		logger.debug("contenido de la etiqueta "+contenido);
	}

	// nos pasan el path completo
	public boolean init(String xmlFile){
		
		int index = xmlFile.indexOf(".xml");
		fileName = xmlFile.substring(0, index);
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
			authors = new ArrayList<String>();

			parserStack.push(new StartReceiver());
			InputStream ficEntrada=null;
			if ((ficEntrada=new FileInputStream(xmlFilename)) != null)
				saxParser.parse( ficEntrada, this );

		} catch (SAXException e) {
			if (e instanceof BreakParsingException) {
				// we have broken the parsing process
				logger.debug("End contrib-group");
				createROModel(doiPaper,titlePaper,authors);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createROModel(String doiPaper2, String titlePaper2,
			List<String> authors2) {

		// create an empty Model
		model = ModelFactory.createDefaultModel();

		// create the prefix for the most used properties
		model.setNsPrefix("rdf", rdf);
		model.setNsPrefix("ro", ro);
		model.setNsPrefix("ore", ore);
		model.setNsPrefix("dc", DCTerms.getURI());

		// create the most used properties
		a = model.createProperty(rdf+"type");
		roResearchObject = model.createProperty(ro+"ResearchObject");
		oreAggregation = model.createProperty(ore+"Aggregation");

		// now add the information

		paper = model.createResource(doiPaper2);
		paper.addProperty(a, roResearchObject);
		paper.addProperty(a, oreAggregation);

		
		paper.addProperty(DCTerms.title, titlePaper2);
		Property oreAggregates = model.createProperty(ore+"aggregates");
		

		Property roResource = model.createProperty(ro+"Resource");
		
		String resourcePDF = URITitle +safeNamefromString(titlePaper2)+".pdf";		
		Resource pdf = model.createResource(resourcePDF).addProperty(a, roResource);
		
		/* Authors of the PDF not the RO
		 * dc:creator [ a dc:Agent ;						
						foaf:name "Joe"^^http://www.w3.org/2001/XMLSchema#string
						] ;
		 */
		for (String author: authors2){			
			Resource authorR = model.createResource();
			authorR.addProperty(RDF.type, DCTerms.Agent);
			authorR.addLiteral(FOAF.name, author);			
			pdf.addProperty(DCTerms.creator, authorR);
		}
		
		paper.addProperty(oreAggregates, pdf);
		

		String resourceSDO = URITitle +safeNamefromString(titlePaper2)+"-sdo.ttl";
		Resource sdo = model.createResource(resourceSDO).addProperty(a, roResource);
		paper.addProperty(oreAggregates, sdo);

	}
	
	private void modelToFile() {
		FileWriter out = null;
		try {
			// OR Turtle format - compact and more readable
			String newFileName = fileName.replaceAll("[^a-zA-Z0-9]", "");
			out = new FileWriter( SRC_TEST_RESOURCES_DATA_RO+newFileName+".ttl");
			model.write(out, "TURTLE" );
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		finally {
			if (out != null) {
				try {out.close();} catch (IOException ignore) {}
			}
		}
	}

	public void end() {
		logger.debug("Summary: ");
		logger.debug("doi: "+doiPaper);
		logger.debug("tittle: "+titlePaper);
		logger.debug("authors: "+authors.toString());
		modelToFile();
	}

	private abstract class Receiver {		

		abstract Receiver processData(String name, Attributes attrs);

		abstract void finishProcessData(String name);
	}

	private class StartReceiver extends Receiver{

		@Override
		Receiver processData(String name, Attributes attrs) {
			// � se hace algo con el documento ?
			logger.debug("Tag :"+name);
			return new MetaReceiver();
		}

		@Override
		void finishProcessData(String name) {
			logger.debug("Tag's end:"+name);
			contenido = new String();
		}

	}

	private class MetaReceiver extends Receiver{

		@Override
		Receiver processData(String name, Attributes attrs) {
			logger.debug("Tag :"+name);
			return new DoiReceiver();
		}

		@Override
		void finishProcessData(String name) {
			logger.debug("End of tag :"+name);
			logger.debug("added article receiver");
			parserStack.push(new ArticleReceiver());
		}

	}

	private class DoiReceiver extends Receiver{

		@Override
		Receiver processData(String name, Attributes attrs) {
			logger.debug("start processing doi");
			return null;
		}

		@Override
		void finishProcessData(String name) {
			logger.info("doi: "+contenido);
			doiPaper = contenido;						
			// si aqu� lo hemos usado habr�a que vaciarlo
			contenido = new String();
			logger.debug(this.getClass().getCanonicalName()+" status "+parserStack.toString());
		}

	}

	private class ArticleReceiver extends Receiver{

		@Override
		Receiver processData(String name, Attributes attrs) {
			logger.debug("Tag :"+name);
			return new FrontReceiver();
		}

		@Override
		void finishProcessData(String name) {
			logger.debug("Tag's end:"+name);
		}

	}

	private class FrontReceiver extends Receiver{

		@Override
		Receiver processData(String name, Attributes attrs) {
			logger.debug("Tag :"+name);
			return new TitleGroupReceiver();
		}

		@Override
		void finishProcessData(String name) {
			logger.debug("Tag's end:"+name);
		}}

	private class TitleGroupReceiver extends Receiver{

		@Override
		Receiver processData(String name, Attributes attrs) {
			logger.debug("Etiqueta :"+name);

			return new ArticleTittleReceiver();
		}

		@Override
		void finishProcessData(String name) {
			logger.debug("Tag's end:"+name);
			parserStack.push(new ContribGroupReceiver());
			logger.debug("estado de la pila "+parserStack.toString());
		}}

	private class ArticleTittleReceiver extends Receiver {

		@Override
		Receiver processData(String name, Attributes attrs) {
			logger.debug("Etiqueta :"+name);
			return new SentenceReceiver();
		}

		@Override
		void finishProcessData(String name) {

			contenido = new String();
		}}

	private class ContribGroupReceiver extends Receiver{

		@Override
		Receiver processData(String name, Attributes attrs) {
			return new ContribReceiver();
		}

		@Override
		void finishProcessData(String name) {
			contenido = new String();
		}

	}

	private class ContribReceiver extends Receiver{

		@Override
		Receiver processData(String name, Attributes attrs) {
			return new NameReceiver();
		}

		@Override
		void finishProcessData(String name) {

		}

	}

	private class NameReceiver extends Receiver{

		@Override
		Receiver processData(String name, Attributes attrs) {
			logger.debug("estamos en name receiver proccess");
			return null;
		}

		@Override
		void finishProcessData(String name) {
			authors.add(new String(contenido));
			contenido = new String();
			logger.debug("fin de name receiver proccess");
		}

	}

	private class SentenceReceiver extends Receiver{

		@Override
		Receiver processData(String name, Attributes attrs) {
			return null;
		}

		@Override
		void finishProcessData(String name) {
			logger.debug("Tag's end S");
			logger.info("content: "+contenido);
			fileName =  contenido;
			titlePaper = contenido;
		}

	}
	
	private String safeNamefromString(String name){
		
		if (name == null || name.isEmpty()){
			throw new IllegalArgumentException("parameter must not be null or empty, actual vale: '" + name + "'");
		}		
		// 1. camel case
		String camelCased = "";
		String[] tokens = name.split("\\s");
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i];
            if (!token.isEmpty()){ // blank space are ignored
                camelCased = camelCased + token.substring(0, 1).toUpperCase()
                        + token.substring(1, token.length());
            }

		}
		// 2. imtentamos limpiar simbolos
		// eliminamos acentos y otras formas de representar: 
		// http://stackoverflow.com/questions/1008802/converting-symbols-accent-letters-to-english-alphabet
		String nfdNormalizedString = Normalizer.normalize(camelCased,
				Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		String result = pattern.matcher(nfdNormalizedString).replaceAll("");
		// 3. eliminamos todos lo que no sea ni letras ni numeros.
		return result.replaceAll("[^a-zA-Z0-9]", "");
	}

}
