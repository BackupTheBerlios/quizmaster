/* QuizServant.java
 * 
 * Created on 05.01.2005
 */
package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

import messaging.ChatMessage;
import messaging.QuizAnswer;
import client.QuizClientServices;

/**
 * @author reinhard
 *
 * The Servant class provides the server functionality
 */
public class QuizServant extends UnicastRemoteObject implements QuizServices {
	
	private Vector connectedClients;
	private Vector quizClients;
	private boolean activeQuiz;
	private Vector answers;
	
	private StateChecker checker;
	
	
	/**
	 * Standard constructor
	 * @throws RemoteException
	 */
	public QuizServant() throws RemoteException
	{
		super();
		this.connectedClients = new Vector();
		this.quizClients = new Vector();
		this.answers = new Vector();
		this.activeQuiz = false;
		
		checker = new StateChecker();
		checker.setServant(this);
		checker.start();
	}
	
	/**
	 * Method for stopping a quiz game
	 *
	 */
	public synchronized void stopGame()
	{
		System.out.println("Stopping server quiz thread");
		this.setActiveQuiz(false);
		
		try {
			this.gameCleanUp();
		} catch (RemoteException e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Perform cleanup after a quiz game
	 * @throws RemoteException
	 */
	public void gameCleanUp() throws RemoteException
	{
		System.out.println("Cleaning up...");
		
		for(int i=0; i<this.quizClients.size(); i++)
		{
			QuizClientServices client = (QuizClientServices) this.quizClients.elementAt(i);
			client.gameEnded();
		}
		
		this.quizClients.removeAllElements();
	}
	
	/* (non-Javadoc)
	 * @see server.QuizServices#takeMessage(null)
	 */
	public void takeMessage(ChatMessage msg)  throws RemoteException {
		
		System.out.println("Entering QuizServant.takeMessage()");
		System.out.println("msg.body: "+msg.getBody());
		System.out.println("msg.sender: "+msg.getSender());
		for(int i=0; i<this.connectedClients.size(); i++)
		{
			System.out.println("Sending message to client #" + i + "...");
			QuizClientServices client = (QuizClientServices) this.connectedClients.elementAt(i);
			client.display(msg);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see server.QuizServices#register(java.lang.String)
	 */
	public void register(QuizClientServices client) throws RemoteException {
		System.out.println("Trying to register client with username "+client.getNickname() + "...");
		String nick = checkNickname(client.getNickname(), 0);
		client.setNickname(nick);
		
		this.connectedClients.add(client);
		
		//send updated client list to all clients
		sendClientList();
	}
	
	/* (non-Javadoc)
	 * @see server.QuizServices#unregister(client.QuizClientServices)
	 */
	public void unregister(QuizClientServices client) throws RemoteException 
	{
		System.out.println(this.connectedClients.size() + " client(s) registered");
		System.out.println("Trying to unregister client...");
		
		if(this.quizClients.contains(client))
		{
			this.quizClients.remove(client);
			this.checker.getGame().removeClient(client);
		}
		
		this.connectedClients.removeElement(client);
		System.out.println(this.connectedClients.size() + " client(s) registered");
		
		//send updated client list to all clients
		sendClientList();
	}
	
	
	/**
	 * Method to make sure the client's username isn't already in use
	 * 
	 * @param nick
	 * @param nr
	 * @return
	 * @throws RemoteException
	 */
	private String checkNickname(String nick, int nr)throws RemoteException{
		if(nr!=0){
			nick += nr;
		}
		for(int i=0; i<connectedClients.size();i++){
			QuizClientServices c = (QuizClientServices) connectedClients.elementAt(i);
			if(nick.equals(c.getNickname())){
				nick = checkNickname(nick, nr+1);
				break;
			}
		}
		return nick;
	}
	
	/**
	 * @return Returns the nicknames of all clients. 
	 */
	public String[] getClientNames() throws RemoteException{
		String[] res = new String[connectedClients.size()];
		for(int i=0; i<connectedClients.size(); i++){
			res[i] = ((QuizClientServices) connectedClients.elementAt(i)).getNickname();
		}
		return res;
	}
	
	private void sendClientList() throws RemoteException{
		for(int i=0; i<connectedClients.size(); i++){
			((QuizClientServices) connectedClients.elementAt(0)).updateClientList(getClientNames());
		}
	}
	
	/**
	 * @return Returns the connectedClients.
	 */
	public Vector getConnectedClients() {
		return connectedClients;
	}
	
	/**
	 * @param connectedClients The connectedClients to set.
	 */
	public void setConnectedClients(Vector connectedClients) {
		this.connectedClients = connectedClients;
	}
	
	/**
	 * @return Returns the quizClients.
	 */
	public Vector getQuizClients() {
		return quizClients;
	}
	
	/**
	 * @param quizClients The quizClients to set.
	 */
	public void setQuizClients(Vector quizClients) {
		this.quizClients = quizClients;
	}
	
	/**
	 * 
	 * @return The number of clients currently in a quiz game
	 */
	public int getNumQuizClients()
	{
		return this.quizClients.size();
	}
	
	/**
	 * 
	 * @return The number of currently connected clients
	 */
	public int getNumConnectedClients()
	{
		return this.connectedClients.size();
	}
	
	/**
	 * @return Returns the activeQuiz.
	 */
	public boolean isActiveQuiz() {
		return activeQuiz;
	}
	
	/**
	 * @param activeQuiz The activeQuiz to set.
	 */
	public void setActiveQuiz(boolean activeQuiz) {
		this.activeQuiz = activeQuiz;
	}
	
	/**
	 * A client can request to join a quizgame using this method
	 * @param client
	 * @return Returns if joining a quiz was successful
	 */
	public boolean joinGame(QuizClientServices client)
	{
		if(this.checker.isAlive())
		{
			this.quizClients.add(client);
			return true;
		}
		return false;
	}
	
	/**
	 * DEBUG: Method for killing the statechecker thread
	 *
	 */
	public void killCheckerThread()
	{
		System.out.println("Active threads: "+ Thread.activeCount());
		this.checker.setQuit(true);
	}
	
	/**
	 * A client can request to leave the running game using this method
	 */
	public boolean requestLeaveGame(QuizClientServices client)
	{
		if(this.checker.isAlive())
		{
			this.checker.getGame().removeClient(client);
			return false;
		}
		return true;
	}
	
	/**
	 * Clients call this method if they want to leave a quiz game
	 * @param client The caller
	 * @return Returns if leaving the quiz was successful
	 */
	public boolean leaveGame(QuizClientServices client) 
	{	
		if(this.checker.isAlive())
		{
			this.quizClients.removeElement(client);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Add a quiz answer to the vector of quiz answers
	 * @param answer
	 */
	public void addAnswer(QuizAnswer answer)
	{
		System.out.println("Adding an answer for question #" + answer.getQuestionId());
		System.out.println("Adding an answer");
		
		// HACK: Manipulating the answerid
		answer.setAnswer(answer.getAnswer()+1);
		
		this.answers.add(answer);
	}
	
	/**
	 * Remove all previous answers from the vector
	 *
	 */
	public void clearAnswers()
	{
		this.answers.removeAllElements();
	}
	
	/**
	 * 
	 * @return Quiz answers for the current question
	 */
	public Vector getAnswers()
	{
		return this.answers;
	}
}
