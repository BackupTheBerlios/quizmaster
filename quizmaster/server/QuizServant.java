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
import messaging.SystemMessage;
import tools.Console;
import tools.QuizQuestionFactory;
import client.QuizClientServices;

/**
 * The Servant class provides the server functionality
 * @author reinhard
 */
public class QuizServant extends UnicastRemoteObject implements QuizServices {
	
	/**
	 * Vector of connected clients
	 */
	private Vector connectedClients;
	/**
	 * Vector of clients participating in the quiz
	 */
	private Vector quizClients;
	/**
	 * Vector of answers sent by quizclients
	 */
	private Vector answers;
	/**
	 * Vector of quiz questions
	 */
	private Vector questions;
	/**
	 * Vector of chat messages sent by clients
	 */
	private volatile Vector messages;
	/**
	 * Indicates if a quiz is active
	 */
	private boolean activeQuiz;
	/**
	 * Indicates if the server uses the highscore feature
	 */
	private boolean useHighscore;
	/**
	 * The filename of the quizfile
	 */
	private volatile String filename;
	/**
	 * The time between to questions in the quiz
	 */
	private int questionCycle;
	/**
	 * The description of the quiz
	 */
	private String quizDesc;
	/**
	 * The database name for the highscore table
	 */
	private String dbname;
	/**
	 * the database table name for the highscore
	 */
	private String dbtable;
	/**
	 * The database username for the highscore
	 */
	private String dbuser;
	/**
	 * The database password for the highscore
	 */
	private String dbpass;
	/**
	 * The database host for the highscore
	 */
	private String dbhost;
	/**
	 * Reference to the StateChecker object
	 */
	private StateChecker checker;
	/**
	 * Reference to the Highscore object
	 */
	private HighScore highscore;
	
