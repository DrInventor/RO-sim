package es.oeg.om.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Locator {

	/**
	 * Load a properties from class loader
	 * @param resourceName String name of the resource (without extension .properties)
	 * @return Properties or <code>null</code> if the resource is unreachable.
	 */
	public static String getDevKey(String resourceName){		
		try{			
			Stream<String> lines = Files.lines(Paths.get(resourceName));
			List<String> line = new ArrayList<String>();
			lines.forEach(line::add);		
			lines.close();
			return line.get(0).toString();
		} catch (IOException e) {
			return null;
		}
		catch (NullPointerException e) {
			return null;
		}
	}


}
