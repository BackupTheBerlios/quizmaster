/* QuizServant.java
 * 
 * Created on 05.01.2005
 */
package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

import messaging.ChatMessage;
import messaging.QuizAnswer;
import messaging.QuizQuestion;
import client.QuizClientServices;

/**
 * @author reinhard
 *
 * The Servant class provides the server functionality
 */
public class QuizServant extends UnicastRemoteObject implements QuizServices {
	
	private Vector clients;
	private QuizQuestion activeQuestion;
	private volatile boolean available;

	private Quiz activeQuiz;
	private boolean quizIsActive;
	
	/**
	 * Standard constructor
	 * @throws RemoteException
	 */
	public QuizServant() throws RemoteException
	{
		super();
		this.clients = new Vector();
		this.available = true;
	}
	
	/* (non-Javadoc)
	 * @see server.QuizServices#takeAnswer()
	 */
	public synchronized void takeAnswer() throws RemoteException  
	{
		int rounds=0;	

		while(available==false)
		{
			System.out.println("Entering QuizServant.takeAnswer()");
			
			rounds+=1;
			
			System.out.println("Notifying threads");
			available=true;
			notifyAll();
			
			try {
				System.out.println("QuizServant waiting for new question");
				wait();
			} catch(InterruptedException e)
			{
				System.err.println("InterruptedException in QuizServant.takeAnswer()");
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
			
			int i=0;
			int quizClients=0;
			
			for(i=0; i<this.clients.size(); i++)
			{
				QuizClientServices client = (QuizClientServices) this.clients.elementAt(i);
				
				boolean quizMode=false;
				try{
					quizMode=client.isQuizMode();
				} catch(RemoteException e)
				{
					System.err.println("RemoteException in QuizServant.takeAnswer()");
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
				
				// Only clients in quizmode will be asked to answer!
				if(quizMode)
				{
					QuizAnswer answer=null;
					
					try {
						System.out.println("About to read an answer from client #"+(i+1));
						answer = client.readAnswer();
					} catch(RemoteException e)
					{
						System.err.println("RemoteException in QuizServant.takeAnswer()");
						System.err.println(e.getMessage());
						e.printStackTrace();
					}
					
					if(rounds > this.activeQuiz.getNumQuestions())
					{
						try {
							client.setQuizMode(false);
						} catch(RemoteException e)
						{
							System.err.println("RemoteException in QuizServant.sendQuizQuestion()");
							System.err.println(e.getMessage());
							e.printStackTrace();
						}
					}
					
					quizClients+=1;
					
					if(answer==null)
					{
						quizClients -= 1;
						System.out.println("Client #"+i+" is exiting quizmode");
						client.setQuizMode(false);
					}
					else if(answer.getAnswer() == this.activeQuestion.getCorrectAnswer())
					{
						int points = this.activeQuestion.getPoints();
						client.updateScore(points);
						
						System.out.println("Correct answer by client #"+i);
						System.out.println("Updated client's score (+"+points+")");
					}
				}
			}	
			
			System.out.println(quizClients+" more client(s) in quizmode");

			if(quizClients > 0 && rounds <= this.activeQuiz.getNumQuestions())
			{	
				
				available=false;
			}
			else
			{
				// No more clients, leaving quizmode...
				available=true;
				this.activeQuiz.setQuit(true);
			}
		}
		
		available = true;
	}
	
	/**
	 * Send all connected clients a quizquestion
	 * @param question
	 */
	public synchronized void sendQuizQuestion()
	{
		int rounds=0;
		
		System.out.println("QuizServant.sendQuizQuestion()");
		System.out.println("Variable available="+available);
		
		while(available==true)
		{
			rounds+=1;
			System.out.println("Notifying threads");
			available=false;
			notifyAll();
			
			try {
				System.out.println("Waiting for client thread");
				wait();
			} catch(InterruptedException e)
			{
				System.err.println("InterruptedException in QuizServant.sendQuizQuestion()");
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
			
			this.activeQuestion=null;
			this.activeQuestion = activeQuiz.fetchQuestion();
			
			int i=0;
			int quizClients=0;
			
			// We're running through the clients vector, here...
			for(i=0; i<this.clients.size(); i++)
			{
				QuizClientServices client = (QuizClientServices) this.clients.elementAt(i);
				
				boolean quizMode=false;
	
				try {
					quizMode = client.isQuizMode();
				} catch(RemoteException e)
				{
					System.err.println("RemoteException in QuizServant.sendQuizQuestion");
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
				
				// Only clients who want to take part receive questions!
				if(quizMode)
				{					
					try {
						client.display(this.activeQuestion);
					} catch(RemoteException e)
					{
						System.err.println("RemoteException in QuizServant.sendQuizQuestion()");
						System.err.println(e.getMessage());
						e.printStackTrace();
					}
					
					if(rounds>this.activeQuiz.getNumQuestions())
					{
						try {
							client.setQuizMode(false);
						} catch(RemoteException e)
						{
							System.err.println("RemoteException in QuizServant.sendQuizQuestion()");
							System.err.println(e.getMessage());
							e.printStackTrace();
						}
					}
					
					quizClients+=1;
				}
			}
			
			System.out.println("Displayed "+quizClients+" QuizQuestions remotely");

			if(quizClients>0 && rounds <= this.activeQuiz.getNumQuestions())
			{
				available = true;
			}
			else 
			{
				System.out.println("Quitting current quiz");
				// No more clients, quiz ends now!
				available=false;
				this.activeQuiz.setQuit(true);
			}
		}
		
		available = true;
	}
	
	/**
	 * Method to start a new quiz game
	 * @param numQuestions
	 */
	public void startGame(int numQuestions)
	{
		this.setQuizIsActive(true);
		this.available = true;
		this.activeQuiz = new Quiz(numQuestions);
		this.activeQuiz.setServant(this);
		System.out.println("Starting server quiz thread...");
		this.activeQuiz.start();
		
		Vector clientgames=new Vector();
		
		int i=0;
		int threadNo=0;
		
		for(i=0; i<clients.size(); i++)
		{
			QuizClientServices client = (QuizClientServices) clients.elementAt(i);

			boolean quizmode=false;
			
			try {
				quizmode=client.isQuizMode();
			} catch(RemoteException e)
			{
				System.err.println("RemoteException in Quiz.run()");
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
			
			if(quizmode)
			{
				System.out.println("Creating client thread");
				ClientGameLogic clientgamelogic = new ClientGameLogic(numQuestions);
				clientgamelogic.setClient(client);
				clientgamelogic.setServer(this);
				clientgames.add(clientgamelogic);
				clientgamelogic.start();
				threadNo+=1;
			}
		}
		
		this.activeQuiz.setClientgames(clientgames);
		clientgames=null;
		
		System.out.println("Started "+threadNo+" game thread(s)");
		
	}
	
	/* (non-Javadoc)
	 * @see server.QuizServices#takeMessage(null)
	 */
	public void takeMessage(ChatMessage msg)  throws RemoteException {
		
		System.out.println("Entering QuizServant.takeMessage()");
		System.out.println("msg.body: "+msg.getBody());
		System.out.println("msg.sender: "+msg.getSender());
		for(int i=0; i<this.clients.size(); i++)
		{
			System.out.println("Sending message to client #" + i + "...");
			QuizClientServices client = (QuizClientServices) this.clients.elementAt(i);
			client.display(msg);
		}

	}
	
	/* (non-Javadoc)
	 * @see server.QuizServices#register(java.lang.String)
	 */
	public void register(QuizClientServices client) throws RemoteException {
		System.out.println("Trying to register client with username "+client.getNickname() + "...");
		String nick = checkNickname(client.getNickname(), 0);
		client.setNickname(nick);
		
		this.clients.add(client);
		this.printClientsToConsole();	
		
		//send updated client list to all clients
		sendClientList();
	}
	
	private String checkNickname(String nick, int nr)throws RemoteException{
		if(nr!=0){
			nick += nr;
		}
		for(int i=0; i<clients.size();i++){
			QuizClientServices c = (QuizClientServices) clients.elementAt(i);
			if(nick.equals(c.getNickname())){
				nick = checkNickname(nick, nr+1);
				break;
			}
		}
		return nick;
	}
	
	/* (non-Javadoc)
	 * @see server.QuizServices#unregister(client.QuizClientServices)
	 */
	public void unregister(QuizClientServices client) throws RemoteException {
		System.out.println(this.clients.size() + " client(s) registered");
		System.out.println("Trying to unregister client...");
		this.clients.removeElement(client);
		System.out.println(this.clients.size() + " client(s) registered");
		this.printClientsToConsole();
		//send updated client list to all clients
		sendClientList();
	}
	
	/* DEBUG
	 * Used to print out all connected clients to the server-console
	 */
	public void printClientsToConsole()
	{
		for(int i=0; i<this.clients.size(); i++)
		{
			QuizClientServices thisclient = (QuizClientServices) this.clients.elementAt(i);
			System.out.println(thisclient.toString());
		}
	}
	
	/**
	 * @return Returns the activeQuestion.
	 */
	public QuizQuestion getActiveQuestion() {
		return activeQuestion;
	}
	/**
	 * @param activeQuestion The activeQuestion to set.
	 */
	public void setActiveQuestion(QuizQuestion activeQuestion) {
		this.activeQuestion = activeQuestion;
	}
	/**
	 * @return Returns the clients.
	 */
	public Vector getClients() {
		return clients;
	}
	
	/**
	 * @return Returns the nicknames of all clients. 
	 */
	public String[] getClientNames() throws RemoteException{
		String[] res = new String[clients.size()];
		for(int i=0; i<clients.size(); i++){
			res[i] = ((QuizClientServices) clients.elementAt(i)).getNickname();
		}
		return res;
	}
	/**
	 * @param clients The clients to set.
	 */
	public void setClients(Vector clients) {
		this.clients = clients;
	}
	
	/**
	 * Method for checking if a quiz is currently running
	 * @return
	 */
	public boolean quizIsActive() throws RemoteException
	{
		return this.quizIsActive;
	}
	
	/**
	 * Method to tell the servant that a quiz is currently running
	 * @param b
	 */
	private void setQuizIsActive(boolean b)
	{
		this.quizIsActive=b;
	}
	
	private void sendClientList() throws RemoteException{
		for(int i=0; i<clients.size(); i++){
			((QuizClientServices) clients.elementAt(0)).updateClientList(getClientNames());
		}
	}
}
