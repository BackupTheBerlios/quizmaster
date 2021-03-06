/* StateChecker.java
 * 
 * Created on 14.01.2005
 */
package server;

import java.rmi.RemoteException;

import messaging.ChatMessage;
import tools.Console;
import client.QuizClientServices;

/**
 * The StateChecker regulary checks the servant and initiates the requested
 * application features
 * 
 * @author reinhard
 */
public class StateChecker extends Thread 
{
	private volatile boolean quit=false;
	private volatile Quiz game;
	private QuizServant servant;
	private int numConnectedClients;
	
	
	/**
	 * Constructor
	 *
	 */
	public StateChecker()
	{
		this.setName("StateChecker");
		this.numConnectedClients=0;
	}
	
	/**
	 * The run method for this thread
	 * 
	 */
	public void run()
	{
		while(!quit)
		{
			// Sending the state checker thread to sleep for 500ms
			try {
				Thread.sleep(100);
			} catch (InterruptedException e)
			{
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
			
			// Check if the list of connected clients changed
			if(this.servant.getNumConnectedClients()!=this.numConnectedClients)
			{
				for(int i=0; i<this.servant.getNumConnectedClients(); i++)
				{
					QuizClientServices client = (QuizClientServices) this.servant.getConnectedClients().elementAt(i);
					try {
						client.updateClientList(this.servant.getClientNames());
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}
				}
				this.numConnectedClients=this.servant.getNumConnectedClients();
			}
						
			// Checking if we have to start a new quiz
			if(this.servant.getNumQuizClients() > 0 && !this.servant.isActiveQuiz())
			{
				Console.println("StateChecker: Starting a new quiz", Console.MSG_DEBUG);
				this.servant.setActiveQuiz(true);
				game = new Quiz(this.servant.getQuestionCycle());
				game.setServant(this.servant);
				game.setClients(this.servant.getQuizClients());
				game.setQuestions(this.servant.getQuestions());
				game.start();
			}
			
			// Checking if we have to stop a running quiz
			else if(this.servant.getNumQuizClients() == 0 && this.servant.isActiveQuiz())
			{
				game.setQuit(true);
				this.servant.stopGame();
			}
			
			// Checking if there are messages to send
			if(this.servant.getMessages().size()>0)
			{
				for(int j=0; j<this.servant.getMessages().size(); j++)
				{
					ChatMessage newMsg = (ChatMessage) this.servant.getMessages().elementAt(j);
					
					for(int i=0; i<this.servant.getConnectedClients().size(); i++)
					{
						Console.println("Sending message", Console.MSG_NORMAL);
						try {
							Console.println("User " + ((QuizClientServices) this.servant.getConnectedClients().elementAt(i)).getNickname(), Console.MSG_DEBUG);
						} catch (RemoteException e1) {
							e1.printStackTrace();
						}
						Console.println("Message: "+newMsg.getBody(), Console.MSG_DEBUG);
						QuizClientServices client = (QuizClientServices) this.servant.getConnectedClients().elementAt(i);
						try{
							client.display(newMsg);
						} catch(RemoteException e)
						{
							e.printStackTrace();
						}
					}
				}
				
				this.servant.resetMessages();
			}
		}
		
		Console.println("Servant StateChecker quitting", Console.MSG_DEBUG);
		return;
	}
	
	/**
	 * Getter method for the quit variable, which indicates that the thread will be quitting soon
	 * @return Returns the quit.
	 */
	public boolean isQuit() {
		return quit;
	}
	
	/**
	 * Setter method for the quit variable, which indicates that the thread will be quitting soon
	 * @param quit The quit to set.
	 */
	public void setQuit(boolean quit) {
		this.quit = quit;
	}

	/**
	 * Gets the servant the statechecker is watching
	 * @param servant The servant to set.
	 */
	public void setServant(QuizServant servant) {
		this.servant = servant;
	}

	/**
	 * Gets the quiz object the statechecker is watching
	 * @return Returns the game.
	 */
	public Quiz getGame() {
		return game;
	}
	
	/**
	 * @return Returns the numConnectedClients.
	 */
	public int getNumConnectedClients() {
		return numConnectedClients;
	}
	
	/**
	 * @param numConnectedClients The numConnectedClients to set.
	 */
	public void setNumConnectedClients(int numConnectedClients) {
		this.numConnectedClients = numConnectedClients;
	}
}
