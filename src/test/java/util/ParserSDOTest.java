package util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.oeg.om.util.SDOParser;

public class ParserSDOTest {

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
				// Only index xml files with annotations	      
				if (filename.endsWith(".xml") && filename.endsWith("v3.xml")) {
					list.add(file);
				} else {
					System.out.println("Skipped " + filename);
				}
			}
		return list;
	}

	@Test
	public void parseAllTheFiles(){
		Path pathDirectory = Paths.get(FILE_PATH);
		indexAllFilesInDirectory(pathDirectory);
		logger.info(list.toString());

		SDOParser p = new SDOParser();

		for (File file: list){
			String name = file.getAbsolutePath();
			logger.debug("Comenzamos a procesar el fichero: "+name);
			try{
				if (p.init(name))
					p.parse();
			}
			finally {
				p.end();
				logger.debug("Fichero creado");
			}
		}
	}

}