	/**
	 * Constructor
	 * @param filename The filename to read questions from
	 * @param questionCycle The time to show each question
	 * @param useHighscore Use the highscore feature?
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
		
		QuizQuestionFactory quizquestionfactory = new QuizQuestionFactory(this.filename);
		quizquestionfactory.readQuestions();
		this.questions = quizquestionfactory.getQuestions();
		this.quizDesc = quizquestionfactory.getQuizDesc();
		quizquestionfactory = null;
		
		checker = new StateChecker();
		checker.setServant(this);
		checker.start();
	}
	
	/**
	 * Tells the servant that the game is using the highscore feature
	 *
	 */
	public void setupHighscore()
	{
		if(this.isUseHighscore())
		{
			try {
				this.highscore = new HighScore(this.dbhost, this.dbname, this.dbtable, this.dbuser, this.dbpass);
			} catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Method for stopping a quiz game
	 *
	 */
	public synchronized void stopGame()
	{
		Console.println("Stopping server quiz thread", Console.MSG_DEBUG);
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
		Console.println("Cleaning up...", Console.MSG_DEBUG);
		
		for(int i=0; i<this.quizClients.size(); i++)
		{
			QuizClientServices client = (QuizClientServices) this.quizClients.elementAt(i);
			client.gameEnded();
		}
		
		this.quizClients.removeAllElements();
	}
	
	/**
	 * Takes a ChatMessage from a client
	 * @param msg The ChatMessage of the client
	 * @throws RemoteException
	 */
	public void takeMessage(ChatMessage msg)  throws RemoteException 
	{
		this.messages.add(msg);
	}
	
	/**
	 * Registers a client with the servant
	 * @param client The client to register
	 * @throws RemoteException
	 */
	public void register(QuizClientServices client) throws RemoteException {
		Console.println("Registering client with username "+client.getNickname() + "...", Console.MSG_NORMAL);
		String nick = checkNickname(client.getNickname(), 0);
		client.setNickname(nick);
		
		this.connectedClients.add(client);
		
		SystemMessage msg = new SystemMessage();
		msg.setOpCode(SystemMessage.QUIZ_DESC);
		msg.setBody(this.quizDesc);
		
		// Send welcome message
		client.display(msg);
	}
	
	/**
	 * Unregister a client
	 * @param client The client to unregister
	 * @throws RemoteException
	 */
	public void unregister(QuizClientServices client) throws RemoteException 
	{
		// Handling the highscore
		if(this.isUseHighscore())
		{
			try {
				this.highscore.processScore(client.getNickname(), client.getScore());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		// Unregistering
		Console.println("Unregistering client...", Console.MSG_NORMAL);
		
		if(this.quizClients.contains(client))
		{
			this.quizClients.remove(client);
			this.checker.getGame().removeClient(client);
		}
		
		this.connectedClients.removeElement(client);
		Console.println(this.connectedClients.size() + " client(s) registered", Console.MSG_DEBUG);
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
	
	/**
	 * Access the client names
	 * @throws RemoteException
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
	 * Access connected clients
	 * @return Returns the connectedClients.
	 */
	public Vector getConnectedClients() 
	{
		return connectedClients;
	}
	
	/**
	 * Access clients participating in the quiz
	 * @return Returns the quizClients.
	 */
	public Vector getQuizClients() 
	{
		return quizClients;
	}
	
	/**
	 * Get the number of quiz clients
	 * @return The number of clients currently in a quiz game
	 */
	public int getNumQuizClients()
	{
		return this.quizClients.size();
	}
	
	/**
	 * Get number of connected clients
	 * @return The number of currently connected clients
	 */
	public int getNumConnectedClients()
	{
		return this.connectedClients.size();
	}
	
	/**
	 * Checks if a quiz is active
	 * @return Returns true if a quiz is running, false if not
	 */
	public boolean isActiveQuiz() {
		return activeQuiz;
	}
	
	/**
	 * Sets if a quiz is active
	 * @param activeQuiz Set if a quiz is running
	 */
	public void setActiveQuiz(boolean activeQuiz) {
		this.activeQuiz = activeQuiz;
	}
	
	/**
	 * Add a client to a quiz
	 * @param client The client to add
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
	 * Remove a client from a quiz
	 * @param client The client to remove
	 * @return FALSE, if the client isn't in the quiz anymore, TRUE if it still is
	 */
	public boolean requestLeaveGame(QuizClientServices client)
	{
		if(this.checker.isAlive())
		{
			this.checker.getGame().removeClient(client);
			this.leaveGame(client);
			return false;
		}
		return true;
	}
	
	/**
	 * Clients call this method if they want to leave a quiz game
	 * @param client The client to remove from the game
	 */
	public void leaveGame(QuizClientServices client) 
	{	
		this.quizClients.removeElement(client);
	}
	
	/**
	 * Add a quizanswer
	 * @param answer The answer to add
	 */
	public void addAnswer(QuizAnswer answer)
	{
		Console.println("Adding an answer", Console.MSG_DEBUG);
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
	 * Access answers for current question
	 * @return Quiz answers for the current question
	 */
	public Vector getAnswers()
	{
		return this.answers;
	}
	
	/**
	 * Set the quiz filename
	 * @param filename The filename of the quizdata to set.
	 */
	public void setFilename(String filename) 
	{
		this.filename = filename;
	}
	
	/**
	 * Access messages vector
	 * @return Returns the messages.
	 */
	public Vector getMessages() 
	{
		return messages;
	}
	
	/**
	 * Reset messages vector (after sending)
	 *
	 */
	public void resetMessages()
	{
		this.messages.removeAllElements();
	}
	
	/**
	 * Access quiz questions
	 * @return Returns the questions.
	 */
	public Vector getQuestions() {
		return questions;
	}
	
	/**
	 * Access questionCycle
	 * @return Returns the questionCycle.
	 */
	public int getQuestionCycle() {
		return questionCycle;
	}
	
	/**
	 * Access useHighscore
	 * @return Returns the useHighscore.
	 */
	private boolean isUseHighscore() {
		return useHighscore;
	}

	/**
	 * Set the database name to use
	 * @param dbname The dbname to set.
	 */
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	
	/**
	 * Set the database table to use
	 * @param dbtable The dbtable to set.
	 */
	public void setDbtable(String dbtable) {
		this.dbtable = dbtable;
	}
	
	/**
	 * Set the database password to use
	 * @param dbpass The dbpass to set.
	 */
	public void setDbpass(String dbpass) {
		this.dbpass = dbpass;
	}
	
	/**
	 * Set the database user to use
	 * @param dbuser The dbuser to set.
	 */
	public void setDbuser(String dbuser) {
		this.dbuser = dbuser;
	}
	
	/**
	 * Set the database host to use
	 * @param dbhost The dbhost to set.
	 */
	public void setDbhost(String dbhost) {
		this.dbhost = dbhost;
	}
	
	/**
	 * Access the quiz description
	 * @return Returns the quizDesc.
	 */
	public String getQuizDesc() {
		return quizDesc;
	}
}
