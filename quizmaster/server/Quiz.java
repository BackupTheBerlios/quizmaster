/* Quiz.java
 * 
 * Created on 11.01.2005
 */

package server;

import java.rmi.RemoteException;
import java.util.Vector;

import messaging.QuizAnswer;
import messaging.QuizQuestion;
import messaging.SystemMessage;
import tools.Console;
import tools.QuizQuestionFactory;
import client.QuizClientServices;

/**
 * The Quiz class manages all quiz tasks
 * 
 * @author reinhard
 */
public class Quiz extends Thread {

	/**
	 * Reference to the servant object
	 */
	private QuizServant servant;
	/**
	 * Indicates if the quiz thread has to quit
	 */
	private volatile boolean quit;
	/**
	 * Vector of clients participating in the quiz
	 */
	private volatile Vector clients;
	/**
	 * Vector of quiz questions
	 */
	private Vector questions;
	/**
	 * Counts asked questions
	 */
	private int questionCounter;
	/**
	 * Indicates the time for answering a question
	 */
	private int questionCycle;
	/**
	 * Temporary holds a reference to a client which left the quiz
	 */
	private QuizClientServices tmpclient;

	/**
	 * Constructor
	 * @param questionCycle Time to show each question
	 */
	public Quiz(int questionCycle)
	{
		this.quit = false;
		this.questionCounter = 0;
		this.setName("Quiz");
		this.questionCycle = questionCycle;
		this.tmpclient=null;
	}
	
