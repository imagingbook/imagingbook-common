package imagingbook.lib.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Helper class providing simple static methods for writing/reading
 * serialized data to/from files. It is recommended to serialize
 * only data structures composed of standard Java types.
 * Otherwise, if self-defined classes are reloaded, classes of 
 * previously serialized objects may not match any more, causing
 * a ClassNotFoundException to be thrown.
 * 
 * @author WB
 * @version 2015/04/19
 */
public class SerializationHelper {
	
	// This class is not supposed to be instantiated.
	private SerializationHelper() {
	}
	
	/**
	 * Writes a serialized representation of an arbitrary Java object to 
	 * a file. Make sure the serialized object is composed of standard Java types 
	 * only to avoid class loader problems.
	 *  
	 * @param any The object to be serialized.
	 * @param fileName The file to write to.
	 * @return The full path of the written file.
	 */
	public static String writeObject(Object any, String fileName) {
		File file = new File(fileName);
		String path = file.getAbsolutePath();
		try (FileOutputStream strm = new FileOutputStream(file);
			 OutputStream buffer = new BufferedOutputStream(strm);
			 ObjectOutput output = new ObjectOutputStream(buffer);) 
		{
			output.writeObject(any);
		} catch (IOException e) {
			System.err.println("Output error.");
			return null;
		}
		return path;
	}
	
	/**
	 * Reads an object (of known type) from a serialization file.
	 * The return value must be cast to the appropriate type, which
	 * must be known.
	 * 
	 * @param fileName The file containing serialized data.
	 * @return The object reconstructed from the file representation or null if unsuccessful.
	 */
	public static Object readObject(String fileName) {
		File file = new File(fileName);
		Object any = null;
		try (InputStream strm = new FileInputStream(file);
			 InputStream buffer = new BufferedInputStream(strm);
			 ObjectInput input = new ObjectInputStream(buffer);) 
		{
			any = input.readObject();
		} catch (ClassNotFoundException | IOException e) {
			System.err.println("Input error or class not found.");
			return null;
		}
		return any;
	}

}
