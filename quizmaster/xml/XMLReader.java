/* XMLReader.java
 * 
 * Created on 22.01.2005
 */
package xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

/**
 * @author reinhard
 *
 *
 */
public class XMLReader 
{
	
	/**
	 * Read XML data into a Document object
	 * @param f The file to read from
	 * @return The document constructed from the xml file
	 */
	public static Document readDocFromFile(File f)
	{
		Document doc = null;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringComments(true);
		try {
			DocumentBuilder db= dbf.newDocumentBuilder();
			doc = db.parse(f);
		} catch (ParserConfigurationException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch(IOException e)
		{
			System.err.println(e.getMessage());
		} catch(Exception e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		return doc;
	}

}