	/**
	 * The threads run method
	 */
	public void run()
	{
		Console.println("Quiz thread running", Console.MSG_DEBUG);
		
		this.runGame();
		
		// Doing some cleanup before exiting
		Console.println("Quiz thread terminating", Console.MSG_DEBUG);
		
		for(int i=0; i< this.clients.size(); i++)
		{
			QuizClientServices client = (QuizClientServices) this.clients.elementAt(i);
			try {
				client.setQuizMode(false);
				this.servant.leaveGame(client);
			} catch(RemoteException e)
			{
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
		
		this.servant.setActiveQuiz(false);
		this.clients.removeAllElements();
			
		return;
	}
	
	/**
	 * All the quiz logic
	 *
	 */
	private synchronized void runGame()
	{
		// Initially mix the available questions
		this.questions = QuizQuestionFactory.mixQuestions(this.questions);
		
		
		while(!quit)
		{
			// If no more clients want to play the quiz
			if(this.clients.size()==0)
			{
				this.setQuit(true);
				this.servant.stopGame();
				continue;
			}
			
			// Get a new question
			QuizQuestion question = this.fetchQuestion();
			
			// Send the question to all clients
			if(this.sendQuestion(question)==0)
			{
				this.quit=true;
				continue;
			}
			
			// Wait for client answers
			try {
				Thread.sleep(this.questionCycle);
			} catch(InterruptedException e)
			{
				System.err.println(e.getMessage());
				e.printStackTrace();
			}

			// Checking all answers
			this.checkAnswers(question);
			
			if(this.tmpclient==null) continue;
			
			try {
				this.tmpclient.setJoinButtonActive(true);
			} catch (RemoteException e1) {
				// empty
			}
			this.tmpclient=null;
		}
	}
	
	/**
	 * Send a question to all clients of the quiz
	 * @param question The question to send
	 * @return Number of sent questions
	 * @throws RemoteException
	 */
	private int sendQuestion(QuizQuestion question) 
	{
		int i=0;
		
		Console.println("Sending question to all clients in the game", Console.MSG_NORMAL);
		for(i=0; i<this.clients.size(); i++)
		{
			QuizClientServices client = (QuizClientServices) this.clients.elementAt(i);
			
			try {
				question.setSender((QuizClientServices) this.clients.elementAt(0));
				client.display(question);
			} catch(RemoteException e)
			{
				System.err.println("RemoteException in Quiz.sendQuestion");
				System.err.println("Client="+i);
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
		return i;
	}
	
	/**
	 * Method checks all answers to a question
	 * @param question
	 * @return Number of correct answers
	 */
	private int checkAnswers(QuizQuestion question)
	{
		int correctAnswers=0;
		Vector answers = this.servant.getAnswers();
		
		Console.println("Current question: " + question.getId(), Console.MSG_DEBUG);
		Console.println("Correct answer: #"+question.getCorrectAnswer(), Console.MSG_DEBUG);
		Console.println("There are "+answers.size()+" answers to be checked", Console.MSG_DEBUG);
		
		// Iterating over the available answers
		for(int i=0; i<answers.size(); i++)
		{
			QuizAnswer answer = (QuizAnswer) answers.elementAt(i);
			
			String nick=null;
			QuizClientServices client=null;
			
			try {
				client = answer.getSender();
				if(this.servant.getConnectedClients().contains(client)) nick = client.getNickname();
			} catch (RemoteException e)
			{
				e.printStackTrace();
			}
			if(!this.servant.getConnectedClients().contains(client))
			{
				continue;
			}
			
			// Just to be sure that the current answer is related to the current question (SimpleClient)
			if(answer.getQuestionId()!=question.getId())
			{
				Console.println("Answer from client "+ nick +" is not related to the current question", Console.MSG_NORMAL);
				continue;
			}
			
			Console.println(nick+" answered: #"+answer.getAnswer(), Console.MSG_DEBUG);
			
			// Check if the answer is correct
			if(answer.getAnswer() == question.getCorrectAnswer())
			{

				try {
					// Updating client's score
					client.updateScore(question.getPoints());
					SystemMessage sysMsg = new SystemMessage();
					sysMsg.setOpCode(SystemMessage.RIGHT_ANSWER);
					sysMsg.setBody("Your answer was right. " + question.getPoints() + " points!");
					client.display(sysMsg);
				} catch(RemoteException e)
				{
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
				
				Console.println("Correct answer from " +nick+". "+question.getPoints()+" points added.", Console.MSG_DEBUG);
				correctAnswers+=1;
			}
		}
		
		if(correctAnswers == 0)
		{
			Console.println("No correct answer", Console.MSG_DEBUG);
		}
		
		// Reset the servant's answer vector
		this.servant.clearAnswers();
		
		return correctAnswers;
	}
	
	/**
	 * Method to fetch a question for the quiz
	 * @return Next question to be displayed
	 */
	public QuizQuestion fetchQuestion()
	{	
		Console.println("Fetching a new quiz question", Console.MSG_NORMAL);
		
		// If the quiz is just beginning or all question have been answered, begin again
		if(this.questionCounter>=this.questions.size())
		{
			this.questionCounter=0;
			
			// After one round mix questions
			this.questions = QuizQuestionFactory.mixQuestions(this.questions);
		}
		
		QuizQuestion question = (QuizQuestion) this.questions.elementAt(this.questionCounter);
		
		this.questionCounter+=1;
		
		return question;
	}
	
	/**
	 * Associates the servant object
	 * @param servant The servant to set.
	 */
	public void setServant(QuizServant servant) {
		this.servant = servant;
	}
	
	/**
	 * Method which tells the quiz thread to stop running
	 * @param b
	 */
	public void setQuit(boolean b)
	{
		this.quit=b;
	}

	/**
	 * Accesses the clients vector
	 * @return Returns the clients.
	 */
	public Vector getClients() {
		return clients;
	}
	
	/**
	 * Sets the clients vector
	 * @param clients The clients to set.
	 */
	public void setClients(Vector clients) {
		this.clients = clients;
	}
	
	/**
	 * Add a client to the quiz
	 * @param client
	 */
	public void addClient(QuizClientServices client)
	{
		this.clients.add(client);
	}
	
	/**
	 * Remove a client from the running quiz
	 * @param client
	 */
	public void removeClient(QuizClientServices client)
	{
		this.tmpclient = client;
		this.clients.remove(client);
	}

	/**
	 * Sets the question counter
	 * @param questionCounter The questionCounter to set.
	 */
	public void setQuestionCounter(int questionCounter) {
		this.questionCounter = questionCounter;
	}
	
	/**
	 * @param questions The questions to set.
	 */
	public void setQuestions(Vector questions) {
		this.questions = questions;
	}
	/**
	 * @param tmpclient The tmpclient to set.
	 */
	public void setTmpclient(QuizClientServices tmpclient) {
		this.tmpclient = tmpclient;
	}
}
