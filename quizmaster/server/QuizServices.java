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
 * The QuizServices interface is used to let the clients talk to the server
 */
public interface QuizServices extends Remote {
	
	/**
	 * Method for registering a client with the server
	 * @param client The client to register
	 * @throws RemoteException
	 */
	public void register(QuizClientServices client) throws RemoteException;
	
	/**
	 * Method for unregistering a client at the server
	 * @param client The client to unregister
	 * @throws RemoteException
	 */
	public void unregister(QuizClientServices client) throws RemoteException;
	
	/**
	 * Method for receiving and distributing a chatmessage at the server
	 * @param msg
	 * @throws RemoteException
	 */
	public void takeMessage(ChatMessage msg) throws RemoteException;
	
	/**
	 * Method to get the nicknames of all connected clients
	 * @return Client names
	 * @throws RemoteException
	 */
	public String[] getClientNames() throws RemoteException;
	
	/**
	 * Method for joining a quiz game
	 * @param client The client which wants to join
	 * @throws RemoteException
	 * @return False if client could not join quiz
	 */
	public boolean joinGame(QuizClientServices client) throws RemoteException;
	
	/**
	 * Method for leaving a quiz game
	 * @param client The client who wants to leave the quiz
	 * @return True if the client was correctly removed from the quiz
	 * @throws RemoteException
	 */
	public boolean requestLeaveGame(QuizClientServices client) throws RemoteException;
	
	/**
	 * Method for adding an answer to a quizquestion for analysis at the server
	 * @param answer The answer to add
	 * @throws RemoteException
	 */
	public void addAnswer(QuizAnswer answer) throws RemoteException;
	
	/**
	 * Method for checking if a quiz is running at the moment
	 * @return True if a quiz is running, false if not
	 * @throws RemoteException
	 */
	public boolean isActiveQuiz() throws RemoteException;
}
