package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.base.file.FileFactory;
import com.hp.hpl.jena.util.FileManager;


public class CallbackSAXParser extends DefaultHandler {
	
	/**
	 * Interfaz para implementar una pila de callbacks en SAX
	 * Extraido de http://www.gamasutra.com/view/feature/2678/efficient_xml_file_reading_for_.php
	 * @author almudena ruiz-iniesta (almudenaris@gmail.com)
	 *
	 */	
	Logger logger = LoggerFactory.getLogger(this.getClass());

	
	Model model;
	
	private abstract class Receiver {		
		
		abstract Receiver processData(String name, Attributes attrs);

		abstract void finishProcessData(String name);
	}
	
	// TODO Receiver para SDO
	private class SDOReceiver extends Receiver{

		@Override
		Receiver processData(String name, Attributes attrs) {
			// TODO averiguar qu� concepto es....�es necesario? �o simplemente a�adimos statement?
			logger.info("Estamos en una nueva sentence anotada");
			logger.info(this.toString()+" name: "+name+" attributes: "+attrs.toString());
			return new SDOReceiver();
		}

		@Override
		void finishProcessData(String name) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	// Receiver del principio del documento
	// mi objetivo es construir un modelo RDF
	private class StartReceiver extends Receiver{

		public void finishProcessData(String name) {
			
		}
	
		public Receiver processData(String name, Attributes attrs) {			
			// creamos la lista de usuarios
			// TODO cogemos el nombre del documento
			logger.info("Empezamos a procesar el documento");
			logger.info(this.toString()+" name: "+name+" attributes: "+attrs.toString());
			for (int i = 0; i< attrs.getLength(); i++){
				logger.info("Atributo: "+attrs.getValue(i));
			}
//			model = ModelFactory.createDefaultModel();
//			Resource s;
//			Property p;
//			RDFNode o;
//			model.add(s, p, o);
			return new ArticleReceiver();
		}		
	}	
	
	
	private class ArticleReceiver extends Receiver {

		@Override
		Receiver processData(String name, Attributes attrs) {
			// TODO aqui podemos coger el nombre bonito del t�tulo del articulo
			logger.info("Estamos en article receiver");
			logger.info(this.toString()+" name: "+name+" attributes: "+attrs.toString());
			return null;
		}

		@Override
		void finishProcessData(String name) {
			// a�adimos a la pila un SDO
			parserStack.push(new SDOReceiver());
		}
		
	}
	
//	private class UserReceiver extends Receiver{
//
//		
//		public void finishProcessData(String name) {
//			// ya tenemos al usuario con su perfil entero generado
//			// lo a�adimos a la lista de usuarios
//			listaUsuarios.add(user);
//		}
//		
//		public Receiver processData(String name, Attributes attrs) {
//			// estamos procesando un nuevo usuario
//			user = new TransferUser();
//			user.setId(attrs.getValue(0));			
//	
//			return new LessonReceiver();
//		}		
//	}
	
//	private class LessonReceiver extends Receiver{		
//		
//		public void finishProcessData(String name) {
//			
//		}
//
//		public Receiver processData(String name, Attributes attrs) {			
//			// tengo que coger el nombre de la lecci�n para a�adirlo al perfil
//			String lessonName = attrs.getValue(0);			
//			lesson = new Lesson();
//			lesson.setLesson(lessonName);
//			user.addLesson(lesson);
//			// devolvemos un puntero a lo siguiente que vendr� que es un concepto
//			return new ConceptReceiver();
//		}		
//	}
	
//	private class ConceptReceiver extends Receiver{
//
//		void finishProcessData(String name) {			
//			
//		}
//
//		Receiver processData(String name, Attributes attrs) {
//			String nombreConcepto = attrs.getValue(0);
//			lesson.addConcepto(nombreConcepto);
//			String nota = attrs.getValue(1);
//			if (nota.isEmpty()) 
//				nota = "-1.0";			
//			lesson.addNota(nota);
//			return null;
//		}		
//	}
	
	/**
	* Atributos
	*/
	private javax.xml.parsers.SAXParser saxParser;
	private String xmlFilename;	
	private Stack<Receiver> parserStack;
	
	//atributo para guardar a todos los usuarios del sistema
//	private ArrayList<TransferUser> listaUsuarios;
	
	//atributo para guardar un usuario
//	private TransferUser user;
	//atributo para guardar una lecci�n con conceptos
//	private Lesson lesson;
	
	
	public CallbackSAXParser() {
		  parserStack = new Stack<Receiver>();
	}
	
	public void startElement(String uri, String local, String name, Attributes attrs)  throws SAXException {
    	// el elemento que est� en la cima de la pila
		Receiver candidate = parserStack.peek();
		Receiver followUp = null;	
		
		// FIXME cuando article acaba la pila se lia!! hay que hacer un receiver com�n para todo lo que hay dentro de document!!!
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
    	String contenido = new String(buf, offset, len);
    	System.out.println(contenido);
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
	
//	public ArrayList<TransferUser> getListaUsuarios(){
//		return listaUsuarios;
//	}
	
	// EJEMPLO DE USO
	public static void main(String[] args) {
		CallbackSAXParser p = new CallbackSAXParser();
		if (p.init("src/test/resources/data/A31_C01_A_Data-driven_Approach_for_Real-Time_Clothes_Simulation__CORPUS__v3.xml")){
			p.parse();
		}
		p.end();
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
	private final String document ="Document";

}