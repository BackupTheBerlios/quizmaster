/* QuizServer.java
 * 
 * Created on 05.01.2005
 */
package server;

import java.io.File;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import xml.IniFileReader;

/**
 * @author reinhard
 *
 * The server class starts the server
 */
public class QuizServer {
	
	public static void main(String args[])
	{
		QuizServant servant = null;
		String filename = null;
		boolean startRegistry = true;
		int questionCycle = 7500;
		
		// Process configuration file first
		if(IniFileReader.isConfigFileExisting("init.xml"))
		{
			IniFileReader reader = new IniFileReader("init.xml");
			filename = reader.getStringValue("file");
			startRegistry = reader.getBooleanValue("registry");
			questionCycle = reader.getIntValue("cycle");
			
			reader=null;
		}
		
		
		// Then parse the commandline arguments
		if(CliParamParser.paramCount(args) > 1)
		{
			CliParamParser parser = new CliParamParser(args, ":");
			
			if(parser.existsParam("-file")) filename = parser.getStringValue("-file");
			if(parser.existsParam("-registry")) startRegistry = parser.getBooleanValue("-registry");
			if(parser.existsParam("-cycle")) questionCycle = parser.getIntValue("-cycle");
			
			parser = null;
		}
		
		if(filename == null)
		{
			// That's our standard quiz file
			filename = "futurama.xml";
		}
		
		File f = new File(filename);
		
		// Error handling
		if(!f.exists())
		{
			System.out.println("The specified file <"+filename+"> does not exist!\n");
			System.exit(-1);
		}
		
		f=null;
		
		System.setProperty("java.rmi.server.codebase", "http://192.168.2.10/classes/");
		
		//Installing the security manager
		System.setProperty("java.security.policy", "java.policy");
		System.setSecurityManager (new RMISecurityManager());
		
		if(startRegistry==true)
		{
			try{
				// Create RMI-registry, we assume there's none running yet...
				System.out.println("Creating local rmiregistry");
				LocateRegistry.createRegistry(1099);
				servant = new QuizServant(filename, questionCycle);
			}
			catch(RemoteException re)
			{
				System.err.println("RemoteException in main: ");
				System.err.println(re.getMessage());
				re.printStackTrace();
			}
		}
		
		try{
			// Bind the servant to the rmi naming service
			System.out.println("Trying to bind");
			Naming.rebind("//localhost/Quizmaster", servant);
			System.out.println("Binding successful. ");
		}
		catch(Exception e){
			System.err.println("Exception in QuizServer.main(): ");
			System.err.println(e.getMessage());
			e.printStackTrace();
			
		}
	}
	
}
