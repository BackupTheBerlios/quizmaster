/* QuizClientServices.java
 * 
 * Created on 05.01.2005
 */
package client;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import messaging.ChatMessage;
import messaging.QuizQuestion;
import messaging.SystemMessage;


/**
 * @author reinhard
 *
 * The client interface is used for RMI callbacks
 */
public interface QuizClientServices extends Remote, Serializable {
	
	/**
	 * Method to get a client's nickname
	 * @return The nickname
	 * @throws RemoteException
	 */
	public String getNickname() throws RemoteException;
	
	/**
	 * Method to set a client's nickname
	 * @param nickname The nickname to set
	 * @throws RemoteException
	 */
	public void setNickname(String nickname) throws RemoteException;
	
	/**
	 * Displays list of connected clients in client list combo box.
	 * @param clients The set of currently connected clients
	 * @throws RemoteException
	 */
	public void updateClientList(String[] clients) throws RemoteException;

	/**
	 * Method for displaying a ChatMessage from another client
	 * @param msg The ChatMessage to display
	 * @throws RemoteException
	 */
	public void display(ChatMessage msg) throws RemoteException;
	
	/**
	 * Method for displaying a Quizquestion
	 * @param question The Quizquestion to display
	 * @throws RemoteException
	 */
	public void display(QuizQuestion question) throws RemoteException;
	
	/**
	 * Method for displaying a SystemMessage
	 * @param msg The SystemMessage to display
	 * @throws RemoteException
	 */
	public void display(SystemMessage msg) throws RemoteException;

	/**
	 * Method to find out if a client is in quizmode
	 * @return If a client is in quizmode
	 * @throws RemoteException
	 */
	public boolean isQuizMode() throws RemoteException;
	
	/**
	 * Method to send a client to quizmode remotly
	 * @param b
	 * @throws RemoteException
	 */
	public void setQuizMode(boolean b) throws RemoteException;
	
	/**
	 * Method to update a client's score
	 * @param points The points to add to the current score
	 * @throws RemoteException
	 */
	public void updateScore(int points) throws RemoteException;
	
	/**
	 * Method to receive a clients current score
	 * @return
	 * @throws RemoteException
	 */
	public int getScore() throws RemoteException;
	
	/**
	 * Tells the client that the quiz has been ended
	 * @throws RemoteException
	 */
	public void gameEnded() throws RemoteException;

}
