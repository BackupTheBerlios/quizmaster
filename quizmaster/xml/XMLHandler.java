/* XMLReader.java
 * 
 * Created on 22.01.2005
 */
package xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * @author reinhard
 *
 * A class for managing loading and saving Document data from and to files
 */
public class XMLHandler 
{
	private String xmlString;	
	
	public XMLHandler()
	{
		this.xmlString="";
	}
	
	/**
	 * Method for saving a Document object to a file
	 * @param filename The file to save to
	 * @param document The document to save
	 * @return TRUE if everything worked out fine, FALSE if not
	 */
	public static boolean saveDocToFile(String filename, Document document)
	{
		System.out.println("Saving xml file "+filename);
		
		// Setting file object to save to
		File xmlout = new File(filename);
		FileOutputStream fos=null;
		Transformer transformer=null;
		
		try{
			// Creating FileOutputStream
			fos=new FileOutputStream(xmlout);
		} catch(FileNotFoundException e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
			return false;
		}
		
		// Creating a transformer factory
		TransformerFactory tf = TransformerFactory.newInstance();
		
		try {
			// Creating a new transformer object, for creating a result tree from Document data
			transformer = tf.newTransformer();
		} catch (TransformerConfigurationException e1) {
			System.err.println(e1.getMessage());
			e1.printStackTrace();
			return false;
		}
		
		// Create a DOMSource object, which holds the document data source
		DOMSource source = new DOMSource(document);
		
		StreamResult result = new StreamResult(fos);
		
		try {
			// The transformer writes the source data to the fileoutputstream
			transformer.transform(source, result);
		} catch (TransformerException e2) {
			System.err.println(e2.getMessage());
			e2.printStackTrace();
		}
		
		System.out.println("XML file saved");
		
		return true;
	}
	
	/**
	 * Read XML data into a Document object
	 * @param f The file to read from
	 * @return The document constructed from the xml file
	 */
	public static Document readDocFromFile(File f)
	{
		Document doc=null;
		
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
	
	/**
	 * Append XML data for latter writing to file
	 * HACK: That's only for getting started!
	 * TODO: Find out how to construct Node's from input, appending them to a Document object
	 * @param s The string to add
	 */
	public void appendForSaving(String s)
	{
		this.xmlString+=s+"\n";
	}
	
	/**
	 * Save the gathered information held by the object to file
	 * @param filename
	 * @return TRUE if everything worked fine, FALSE if not
	 */
	public boolean saveFile(String filename)
	{
		FileWriter fileout=null;

		try {
			fileout = new FileWriter(filename);
		} catch (IOException e1) {
			System.err.println(e1.getMessage());
			e1.printStackTrace();
		}

		BufferedWriter out = new BufferedWriter(fileout);
		
		try {
			out.write(this.xmlString);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		try {
			out.close();
		} catch (IOException e2) {
			System.err.println(e2.getMessage());
			e2.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * Set the DocType for the file to write
	 * This is part of the HACK
	 * @param ver XML Version
	 * @param encoding Document encoding
	 * @param doctypename The name of the document type
	 * @param dtdfile The name of the dtd file
	 */
	public void setDocType(String ver, String encoding, String doctypename, String dtdfile)
	{
		this.xmlString="<?xml version="+ver+" encoding="+encoding+"?>\n"+
					  "<!DOCTYPE "+doctypename+" SYSTEM "+dtdfile+">\n"+this.xmlString;
	}

}
