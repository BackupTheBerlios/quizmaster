/* IniFileReader.java
 * 
 * Created on 20.01.2005
 */
package xml;

import java.io.File;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author reinhard
 *
 * Class for reading an XML configuration file
 */
public class IniFileReader 
{
	private String filename;
	private Hashtable params;
	private File iniFile;
	
	/**
	 * Constructor
	 * @param filename
	 */
	public IniFileReader(String filename)
	{
		this.filename = filename;
		this.params = new Hashtable();
		this.openFile();
		this.readParams();
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
	 * Get the associated flaot value of a param
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
	 * Method for opening an initialization file
	 * @return TRUE if opening was successful, FALSE if not
	 */
	private boolean openFile()
	{
		this.iniFile = new File(this.filename);
		
		if(this.iniFile!=null)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Close an initialization file
	 * @return TRUE if successful, FALSE if initialization file hasn't been opened before
	 */
	private boolean closeFile()
	{
		if(this.iniFile==null)
		{
			return false;
		}
		
		this.iniFile=null;
		
		return true;
	}
	
	/**
	 * Read all parameters from the configuration file
	 *
	 */
	private void readParams()
	{
		Document doc = XMLReader.readDocFromFile(this.iniFile);
		
		if(doc==null)
		{
			System.err.println("No Document constructed from XML-File");
			System.err.println("File: "+ this.iniFile);
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
		
		this.closeFile();
		
		System.out.println("#"+i+" parameters read from configuration file");
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
