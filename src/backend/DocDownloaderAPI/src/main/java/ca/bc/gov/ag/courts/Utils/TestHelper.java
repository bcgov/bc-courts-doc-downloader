package ca.bc.gov.ag.courts.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.util.ResourceUtils;

/**
 * 
 * Test utilities. 
 * 
 * @author 176899
 *
 */
public class TestHelper {

	/**
	 * 
	 * Fetch resource as File
	 * 
	 * @param fileName
	 * @return
	 */
	public static File fetchFileResource(String fileName) {
		File file = null;
		try {
			file = ResourceUtils.getFile("classpath:" + fileName);
			return file;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	/**
	 * 
	 * Fetch resource as bytes. 
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static byte[] fetchFileResourceAsBytes(String fileName) throws IOException {
		
		File file = fetchFileResource(fileName);
		Path path = file.toPath();
		
		return Files.readAllBytes(path);
	}

}
