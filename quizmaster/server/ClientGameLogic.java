/* ClientGameLogic.java
 * 
 * Created on 11.01.2005
 */
package server;

import java.rmi.RemoteException;

import client.QuizClientServices;

import messaging.QuizQuestion;

/**
 * @author reinhard
 *
 *
 */
public class ClientGameLogic extends Thread {
	
	private QuizServices server;
	private QuizClientServices client;
	private int numQuestions;
	private QuizQuestion activeQuestion;
	private volatile boolean quit;
	
	/**
	 * Default constructor
	 *
	 */
	public ClientGameLogic()
	{
		this.numQuestions = 0;
		this.quit = false;
	}
	
	/**
	 * Constructor which takes the number of requested Questions as an argument
	 * @param numQuestions
	 */
	public ClientGameLogic(int numQuestions)
	{
		this.numQuestions = numQuestions;
		this.quit = false;
	}
	
	/**
	 * @return Returns the activeQuestion.
	 */
	public QuizQuestion getActiveQuestion() {
		return activeQuestion;
	}
	/**
	 * @param activeQuestion The activeQuestion to set.
	 */
	public void setActiveQuestion(QuizQuestion activeQuestion) {
		this.activeQuestion = activeQuestion;
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

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		
		try {
			if(this.client.isQuizMode()==false)
			{
				
				System.out.println("Client thread terminated");
				return;
			}
		} catch(RemoteException e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		System.out.println("ClientGameLogic thread running");
		
		try {
			server.takeAnswer();
		} catch(RemoteException e)
		{
			System.err.println("RemoteException in ClientGameLogic.run()");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @return Returns the client.
	 */
	public QuizServices getServer() {
		return server;
	}
	
	/**
	 * @param client The client to set.
	 */
	public void setServer(QuizServices server) {
		this.server = server;
	}
	
	/**
	 * @return Returns the numQuestions.
	 */
	public int getNumQuestions() {
		return numQuestions;
	}
	
	/**
	 * @param numQuestions The numQuestions to set.
	 */
	public void setNumQuestions(int numQuestions) {
		this.numQuestions = numQuestions;
	}
	
	public synchronized void setQuit(boolean b)
	{
		this.quit=b;
	}
}
