/* StateChecker.java
 * 
 * Created on 14.01.2005
 */
package server;

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
	 * The run method for this thread
	 * 
	 */
	public void run()
	{
		while(!quit)
		{
			// Sending the state checker thread to sleep for 500ms
			try {
				Thread.sleep(500);
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
				game.start();
			}
			
			// Checking if we have to stop a running quiz
			else if(this.servant.getNumQuizClients() == 0 && this.servant.isActiveQuiz())
			{
				game.setQuit(true);
				this.servant.stopGame();
			}				
		}
		
		System.out.println("Servant StateChecker quitting");
		return;
	}
	
	/**
	 * @return Returns the quit.
	 */
	public boolean isQuit() {
		return quit;
	}
	/**
	 * @param quit The quit to set.
	 */
	public void setQuit(boolean quit) {
		this.quit = quit;
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
	 * @return Returns the game.
	 */
	public Quiz getGame() {
		return game;
	}
}
