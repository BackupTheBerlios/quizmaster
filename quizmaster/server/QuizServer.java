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

import tools.Console;
import tools.IniFileReader;

/**
 * The server class starts the server
 * 
 * @author reinhard
 */
public class QuizServer {
	
	public static void main(String args[])
	{
		QuizServant servant = null;
		String filename = null;
		boolean startRegistry = true;
		int questionCycle = 7500;
		boolean useHighscore = false;
		Console.setOutputMode(Console.MODE_ALL);
		String dbname="";
		String dbtable="";
		String dbuser="";
		String dbpass="";
		String dbhost="";
		
		// Process configuration file first
		if(IniFileReader.isConfigFileExisting("init.xml"))
		{
			IniFileReader reader = new IniFileReader("init.xml");
			filename = reader.getStringValue("quizfile");
			startRegistry = reader.getBooleanValue("startregistry");
			questionCycle = reader.getIntValue("cycletime");
			useHighscore = reader.getBooleanValue("highscore");
			Console.setOutputModeByString( reader.getStringValue("gossip") );
			
			if(useHighscore)
			{
				dbhost = reader.getStringValue("dbhost");
				dbname = reader.getStringValue("dbname");
				dbtable = reader.getStringValue("dbtable");
				dbuser = reader.getStringValue("dbuser");
				dbpass = reader.getStringValue("dbpass");
			}
			
			reader=null;
		}

		
		// Then parse the commandline arguments
		CliParamParser parser = new CliParamParser(args, ":");
		
		if(parser.paramCount() > 0)
		{
			if(parser.existsParam("-quizfile")) filename = parser.getStringValue("-quizfile");
			if(parser.existsParam("-startregistry")) startRegistry = parser.getBooleanValue("-startregistry");
			if(parser.existsParam("-cycletime")) questionCycle = parser.getIntValue("-cycletime");
			if(parser.existsParam("-gossip")) Console.setOutputModeByString( parser.getStringValue("-gossip"));
			
			parser = null;
		}
		
		// Quizfile error handling
		File f = new File(filename);
		
		if(!f.exists())
		{
			Console.println("The specified file <"+filename+"> does not exist!\n", Console.MSG_NORMAL);
			System.exit(-1);
		}
		
		f=null;
		
		// Setting up RMI...
		System.setProperty("java.rmi.server.codebase", "http://192.168.2.10/classes/");
		
		//Installing the security manager
		System.setProperty("java.security.policy", "java.policy");
		System.setSecurityManager (new RMISecurityManager());
		
		if(startRegistry==true)
		{
			try{
				// Create RMI-registry, we assume there's none running yet...
				//System.out.println("Creating local rmiregistry");
				Console.println("Creating local rmiregistry", Console.MSG_NORMAL);
				LocateRegistry.createRegistry(1099);
				servant = new QuizServant(filename, questionCycle, useHighscore);
				if(useHighscore)
				{
					servant.setDbhost(dbhost);
					servant.setDbname(dbname);
					servant.setDbtable(dbtable);
					servant.setDbuser(dbuser);
					servant.setDbpass(dbpass);
					
					servant.setupHighscore();
				}
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
			Console.println("Trying to bind", Console.MSG_NORMAL);
			Naming.rebind("//localhost/Quizmaster", servant);
			Console.println("Binding successful.", Console.MSG_NORMAL);
		}
		catch(Exception e){
			System.err.println("Exception in QuizServer.main(): ");
			System.err.println(e.getMessage());
			e.printStackTrace();
			
		}
	}
	
}
