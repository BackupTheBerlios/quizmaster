/* Quiz.java
 * 
 * Created on 11.01.2005
 */

package server;

import java.rmi.RemoteException;
import java.util.Vector;

import xml.QuizQuestionFactory;

import messaging.QuizAnswer;
import messaging.QuizQuestion;
import messaging.SystemMessage;
import client.QuizClientServices;

/**
 * @author reinhard
 *
 * The Quiz class manages all quiz tasks
 */
public class Quiz extends Thread {

	private QuizServant servant;
	private volatile boolean quit;
	private volatile Vector clients;
	private Vector questions;
	private int questionCounter;
	private int questionCycle;

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
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		System.out.println("Quiz thread running");
		
		this.runGame();
		
		// Doing some cleanup before exiting
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
			
		System.out.println("Quiz thread terminating");
		return;
	}
	
	/**
	 * All the quiz logic
	 *
	 */
	private synchronized void runGame()
	{
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
		
		System.out.println("Sending question to all clients in the game");
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
		
		System.out.println("Current question: " + question.getId());
		System.out.println("Correct answer: #"+question.getCorrectAnswer());
		System.out.println("There are "+answers.size()+" answers to be checked");
		
		// Iterating over the available answers
		for(int i=0; i<answers.size(); i++)
		{
			QuizAnswer answer = (QuizAnswer) answers.elementAt(i);
			
			String nick=null;
			
			try {
				nick = answer.getSender().getNickname();
			} catch (RemoteException e)
			{
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
			
			// Just to be sure that the current answer is related to the current question (SimpleClient)
			if(answer.getQuestionId()!=question.getId())
			{
				System.out.println("Answer from client "+ nick +" is not related to the current question");
				continue;
			}
			
			System.out.println(nick+" answered: #"+answer.getAnswer());
			
			// Check if the answer is correct
			if(answer.getAnswer() == question.getCorrectAnswer())
			{
				QuizClientServices client = answer.getSender();
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
				
				System.out.println("Correct answer from " +nick+". "+question.getPoints()+" points added.");
				correctAnswers+=1;
			}
		}
		
		if(correctAnswers == 0)
		{
			System.out.println("No correct answer");
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
		System.out.println("Fetching a new quiz question");
		
		// If all question have been answered, begin again
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
		this.clients.remove(client);
		this.servant.leaveGame(client);
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
}
