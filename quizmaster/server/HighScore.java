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
 * A class for calculating Highscores
 * 
 * @author reinhard
 */
public class HighScore 
{
	private Vector highscore;
	private final int MAXENTRIES = 10;
	private int lowestscore = Integer.MAX_VALUE;
	
	// For the database
	private String dbtable;
	private Connection connection;
	
	/**
	 * Constructor
	 * @param dbhost Network adress of the database
	 * @param dbname Database name
	 * @param dbtable Database table
	 * @param dbuser Database login
	 * @param dbpass Databae password
	 * @throws Exception
	 */
	public HighScore(String dbhost, String dbname, String dbtable, String dbuser, String dbpass) throws Exception
	{
		this.highscore = new Vector( MAXENTRIES );
		this.dbtable = dbtable;
		
		String dbUri="jdbc:mysql://" + dbhost + "/" + dbname + "?user=" + dbuser + "&password=" + dbpass;
		
		Class.forName("org.gjt.mm.mysql.Driver");
		connection = DriverManager.getConnection(dbUri);
		
		try{
			this.loadHighscore();
		} catch(SQLException e){
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks if a users score qualifies for a highscore
	 * @param nick Nickname of the user
	 * @param points Score of the user
	 * @return TRUE, if new highscore entry, FALSE if not
	 */
	public boolean processScore(String nick, int points)
	{
		// Only process scores above 0 points
		if(points!=0 && (points>=this.lowestscore || this.highscore.size()<MAXENTRIES))
		{
			this.addEntry(nick, points);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Add an entry to the highscore, removing the lowest present entry
	 * @param nick Nickname of the user
	 * @param points Score of the user
	 */
	private void addEntry(String nick, int points)
	{
		System.out.println("New highscore entry for "+nick+" with "+points+" points");
		Score sc = new Score(nick, points);
		
		if(this.highscore.isEmpty())
		{
			this.highscore.add(sc);
		}
		else if(this.highscore.size() < MAXENTRIES)
		{
			this.highscore.add(sc);
		}
		else
		{
			for(int i=0; i<this.highscore.size(); i++)
			{
				Score sc1 = (Score) this.highscore.elementAt(i);
				if(sc1.getScore() == this.lowestscore)
				{
					this.highscore.removeElementAt(i);
					this.highscore.add(sc);
					break;
				}
			}
		}
	
		sc=null;
		
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
	 * @throws SQLException
	 */
	public void saveHighscore() throws SQLException
	{
		System.out.println("Updating highscore database");
		Statement s = connection.createStatement();

		// Deleting all data from the database, all logic done by this class
		s.executeUpdate("delete from "+this.dbtable);

		for(int i=0; i<this.highscore.size(); i++)
		{
			Score sc = (Score) this.highscore.elementAt(i);
			String query = "insert into "+this.dbtable+" values('', '"+sc.getNick()+"', "+sc.getScore()+", '"+sc.getDate()+"')";
			s.executeUpdate(query);
		}
	}
	
	/**
	 * Method for loading Highscore data from the database
	 *
	 * @throws SQLException
	 */
	private void loadHighscore() throws SQLException
	{
		System.out.println("Reading highscore database");
		Statement s = connection.createStatement();
		ResultSet rs = s.executeQuery("select id, nick, score, date from "+this.dbtable+" order by score desc");
		
		while(rs.next())
		{
			Score sc = new Score(rs.getInt("id"), rs.getString("nick"), rs.getInt("score"), rs.getDate("date"));
			this.highscore.add(sc);
		}
		
		this.setLowestscore();
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
