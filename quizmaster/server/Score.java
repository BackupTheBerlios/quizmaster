/* Score.java
 * 
 * Created on 26.01.2005
 */
package server;

/**
 * A class for use with the highscore feature
 * 
 * @author reinhard
 */
public class Score
{
	private int id;
	private String nick;
	private int score;
	private static int counter;
	
	/**
	 * Constructor, which sets the id automatically
	 * @param nick
	 * @param score
	 */
	public Score(String nick, int score)
	{
		this.id = Score.counter;
		this.nick = nick;
		this.score = score;
		
		Score.counter++;
	}
	
	/**
	 * Constructor
	 * @param id Id of this score
	 * @param nick Nickname of this score
	 * @param score score of this score
	 */
	public Score(int id, String nick, int score)
	{
		this.id=id;
		Score.counter++;
		this.nick=nick;
		this.score=score;
	}

	/**
	 * Access score id
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Set score id
	 * @param id The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Access nickname of score object
	 * @return Returns the nick.
	 */
	public String getNick() {
		return nick;
	}
	
	/**
	 * Set nickname of score object
	 * @param nick The nick to set.
	 */
	public void setNick(String nick) {
		this.nick = nick;
	}
	
	/**
	 * Access the score value
	 * @return Returns the score.
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * Set the score value
	 * @param score The score to set.
	 */
	public void setScore(int score) {
		this.score = score;
	}
}
