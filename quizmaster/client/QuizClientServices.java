/* QuizClientServices.java
 * 
 * Created on 05.01.2005
 */
package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

import messaging.ChatMessage;
import messaging.QuizQuestion;
import messaging.SystemMessage;


/**
 * @author reinhard
 *
 * The client interface is used to get RMI callbacks
 */
public interface QuizClientServices extends Remote {
	
	/**
	 * The universal method for passing messages
	 * @param msg
	 */
	public String getNickname() throws RemoteException;
	public void setNickname(String nickname) throws RemoteException;
	public void updateClientList(String[] clients) throws RemoteException;

	public void display(ChatMessage msg) throws RemoteException;
	public void display(QuizQuestion msg) throws RemoteException;
	public void display(SystemMessage msg) throws RemoteException;

	public boolean isQuizMode() throws RemoteException;
	public void setQuizMode(boolean b) throws RemoteException;
	public void updateScore(int points) throws RemoteException;
	
	/**
	 * Tells the client that the quiz has been ended
	 * @throws RemoteException
	 */
	public void gameEnded() throws RemoteException;

}
