/* CliParamParser.java
 * 
 * Created on 19.01.2005
 */
package server;

import java.util.StringTokenizer;

/**
 * @author reinhard
 *
 * A helper class for parsing commandline arguments
 */
public class CliParamParser 
{
	private String[] args;
	private String seperator;
	
	/**
	 * Constructor
	 * @param args The String array to parse
	 * @param seperator The seperator, which seperates the argument name from the argument value
	 */
	public CliParamParser(String[] args, String seperator)
	{
		this.args = args;
		this.seperator = seperator;
	}
	
	/**
	 * Parses a string array for the specified argument
	 * @param arg The argument to look for
	 * @return Value of the specified argument, defaults to true
	 */
	public boolean getBooleanValue(String arg)
	{
		String value=this.getStringValue(arg);
		
		if(value.equals("no") || value.equals("false"))
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Returns the value of an argument as an int
	 * @param arg
	 * @return The value of an argument as an int
	 */
	public int getIntValue(String arg) throws NumberFormatException
	{
		int intValue=0;
		String value=this.getStringValue(arg);
		
		if(value==null)
		{
			return 0;
		}
		
		intValue = Integer.parseInt(value);
		return intValue;
	}
	
	/**
	 * Returns the value of an argument as a float
	 * @param arg
	 * @return The value of an argument as a float
	 */
	public float getFloatValue(String arg) throws NumberFormatException
	{
		float floatValue=0;
		String value=this.getStringValue(arg);
		
		if(value==null)
		{
			return 0;
		}
		
		floatValue = Float.parseFloat(value);
		return floatValue;
	}
	
	/**
	 * Returns the value of an argument as a double
	 * @param arg
	 * @return The value of an argument as a double
	 */
	public double getDoubleValue(String arg) throws NumberFormatException
	{
		double doubleValue=0;
		String value=this.getStringValue(arg);
		
		if(value==null)
		{
			return 0;
		}
		
		doubleValue = Double.parseDouble(value);
		return doubleValue;
	}
	
	/**
	 * Get the String value of an argument
	 * @param arg
	 * @return the value of an argument
	 */
	public String getStringValue(String arg)
	{
		String value="";
		
		for(int i=0; i<args.length; i++)
		{
			if(args[i].startsWith(arg))
			{
				StringTokenizer tok = new StringTokenizer(args[i], seperator);
				
				if(tok.countTokens()==2)
				{
					arg = tok.nextToken();
					value = tok.nextToken();
					
					return value;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Get the number of commandline arguments
	 * @return Number of commandline arguments
	 */
	public static int paramCount(String[] args)
	{
		return args.length;
	}
	
	/**
	 * Check if a specific parameter exists
	 * @param arg
	 * @return TRUE if specified parameter exists, FALSE if not
	 */
	public boolean existsParam(String arg)
	{
		for(int i=0; i< args.length; i++)
		{
			if(args[i].equals(arg))
			{
				return true;
			}
		}
		
		return false;
	}
}
