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
	private String dbtable;
	private Connection con;
	
	/**
	 * Constructor
	 * @throws Exception
	 */
	public HighScore(String dbhost, String dbname, String dbtable, String dbuser, String dbpass) throws Exception
	{
		this.highscore = new Vector( MAXENTRIES );
		this.dbtable = dbtable;
		
		String dbUri="jdbc:mysql://" + dbhost + "/" + dbname + "?user=" + dbuser + "&password=" + dbpass;
		
		Class.forName("org.gjt.mm.mysql.Driver");
		con = DriverManager.getConnection(dbUri);
		
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

		// Deleting all data from the database, all logic done by this class
		s.executeUpdate("delete from "+this.dbtable);

		for(int i=0; i<this.highscore.size(); i++)
		{
			Score sc = (Score) this.highscore.elementAt(i);
			s.executeUpdate("insert into "+this.dbtable+" values("+sc.getId()+", '"
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
		ResultSet rs = s.executeQuery("select id, nick, score from "+this.dbtable+" order by score desc");
		
		while(rs.next())
		{
			Score sc = new Score(rs.getInt("id"), rs.getString("nick"), rs.getInt("score"));
			this.highscore.add(sc);
		}
		
		this.setLowestscore();
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
