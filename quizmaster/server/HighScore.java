/* HighScore.java
 * 
 * Created on 20.01.2005
 */
package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
	private int lowestscore = Integer.MAX_VALUE;
	
	// For the database
	private String dbHost = "localhost";
	private String dbName = "quizmaster";
	private String dbUser = "quizmaster";
	private String dbPassword = "quizpass";
	private String dbUri = "jdbc:mysql://" + dbHost + "/" + dbName + "?user=" + dbUser + "&password=" + dbPassword;
	private Connection con;
	
	/**
	 * Constructor
	 * @throws Exception
	 */
	public HighScore() throws Exception
	{
		this.highscore = new Vector( MAXENTRIES );
		
		Class.forName("org.gjt.mm.mysql.Driver");
		con = DriverManager.getConnection(this.dbUri);
		
		try{
			this.loadHighscore();
		} catch(SQLException e){
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks if a users score qualifies for a highscore 
	 * @return TRUE, if new Highscore, FALSE if not
	 */
	public boolean processScore(String nick, int points)
	{
		if(points>=this.getLowestscore())
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
			Score sc = new Score(nick, points);
			this.highscore.add(sc);
		}
		else if(this.highscore.size() < MAXENTRIES)
		{
			Score sc = new Score(nick, points);
			this.highscore.add(sc);
		}
		else
		{
			for(int i=0; i<this.highscore.size(); i++)
			{
				Score sc = (Score) this.highscore.elementAt(i);
				if(sc.getScore()< points)
				{
					this.highscore.removeElementAt(i);
					this.highscore.add(sc);
				}
			}
		}
		
		try{
			this.saveHighscore();
		} catch(SQLException e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		this.setLowestscore();
	}
	
	/**
	 * Method for saving Highscore data to the database
	 *
	 */
	public void saveHighscore() throws SQLException
	{
		Statement s = con.createStatement();
		
		for(int i=0; i<this.highscore.size(); i++)
		{
			Score sc = (Score) this.highscore.elementAt(i);
			s.executeUpdate("insert into highscore values("+sc.getId()+", '"
													+sc.getNick()+"', "+sc.getScore()+")");
		}
	}
	
	/**
	 * Method for loading Highscore data from the database
	 *
	 */
	private void loadHighscore() throws SQLException
	{
		Statement s = con.createStatement();
		ResultSet rs = s.executeQuery("select id, nick, score from highscore order by score desc");
		
		while(rs.next())
		{
			Score sc = new Score(rs.getInt("id"), rs.getString("nick"), rs.getInt("score"));
			this.highscore.add(sc);
		}
		
		this.setLowestscore();
		
		// For now, we're just deleting all data from the database, all logic done by this class
		s.executeUpdate("delete from highscore");
	}

	/**
	 * @return Returns the dbHost.
	 */
	private String getDbHost() {
		return dbHost;
	}
	
	/**
	 * @param dbHost The dbHost to set.
	 */
	private void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}
	
	/**
	 * @return Returns the dbName.
	 */
	private String getDbName() {
		return dbName;
	}
	
	/**
	 * @param dbName The dbName to set.
	 */
	private void setDbName(String dbName) {
		this.dbName = dbName;
	}
	
	/**
	 * @return Returns the dbPassword.
	 */
	private String getDbPassword() {
		return dbPassword;
	}
	
	/**
	 * @param dbPassword The dbPassword to set.
	 */
	private void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	
	/**
	 * @return Returns the dbUri.
	 */
	private String getDbUri() {
		return dbUri;
	}
	
	/**
	 * @param dbUri The dbUri to set.
	 */
	private void setDbUri(String dbUri) {
		this.dbUri = dbUri;
	}
	
	/**
	 * @return Returns the dbUser.
	 */
	private String getDbUser() {
		return dbUser;
	}
	
	/**
	 * @param dbUser The dbUser to set.
	 */
	private void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}
	
	/**
	 * @return Returns the lowestscore.
	 */
	private int getLowestscore() {
		return lowestscore;
	}
	/**
	 * Set the lowest score in the highscore
	 */
	private void setLowestscore() {
		
		if(this.highscore.size()==0)
		{
			this.lowestscore=0;
		}
		
		for(int i=0; i<this.highscore.size(); i++)
		{
			Score sc = (Score) this.highscore.elementAt(i);
			if(sc.getScore()< this.lowestscore)
			{
				this.lowestscore = sc.getScore();
			}
		}
	}
}
