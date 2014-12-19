package RO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;

import es.oeg.ro.ROManager;

public class ROManagerTest {

	public static final String FILE_PATH = "src/test/resources/data/ro/";
	Logger logger = LoggerFactory.getLogger(this.getClass());

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
				if (filename.endsWith(".ttl")) {
					list.add(file);
				} else {
					System.out.println("Skipped " + filename);
				}
			}
		return list;
	}

	@Test
	public void getAuthorsInformation(){
		Path pathDirectory = Paths.get(FILE_PATH);
		indexAllFilesInDirectory(pathDirectory);
		logger.info(list.toString());
		ROManager manager = new ROManager();

		for (File f:list){
			// load the model
			Model model1 = RDFDataMgr.loadModel(f.getPath() ,Lang.TURTLE) ;
			manager.updatesAuthorInformation(model1);			
		}
		manager.writeResultsToFileJSON();
	}

	private static String checkBlanks( String str ){
		String s = str.replaceAll("\\s+",""); //str.trim();
		return s;
	}

	@Test
	public void cleanTTL() throws IOException{
		Path pathDirectory = Paths.get(FILE_PATH);
		indexAllFilesInDirectory(pathDirectory);
		for (File f:list){
			// para cada fichero leemos las lineas si es <> entonces eliminamos blancos			
//			String title = Files.lines( f.toPath() ).filter( pathName -> checkEmpty( pathName ) ).findFirst().get();
//			logger.debug("Línea que sí empieza por < :"+title);			


			try {
				// input the file content to the String "input"
				BufferedReader file = new BufferedReader(new FileReader(f));
				String line;String input = "";

				while ((line = file.readLine()) != null){
					if (checkEmpty(line))
						line = checkBlanks(line);
					input += line + '\n';
				}				
				file.close();
				
				logger.debug(input); // check that it's inputted right

				// write the new String with the replaced line OVER the same file
				FileOutputStream fileToWrite = new FileOutputStream(f);
				fileToWrite.write(input.getBytes());
				fileToWrite.close();

			} catch (Exception e) {
				logger.error("Problem reading file."+ e.getMessage());
			}
		}

	}

	private boolean checkEmpty(String str) {		
		return str.startsWith("<");
	}	

}
