/* Quiz.java
 * 
 * Created on 11.01.2005
 */
package server;

import java.rmi.RemoteException;
import java.util.Vector;

import messaging.QuizAnswer;
import messaging.QuizQuestion;
import client.QuizClientServices;

/**
 * @author reinhard
 *
 *
 */
public class Quiz extends Thread {

	private QuizServant servant;
	private int numQuestions;
	private volatile boolean quit;
	private volatile Vector clients;


	/**
	 * Default constructor, by not stating a number of questions, we're entering trivia mode
	 *
	 */
	public Quiz()
	{
		this.numQuestions = -1;
		this.quit = false;
	}
	
	/**
	 * Constructor which takes the number of questions, which is going to be asked
	 * @param numQuestions
	 */
	public Quiz(int numQuestions)
	{
		this.numQuestions = numQuestions;
		this.quit = false;
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public synchronized void run()
	{
		int counter=0;
		
		System.out.println("Quiz thread running");
		
		while(!quit)
		{
			// Only counting if there's a limit, thus preventing apotential overflow
			if(numQuestions>0) counter+=1;	
			
			// Get a new question
			QuizQuestion question = this.fetchQuestion();
			
			// Send the question to all clients
			if(this.sendQuestion(question)==0)
			{
				this.quit=true;
				continue;
			}
			
			// Now wait for client answers
			try {
				Thread.sleep(5000);
			} catch(InterruptedException e)
			{
				System.err.println(e.getMessage());
				e.printStackTrace();
			}

			// Now we are checking all answers
			this.checkAnswers(question);

			if(numQuestions>0 && counter>=this.numQuestions)
			{
				this.quit = true;
			}
		}
		
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
		
		System.out.println("Quiz thread terminating");
		return;
	}
	
	/**
	 * Send a question to all clients of the quiz
	 * @param question The question to send
	 * @return Number of sent Questions
	 * @throws RemoteException
	 */
	public int sendQuestion(QuizQuestion question) 
	{
		int i=0;
		
		System.out.println("Sending question to all clients in the game");
		for(i=0; i<this.clients.size(); i++)
		{
			QuizClientServices client = (QuizClientServices) this.clients.elementAt(i);
			
			try {
				client.display(question);
			} catch(RemoteException e)
			{
				System.err.println("RemoteException in Quiz.sendQuestion");
				System.err.println("i="+i);
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
		
		return i;
	}
	
	/**
	 * Method checks all answers to a question
	 * @param question
	 * @return
	 */
	private int checkAnswers(QuizQuestion question)
	{
		int correctAnswers=0;
		Vector answers = this.servant.getAnswers();
		
		System.out.println("Correct answer would be #"+question.getCorrectAnswer());
		System.out.println("There are "+answers.size()+" answers to be checked");
		
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
			
			if(answer.getQuestionId()!=question.getId())
			{
				System.out.println("Answer from client "+ nick +" is not related to the current question");
				
				continue;
			}
			
			System.out.println(nick+" answered: #"+answer.getAnswer());
			
			if(answer.getAnswer() == question.getCorrectAnswer())
			{
				QuizClientServices client = (QuizClientServices) answer.getSender();
				try {
					client.updateScore(question.getPoints());
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
		
		this.servant.clearAnswers();
		
		return correctAnswers;
	}
	
	/**
	 * Method to fetch a question for the quiz
	 * @return
	 */
	public QuizQuestion fetchQuestion()
	{
		System.out.println("Fetching a new quiz question");
		
		// TODO: Correct implementation
		// This is just a dummy for testing purposes...
		
		QuizQuestion question = new QuizQuestion();
		
		question.setId(0);
		question.setQuestion("Wie lang war der Drei§igjŠhrige Krieg?");
		
		Vector answers = new Vector();
		String answer = new String("1000 Jahre");
		answers.add(answer);
		
		answer = new String("30 Jahre");
		answers.add(answer);
		
		answer = new String("12.5 Jahre");
		answers.add(answer);
		
		question.setAnswers(answers);
		
		question.setCorrectAnswer(2);
		
		question.setPoints(10);
		
		return question;
	}
	
	/**
	 * @return Returns the servant.
	 */
	public QuizServant getServant() {
		return servant;
	}
	
	/**
	 * @param servant The servant to set.
	 */
	public void setServant(QuizServant servant) {
		this.servant = servant;
	}
	
	/**
	 * @return Returns the numQuestions.
	 */
	public int getNumQuestions() {
		return this.numQuestions;
	}
	
	/**
	 * @param numQuestions The numQuestions to set.
	 */
	public void setNumQuestions(int numQuestions) {
		this.numQuestions = numQuestions;
	}
	
	/**
	 * Method which tells the Quizthread to stop running
	 * @param b
	 */
	public void setQuit(boolean b)
	{
		this.quit=b;
	}

	/**
	 * @return Returns the clients.
	 */
	public Vector getClients() {
		return clients;
	}
	
	/**
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

}
