/* Score.java
 * 
 * Created on 26.01.2005
 */
package server;

/**
 * @author reinhard
 *
 *
 */
public class Score
{
	private int id;
	private String nick;
	private int score;
	
	/**
	 * Constructor
	 * @param id Id of this score
	 * @param nick Nickname of this score
	 * @param score score of this score
	 */
	public Score(int id, String nick, int score)
	{
		this.id=id;
		this.nick=nick;
		this.score=score;
	}

	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @param id The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * @return Returns the nick.
	 */
	public String getNick() {
		return nick;
	}
	
	/**
	 * @param nick The nick to set.
	 */
	public void setNick(String nick) {
		this.nick = nick;
	}
	
	/**
	 * @return Returns the score.
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * @param score The score to set.
	 */
	public void setScore(int score) {
		this.score = score;
	}
}
