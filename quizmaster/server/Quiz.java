/* Quiz.java
 * 
 * Created on 11.01.2005
 */
package server;

import java.util.Vector;

import messaging.QuizQuestion;
import client.QuizClientServices;

/**
 * @author reinhard
 *
 *
 */
public class Quiz extends Thread {

	private QuizServant servant;
	private QuizClientServices client;
	private int numQuestions;
	private volatile boolean quit;
	private Vector clientgames;

	/**
	 * Default constructor, by not stating a number of questions, we're entering trivia mode
	 *
	 */
	public Quiz()
	{
		this.numQuestions = -1;
		this.quit = false;
		clientgames = new Vector();
	}
	
	/**
	 * Constructor which takes the number of questions, which is going to be asked
	 * @param numQuestions
	 */
	public Quiz(int numQuestions)
	{
		this.numQuestions = numQuestions;
		this.quit = false;
		clientgames = new Vector();
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		
		System.out.println(Thread.activeCount()+" threads running");
		ThreadGroup group = this.getThreadGroup();
		
		System.out.println("Threadgroup's name: "+group.getName());
		
		Thread[] t=new Thread[4];
		
		group.enumerate(t);
		
		for(int i=0; i<t.length; i++)
		{
			System.out.println(t[i].getName());
		}
		
		System.out.println("Quiz.run()");
		
		if(this.quit == true)
		{
			for(int i=0; i<clientgames.size(); i++)
			{
				System.out.println("Terminating client quiz thread");
				ClientGameLogic cgl = (ClientGameLogic) clientgames.elementAt(i);
				cgl.setQuit(true);
			}
			
			this.clientgames = null;
			this.numQuestions = 0;
			
			System.out.println("Server thread terminated");
			return;
		}
		
		System.out.println("Running quiz-thread...");
		
		if(this.numQuestions != -1 && this.numQuestions > 0)
		{
			for(int i=0; i<this.numQuestions; i++)
			{
				servant.sendQuizQuestion();
			}
		}
		else
		{
			// TODO: Implement trivia mode here
		}
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
	 * @return Returns the client.
	 */
	public QuizClientServices getClient() {
		return client;
	}
	
	/**
	 * @param client The client to set.
	 */
	public void setClient(QuizClientServices client) {
		this.client = client;
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
	public synchronized void setQuit(boolean b)
	{
		this.quit=b;
	}
	/**
	 * @return Returns the clientgames.
	 */
	public Vector getClientgames() {
		return clientgames;
	}
	/**
	 * @param clientgames The clientgames to set.
	 */
	public void setClientgames(Vector clientgames) {
		this.clientgames = clientgames;
	}
}
