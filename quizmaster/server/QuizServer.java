/* QuizServer.java
 * 
 * Created on 05.01.2005
 */
package server;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * @author reinhard
 *
 * The server class starts the server
 */
public class QuizServer {
	
	public static void main(String args[])
	{
		QuizServant servant = null;
		
		System.setProperty("java.rmi.server.codebase", "http://localhost/classes/");
		
		try{
			// Create RMI-registry, we assume there's none running yet...
			System.out.println("Creating local rmiregistry...");
			LocateRegistry.createRegistry(1099);
			servant = new QuizServant();
		}
		catch(RemoteException re)
		{
			System.err.println("RemoteException in main: ");
			System.err.println(re.getMessage());
			re.printStackTrace();
		}
		
		try{
			System.out.println("trying to bind...");
			Naming.rebind("Quizmaster", servant);
			System.out.println("Binding successful. ");
			
		}
		catch(Exception e){
			System.err.println("Exception in main: ");
			System.err.println(e.getMessage());
			e.printStackTrace();
			
		}
	}
	
}
