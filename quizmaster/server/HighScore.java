/* HighScore.java
 * 
 * Created on 20.01.2005
 */
package server;

import java.util.Hashtable;

/**
 * @author reinhard
 *
 * A class for calculating Highscores
 */
public class HighScore 
{
	private Hashtable highscore;
	private final int MAXENTRIES = 10;
	
	/**
	 * Constructor
	 *
	 */
	public HighScore()
	{
		this.highscore = new Hashtable( MAXENTRIES );
	}
	
	/**
	 * Checks if a users score qualifies for a highscore 
	 * @return TRUE, if new Highscore, FALSE if not
	 */
	public boolean processScore(String nick, int points)
	{

		return false;
	}
	
	/**
	 * Add an entry to the highscore, removing the lowest present entry
	 * @param nick
	 * @param points
	 */
	private void addEntry(String nick, int points)
	{
		// empty
	}
	
	/**
	 * Debug method for populating the highscore table
	 *
	 */
	public void populate()
	{
		// empty
	}
}
