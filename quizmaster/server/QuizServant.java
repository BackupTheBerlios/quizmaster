/* QuizServant.java
 * 
 * Created on 05.01.2005
 */
package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.Vector;

import messaging.ChatMessage;
import messaging.QuizAnswer;
import xml.QuizQuestionFactory;
import client.QuizClientServices;

/**
 * @author reinhard
 *
 * The Servant class provides the server functionality
 */
public class QuizServant extends UnicastRemoteObject implements QuizServices {
	
	private Vector connectedClients;
	private Vector quizClients;
	private Vector answers;
	private Vector questions;
	private volatile Vector messages;
	private boolean activeQuiz;
	private boolean useHighscore;
	private volatile String filename;	
	private int questionCycle;

	private StateChecker checker;
	private QuizQuestionFactory quizquestionfactory;
	private HighScore highscore;
	
	/**
	 * Standard constructor
	 * @throws RemoteException
	 */
	public QuizServant(String filename, int questionCycle, boolean useHighscore) throws RemoteException
	{
		super();
		this.connectedClients = new Vector();
		this.quizClients = new Vector();
		this.answers = new Vector();
		this.messages = new Vector();
		this.activeQuiz = false;
		this.filename = filename;
		this.questionCycle = questionCycle;
		this.useHighscore=useHighscore;
		if(this.isUseHighscore())
		{
			try {
				this.highscore = new HighScore();
			} catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
		
		this.quizquestionfactory = new QuizQuestionFactory(this.filename);
		this.quizquestionfactory.readQuestions();
		this.questions = this.quizquestionfactory.getQuestions();
		this.quizquestionfactory = null;
		
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
	public void takeMessage(ChatMessage msg)  throws RemoteException 
	{
		this.messages.add(msg);
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
	 * Method checks for unique nicknames and modifies it, if necessary
	 * @param nick The nickname to check
	 * @param nr The number to add in case of not unique nickname
	 * @return Checked nickname
	 */
	private String checkNickname(String nick, int nr) 
	{
		if(nr!=0){
			nick += nr;
		}
		for(int i=0; i<connectedClients.size();i++)
		{
			QuizClientServices c = (QuizClientServices) connectedClients.elementAt(i);

			try {
				if(nick.equals(c.getNickname()))
				{
					// Recursivly call checkNickname, until a unique name is found
					nick = checkNickname(nick, nr+1);
					break;
				}
			} catch (RemoteException e) 
			{
				e.printStackTrace();
			}

		}
		return nick;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see server.QuizServices#getClientNames()
	 */
	public String[] getClientNames() throws RemoteException
	{
		String[] res = new String[connectedClients.size()];
		for(int i=0; i<this.connectedClients.size(); i++){
			res[i] = ((QuizClientServices) this.connectedClients.elementAt(i)).getNickname();
		}
		return res;
	}
	
	/**
	 * Method for sending the list of currently connected clients
	 * @throws RemoteException
	 */
	private void sendClientList() throws RemoteException
	{
		for(int i=0; i<connectedClients.size(); i++)
		{
			((QuizClientServices) connectedClients.elementAt(0)).updateClientList(this.getClientNames());
		}
	}
	
	/**
	 * @return Returns the connectedClients.
	 */
	public Vector getConnectedClients() 
	{
		return connectedClients;
	}
	
	/**
	 * @param connectedClients The connectedClients to set.
	 */
	public void setConnectedClients(Vector connectedClients) 
	{
		this.connectedClients = connectedClients;
	}
	
	/**
	 * @return Returns the quizClients.
	 */
	public Vector getQuizClients() 
	{
		return quizClients;
	}
	
	/**
	 * @param quizClients The quizClients to set.
	 */
	public void setQuizClients(Vector quizClients) 
	{
		this.quizClients = quizClients;
	}
	
	/**
	 * @return The number of clients currently in a quiz game
	 */
	public int getNumQuizClients()
	{
		return this.quizClients.size();
	}
	
	/**
	 * @return The number of currently connected clients
	 */
	public int getNumConnectedClients()
	{
		return this.connectedClients.size();
	}
	
	/**
	 * @return Returns true if a quiz is running, false if not
	 */
	public boolean isActiveQuiz() {
		return activeQuiz;
	}
	
	/**
	 * @param activeQuiz Set if a quiz is running
	 */
	public void setActiveQuiz(boolean activeQuiz) {
		this.activeQuiz = activeQuiz;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see server.QuizServices#joinGame(client.QuizClientServices)
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

	/*
	 *  (non-Javadoc)
	 * @see server.QuizServices#requestLeaveGame(client.QuizClientServices)
	 */
	public boolean requestLeaveGame(QuizClientServices client)
	{
		if(this.checker.isAlive())
		{
			if(this.isUseHighscore())
			{
				try {
					this.highscore.processScore(client.getNickname(), client.getScore());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			
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
	
	/*
	 *  (non-Javadoc)
	 * @see server.QuizServices#addAnswer(messaging.QuizAnswer)
	 */
	public void addAnswer(QuizAnswer answer)
	{
		System.out.println("Adding an answer");
		answer.setAnswer(answer.getAnswer());
		
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
	 * @return Quiz answers for the current question
	 */
	public Vector getAnswers()
	{
		return this.answers;
	}
	
	/**
	 * @return Returns the filename of the quizdata
	 */
	public String getFilename() 
	{
		return filename;
	}
	
	/**
	 * @param filename The filename of the quizdata to set.
	 */
	public void setFilename(String filename) 
	{
		this.filename = filename;
	}
	
	/**
	 * @return Returns the messages.
	 */
	public Vector getMessages() 
	{
		return messages;
	}
	
	/**
	 * @param messages The messages to set.
	 */
	public void setMessages(Vector messages) 
	{
		this.messages = messages;
	}
	
	public void resetMessages()
	{
		this.messages.removeAllElements();
	}
	/**
	 * @return Returns the questions.
	 */
	public Vector getQuestions() {
		return questions;
	}
	/**
	 * @return Returns the questionCycle.
	 */
	public int getQuestionCycle() {
		return questionCycle;
	}
	
	/**
	 * @return Returns the useHighscore.
	 */
	private boolean isUseHighscore() {
		return useHighscore;
	}

}
