package util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParserTest {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());


	public static final String FILE_PATH = "src/test/resources/data/";
	
	private List<File> list = new ArrayList<File>();


	// all the files to be indexed are in @directory
	private List<File> indexAllFilesInDirectory(Path directory){

		if(directory== null) 
			return null;

		File file = directory.toFile();
		if (!file.exists()) {
			System.out.println(directory + " does not exist.");
		}
		else 
			if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					indexAllFilesInDirectory(f.toPath());
				}
			} else {
				String filename = file.getName().toLowerCase();		      
				// Only index xml files produced by PDFX		      
				if (filename.endsWith(".xml") && !filename.endsWith("v3.xml")) {
					list.add(file);
				} else {
					System.out.println("Skipped " + filename);
				}
			}
		return list;
	}
	
	@Test
	public void listAllTheFile(){
		Path pathDirectory = Paths.get(FILE_PATH);
		indexAllFilesInDirectory(pathDirectory);
		logger.info(list.toString());
		
		XML2RO p = new XML2RO();
			for (File file: list){
				String name = file.getAbsolutePath();
				logger.debug("Comenzamos a procesar el fichero: "+name);
				try{
					if (p.init(name))
						p.parse();
				}
				finally {
					//TODO a�adir el resto de recursos agregados haciendo un
					// p.addAgregatedResource y pasarle la lissta de documentos
					p.end();
					logger.debug("Fichero creado");
				}
			}		
	}

}
