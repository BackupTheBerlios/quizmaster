/* StateChecker.java
 * 
 * Created on 14.01.2005
 */
package server;

import java.rmi.RemoteException;

import messaging.ChatMessage;
import client.QuizClientServices;

/**
 * @author reinhard
 *
 * The StateChecker regulary checks the servant and initiates the requested
 * application features
 */
public class StateChecker extends Thread 
{
	private volatile boolean quit=false;
	private volatile Quiz game;
	private QuizServant servant;
	
	
	/**
	 * Constructor
	 *
	 */
	public StateChecker()
	{
		this.setName("StateChecker");
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
						
			// Checking if we have to start a new quiz
			if(this.servant.getNumQuizClients() > 0 && !this.servant.isActiveQuiz())
			{
				System.out.println("StateChecker: Starting a new quiz");
				this.servant.setActiveQuiz(true);
				game = new Quiz(this.servant.getFilename());
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
					
					// DEBUG: Constructing a new message without sender, to see if that's the problem...
					//ChatMessage msg =  new ChatMessage(newMsg.getBody());
					
					for(int i=0; i<this.servant.getConnectedClients().size(); i++)
					{
						System.out.println("Sending message to client #" + i + "...");
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
		
		System.out.println("Servant StateChecker quitting");
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
	 * Sets the servant, the statechecker is watching
	 * @return Returns the servant.
	 */
	public QuizServant getServant() {
		return servant;
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
}
