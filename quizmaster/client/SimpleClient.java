/* SimpleClient.java
 * 
 * Created on 06.01.2005
 */
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import messaging.ChatMessage;
import messaging.QuizAnswer;
import messaging.QuizQuestion;
import server.QuizServices;

/**
 * @author reinhard
 *
 * A Debug class for testing server features
 */
public class SimpleClient implements QuizClientServices{
	
	private static QuizServices server=null;
	private boolean quizMode=false;
	
	private int score;
	private String nickname;
	
	private boolean questionToAnswer;
	private QuizQuestion currentQuestion;

	/**
	 * Default Constructor
	 *
	 */
	public SimpleClient() throws RemoteException
	{
		try {
			UnicastRemoteObject.exportObject(this);
		} catch(RemoteException e)
		{
			System.err.println("RemoteException in SimpleClient():");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		this.setScore(0);
		this.setNickname("reini");
		this.setQuizMode(false);
	}
	/* (non-Javadoc)
	 * @see client.QuizClientServices#notify(messaging.Message)
	 */
	public void display(ChatMessage msg) 
	{
		System.out.println();
		System.out.println(msg.getBody());
	}
	
	/**
	 * Method for displaying a quizquestion the servant sent
	 * 
	 */
	public void display(QuizQuestion question) 
	{
		this.questionToAnswer=true;
		this.currentQuestion = question;
		
		System.out.println("About to display QuizQuestion...");
		
		System.out.println();
		System.out.println(question.getQuestion());
		
		for(int i=0; i<question.getAnswers().size(); i++)
		{
			System.out.println((i+1)+". " +question.getAnswers().elementAt(i));
		}
		
		QuizAnswer answer = this.readAnswer();
		
		if(answer==null)
		{
			try {
				this.setQuizMode(server.requestLeaveGame(this));
			} catch(RemoteException e)
			{
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
		else {
			try {
				server.addAnswer(answer);
			} catch(RemoteException e)
			{
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method for reading the answer from the user
	 * @param questionid ID of the corresponding question
	 * @return Answer object
	 */
	public QuizAnswer readAnswer()
	{
		String message = null;
		
		System.out.print("Answer: ");
		
		// We need an InputStreamReader for reading user input
		InputStreamReader reader = new InputStreamReader(System.in);
		// Wrap the reader with a buffered reader.
		BufferedReader buf_in = new BufferedReader (reader);
		
		try {
			message = buf_in.readLine();
		} catch (IOException e) {
			System.err.println("Client IOException in SimpleClient.sendMessage()");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		int answer = -1;
		try {
			answer = Integer.parseInt(message);
		} catch(NumberFormatException e)
		{
			if(message.equals("exit"))
			{
				return null;
			}
			
			System.out.println("Invalid answer, only numbers allowed");
			return new QuizAnswer(0, this.currentQuestion.getId(), this);
		}
		
		return new QuizAnswer(answer, this.currentQuestion.getId(), this);
	}
	
	/**
	 * Method to update the client's score
	 * @param points These get added to the current client's score
	 */
	public void updateScore(int points) throws RemoteException
	{
		this.setScore(this.getScore()+points);
	}
	
	/**
	 * Read a message from the standard input
	 * @return
	 */
	public ChatMessage readMessage()
	{
		String message=null;
	
		System.out.print("Enter message: ");
		
		// We need an InputStreamReader for reading user input
		InputStreamReader reader = new InputStreamReader(System.in);
		// Wrap the reader with a buffered reader.
		BufferedReader buf_in = new BufferedReader (reader);
		
		try {
			message = buf_in.readLine();
		} catch (IOException e) {
			System.err.println("Client IOException in SimpleClient.sendMessage():");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		ChatMessage chatmsg = new ChatMessage(message);
		chatmsg.setSender(this);
		return chatmsg;
	}
	
	/**
	 * The main method used to start up the application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Set the codebase for this application
		System.setProperty("java.rmi.server.codebase", "http://localhost/classes/");
		
		SimpleClient client=null;
		try {
			client = new SimpleClient();
		} catch(RemoteException e)
		{
			System.err.println("RemoteException in SimpleClient.main()");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		// Connect to localhost for testing
		client.connect("localhost");
		
		// Register the client with the server
		try {
			server.register((QuizClientServices) client);
		} catch(RemoteException e)
		{
			System.err.println("RemoteException in SimpleClient.main():");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		do
		{
			if(!client.isQuizMode())
			{
				switch(client.gui())
				{
					case '1':
						
						ChatMessage message = client.readMessage();
						try {
							server.takeMessage(message);
						} catch(RemoteException e)
						{
							System.err.println("RemoteException in SimpleClient.main()");
							System.err.println(e.getMessage());
							e.printStackTrace();
						}
						break;
						
					case '2':
						
						try {
							client.setQuizMode(server.joinGame(client));
						} catch(RemoteException e)
						{
							System.err.println(e.getMessage());
							e.printStackTrace();
						}
						
						break;
						
					case '3':
						
						
			
		
						break;
						
					case 'k':
						try
						{
							server.killCheckerThread();
						} catch (RemoteException e)
						{
							System.err.println(e.getMessage());
							e.printStackTrace();
						}
						break;
				
					case '0':
						
						try {
							server.unregister((QuizClientServices) client);
						} catch(RemoteException e)
						{
							System.err.println("RemoteException in SimpleClient.main():");
							System.err.println(e.getMessage());
							e.printStackTrace();
						}
						
						try {
							UnicastRemoteObject.unexportObject(client, true);
						} catch(NoSuchObjectException e)
						{
							System.err.println("NoSuchObjectException in SimpleClient.main()");
							System.err.println(e.getMessage());
							e.printStackTrace();
						}
						
						
						System.exit(0);
						break;
						
					default:
						System.out.println("Invalid option!");
						break;
				}
			}
						
		} while(true);
	}
	
	/**
	 * Connection method
	 * @param hostname
	 */
	private void connect(String hostname)
	{
		try {
			String name = "//" + hostname + "/Quizmaster";
			server = (QuizServices) Naming.lookup(name);
		} catch(RemoteException e)
		{
			System.err.println("Client RemoteException in connect(): ");
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (MalformedURLException e) {
			System.err.println("Client MalformedURLException in connect(): ");
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.err.println("Client NotBoundException in connect(): ");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		System.out.println("Successfully connected to " + hostname);
		System.out.println("");
	}
	/**
	 * @return Returns the server.
	 */
	public QuizServices getServer() {
		return server;
	}
	
	/**
	 * The user interface of the testing client
	 * @return
	 */
	public char gui()
	{
		char option=' ';
		
		System.out.println("SimpleClient Server testing");
		System.out.println();
		System.out.println("1. Send Message to all");
		System.out.println("2. Join Quiz");
		System.out.println("3. Leave Quiz");
		System.out.println("0. Exit");
		System.out.print  (">  ");
		
		// We need an InputStreamReader for reading user input
		InputStreamReader reader = new InputStreamReader(System.in);
		
		// Wrap the reader with a buffered reader.
		BufferedReader buf_in = new BufferedReader (reader);
		try {
			option = (char) buf_in.read();
		} catch (IOException e) {
			System.err.println("Client IOException in gui():");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return option;
	}
	/* (non-Javadoc)
	 * @see client.QuizClientServices#getNickname()
	 */
	public String getNickname() throws RemoteException 
	{
		return this.nickname;
	}
	/* (non-Javadoc)
	 * @see client.QuizClientServices#setNickname(java.lang.String)
	 */
	public void setNickname(String nickname) throws RemoteException 
	{
		this.nickname = nickname;
	}
	/* (non-Javadoc)
	 * @see client.QuizClientServices#getNrOfGamesWon()
	 */
	public String getNrOfGamesWon() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * @return Returns the quizMode.
	 */
	public boolean isQuizMode() {
		return quizMode;
	}
	/**
	 * @param b The quizMode to set.
	 */
	public void setQuizMode(boolean b) {
		this.quizMode = b;
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

	/* (non-Javadoc)
	 * @see client.QuizClientServices#updateClientList(java.lang.String[])
	 */
	public void updateClientList(String[] clients) throws RemoteException {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 
	 */
	public void gameEnded() throws RemoteException
	{
		this.quizMode=false;
	}
	
	/**
	 * @return Returns the questionToAnswer.
	 */
	public boolean isQuestionToAnswer() {
		return questionToAnswer;
	}
}
