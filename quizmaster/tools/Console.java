/* Console.java
 * 
 * Created on 01.02.2005
 */
package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A heloer class with methods for reading and writing to the standard output
 * 
 * @author reinhard
 */
public class Console {
	
	/**
	 * Invalid message mode, results in MODE_ALL behaviour
	 */
	public static final int MODE_INVALID=-1;
	/**
	 * Show all messages
	 */
	public static final int MODE_ALL=0;
	/**
	 * Don't show debug messages
	 */
	public static final int MODE_NO_DEBUG=5;
	/**
	 * Don't show any messages
	 */
	public static final int MODE_QUIET=10;
	/**
	 * Debug output flag
	 */
	public static final int MSG_DEBUG = 0;
	/**
	 * Normal output flag
	 */
	public static final int MSG_NORMAL = 5;
	/**
	 * Always show flag
	 */
	public static final int MSG_ALWAYS = 10;

	/**
	 * The current output mode of the console
	 */
	private static int mode=MODE_ALL;
	
	/**
	 * Write a message string to the standard output without linebreak
	 * @param msg The message to send to standard output
	 */
	public static void print(String msg, int mode)
	{
		if(mode >= Console.mode)
		{
			System.out.print(msg);
		}
	}
	
	/**
	 * Write a message string to the standard output with linebreak
	 * @param msg The message to send to standard output
	 */
	public static void println(String msg, int mode)
	{
		if(mode >= Console.mode)
		{
			System.out.println(msg);
		}
	}
	
	/**
	 * Sets the output mode of the console
	 * @param mode The mode to set
	 */
	public static void setOutputMode(int mode)
	{
		Console.mode=mode;
	}
	
	/**
	 * Set the output mode of the console with a string
	 * @param s String containing the output mode
	 */
	public static void setOutputModeByString(String s)
	{
		if(s.equals("MODE_ALL"))
		{
			Console.mode=Console.MODE_ALL;
		}
		else if(s.equals("MODE_NO_DEBUG"))
		{
			Console.mode=Console.MODE_NO_DEBUG;
		}
		else if(s.equals("MODE_QUIET"))
		{
			Console.mode=Console.MODE_QUIET;
		}
		else
		{
			Console.mode=Console.MODE_INVALID;
		}
	}
	
	/**
	 * Read a string from the standard input
	 * @return String entered by the user
	 */
	public static String readLine()
	{
		String s=null;
		
		// We need an InputStreamReader for reading user input
		InputStreamReader reader = new InputStreamReader(System.in);
		// Wrap the reader with a buffered reader.
		BufferedReader buf_in = new BufferedReader (reader);
		
		try {
			s = buf_in.readLine();
		} catch (IOException e) {
			System.err.println("Client IOException in Console.readLine()");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		return s;
	}
	
	/**
	 * Read a string from the standard input
	 * @param msg Message to show before input
	 * @return String entered by the user
	 */
	public static String readLine(String msg)
	{
		Console.print(msg, Console.MSG_ALWAYS);
		return Console.readLine();
	}
}
