/* QuizServices.java
 * 
 * Created on 05.01.2005
 */
package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import messaging.ChatMessage;
import messaging.QuizQuestion;
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
	 * @param answer
	 */
	public void takeAnswer() throws RemoteException;
	
	/**
	 * 
	 * @param numberOfQuestions
	 * @throws RemoteException
	 */
	public void startGame(int numQuestions) throws RemoteException;
	
	/**
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public QuizQuestion getActiveQuestion() throws RemoteException;
	
	/**
	 * Method for checking if a quiz is currently running
	 * @return
	 * @throws RemoteException
	 */
	public boolean quizIsActive() throws RemoteException;
	
	public String[] getClientNames() throws RemoteException;
}
