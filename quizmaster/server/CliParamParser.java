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
	 * @return Value of the specified argument, null if argument not found
	 */
	public String getStringArgument(String arg)
	{
		String value;
		
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
	 * Parses a string array for the specified argument
	 * @param arg The argument to look for
	 * @return Value of the specified argument, defaults to true
	 */
	public boolean getBooleanArgument(String arg)
	{
		String value;
		
		for(int i=0; i<args.length; i++)
		{
			if(args[i].startsWith(arg))
			{
				StringTokenizer tok = new StringTokenizer(args[i], seperator);
				
				if(tok.countTokens()==2)
				{
					arg = tok.nextToken();
					value = tok.nextToken();
					
					if(value.equals("false") || value.equals("no"))
					{
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * Get the number of commandline arguments
	 * @return Number of commandline arguments
	 */
	public int paramCount()
	{
		return this.args.length;
	}
}
