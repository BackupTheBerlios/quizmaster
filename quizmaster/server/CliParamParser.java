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
	
	public CliParamParser(String[] args)
	{
		this.args = args;
	}
	
	/**
	 * Parses a stringarray for the specified argument
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
				StringTokenizer tok = new StringTokenizer(args[i], ":");
				
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
	 * Parses a stringarray for the specified argument
	 * @param arg The argument to look for
	 * @return Value of the specified argument, null if argument not found
	 */
	public boolean getBooleanArgument(String arg)
	{
		String value;
		
		for(int i=0; i<args.length; i++)
		{
			if(args[i].startsWith(arg))
			{
				StringTokenizer tok = new StringTokenizer(args[i], ":");
				
				if(tok.countTokens()==2)
				{
					arg = tok.nextToken();
					value = tok.nextToken();
					
					if(value.equals("false"))
					{
						return false;
					}
					else
					{
						return true;
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
