/* IniFileReader.java
 * 
 * Created on 20.01.2005
 */
package tools;

import java.io.File;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class for reading an XML configuration file
 * 
 * @author reinhard
 */
public class IniFileReader 
{
	/**
	 * Hashtable to store parameters
	 */
	private Hashtable params;

	/**
	 * Constructor
	 * @param filename
	 */
	public IniFileReader(String filename)
	{
		this.params = new Hashtable();
		this.readParams(filename);
	}
	
	/**
	 * Get the associated boolean value of a param
	 * @param param The parameter to get a value for
	 */
	public boolean getBooleanValue(String param)
	{
		String s = this.getValueFromDict(param);
		
		if(s.equals("yes") || s.equals("true"))
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Get the associated String value of a param
	 * @param param The parameter to get a value for
	 */
	public String getStringValue(String param)
	{
		return this.getValueFromDict(param);
	}
	
	/**
	 * Get the associated int value of a param
	 * @param param The parameter to get a value for
	 */
	public int getIntValue(String param) throws NumberFormatException
	{
		return Integer.parseInt( this.getValueFromDict(param) );
	}
	
	/**
	 * Get the associated float value of a param
	 * @param param The parameter to get a value for
	 */
	public float getFloatValue(String param) throws NumberFormatException
	{
		return Float.parseFloat( this.getValueFromDict(param) );
	}

	/**
	 * Get the associated double value of a param
	 * @param param The parameter to get a value for
	 */
	public double getDoubleValue(String param) throws NumberFormatException
	{
		return Double.parseDouble( this.getValueFromDict(param) );
	}
	
	/**
	 * Get a specific value from the dictionary
	 * @param param The parameter to get a value for
	 * @return Value of the requested entry as a String
	 */
	private String getValueFromDict(String param)
	{
		return (String) this.params.get(param);
	}
	
	/**
	 * Read all parameters from the configuration file
	 *
	 */
	private void readParams(String filename)
	{
		Document doc = XMLHandler.readDocFromFile(new File(filename));
		
		if(doc==null)
		{
			System.err.println("No Document constructed from XML-File");
			System.err.println("File: "+ filename);
		}
		
		// Iterate over param elements
		NodeList params = doc.getElementsByTagName("param");
		
		int i=0;
		for(i=0; i<params.getLength(); i++)
		{
			Node param = params.item(i);
			String name = param.getAttributes().getNamedItem("name").getNodeValue();
			String value = param.getAttributes().getNamedItem("value").getNodeValue();
			
			this.params.put(name, value);
		}
	}
	
	/**
	 * Class method for testing if configuration file is existing
	 * @param filename The filename to check
	 * @return TRUE, if file is existing, FALSE if not
	 */
	public static boolean isConfigFileExisting(String filename)
	{
		File f = new File(filename);
		
		// TODO: Also check for file contents, not only for existence
		if(f.exists())
		{
			f=null;
			return true;
		}
		
		f=null;
		return false;
	}
}
