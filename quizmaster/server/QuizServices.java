/* QuizServices.java
 * 
 * Created on 05.01.2005
 */
package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import messaging.ChatMessage;
import messaging.QuizAnswer;
import client.QuizClientServices;

/**
 * @author reinhard
 *
 * The services interface is used to let the clients talk to the server
 */
public interface QuizServices extends Remote {
	
	/**
	 * 
	 * @param nickname
	 * @throws RemoteException
	 */
	public void register(QuizClientServices client) throws RemoteException;
	
	/**
	 * 
	 * @param nickname
	 * @throws RemoteException
	 */
	public void unregister(QuizClientServices client) throws RemoteException;
	
	/**
	 * 
	 * @param msg
	 * @throws RemoteException
	 */
	public void takeMessage(ChatMessage msg) throws RemoteException;
	
	/**
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public String[] getClientNames() throws RemoteException;
	
	/**
	 * 
	 * @param client
	 * @throws RemoteException
	 * @return False if client could not join game
	 */
	public boolean joinGame(QuizClientServices client) throws RemoteException;
	
	/**
	 * 
	 * @param client
	 * @return
	 * @throws RemoteException
	 */
	public boolean requestLeaveGame(QuizClientServices client) throws RemoteException;
	
	/**
	 * 
	 * @param answer
	 * @throws RemoteException
	 */
	public void addAnswer(QuizAnswer answer) throws RemoteException;
	
	
	// DEBUG
	public void killCheckerThread() throws RemoteException;
}
