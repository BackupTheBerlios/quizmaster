/* HighScore.java
 * 
 * Created on 20.01.2005
 */
package server;

import java.util.Vector;

/**
 * @author reinhard
 *
 * A class for calculating Highscores
 */
public class HighScore 
{
	private Vector highscore;
	private final int MAXENTRIES = 10;
	private int lowestScore;
	
	/**
	 * Constructor
	 *
	 */
	public HighScore()
	{
		this.highscore = new Vector( MAXENTRIES );
		this.lowestScore = 0;
		this.loadHighscore();
	}
	
	/**
	 * Checks if a users score qualifies for a highscore 
	 * @return TRUE, if new Highscore, FALSE if not
	 */
	public boolean processScore(String nick, int points)
	{
		if(points>=this.getLowestScore())
		{
			this.addEntry(nick, points);
		}
		
		return false;
	}
	
	/**
	 * Add an entry to the highscore, removing the lowest present entry
	 * @param nick
	 * @param points
	 */
	private void addEntry(String nick, int points)
	{
		
		if(this.highscore.isEmpty())
		{
			this.highscore.add(nick);
			this.highscore.add(new Integer(points));
		}
		else
		{
			for(int i=1; i<MAXENTRIES; i=i+2)
			{
				Integer score = (Integer) this.highscore.elementAt(i);
				
				if(score.intValue()<points)
				{
					// Add nickname and points
					this.highscore.add(i-1, nick);
					this.highscore.add(i, new Integer(points));
					break;
				}
			}
		}
		
		// Remove last nickname and points if MAXENTRIES has been reached
		if(this.highscore.size()>MAXENTRIES)
		{
			this.highscore.removeElementAt(this.highscore.size()-1);
			this.highscore.removeElementAt(this.highscore.size()-1);
		}
		
		// Update lowest score
		this.setLowestScore( ((Integer) this.highscore.elementAt(this.highscore.size()-1)).intValue() );
		
		this.saveHighscore();
	}
	
	/**
	 * Method for saving Highscore data
	 *
	 */
	public void saveHighscore()
	{
		// empty
	}
	
	/**
	 * Method for loading Highscore data
	 *
	 */
	private void loadHighscore()
	{
		// empty
	}


	/**
	 * @return Returns the lowestScore.
	 */
	private int getLowestScore() {
		return lowestScore;
	}
	/**
	 * @param lowestScore The lowestScore to set.
	 */
	private void setLowestScore(int lowestScore) {
		this.lowestScore = lowestScore;
	}
}
