/* ClientApplet.java
 * 
 * Created on 12.01.2005
 */
package client;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import messaging.ChatMessage;
import messaging.QuizAnswer;
import messaging.QuizQuestion;
import messaging.SystemMessage;
import server.QuizServices;

/**
 * @author hannes
 * 
 * Swing-based GUI client applet for Quizmaster
 */
public class ClientApplet extends JApplet implements QuizClientServices,
		ActionListener {

	private static final int NR_OF_ANSWERS = 4;
	private static final int LEFT_INNER_WIDTH = 580;
	private static final int RIGHT_INNER_WIDTH = 190;
	private static final int TOTAL_OUTER_WIDTH = 790;
	private static final int TOTAL_INNER_WIDTH = TOTAL_OUTER_WIDTH - 10;
	private static final int BOTTOM_INNER_HEIGHT = 215;

	private String nickname;
	private Container pane;
	private JPanel top;
	private JPanel bottom;
	private JTextArea questionLabel;
	private JLabel pointsLabel;
	private JButton[] answerButtons;
	private JButton connectButton;
	private JButton startGame;
	private JScrollPane clientListPane;
	private JTextArea chatArea;
	private JTextField input;
	private JList clientList;
	
	private QuizServices server;
	private boolean quizMode;
	private boolean connected;
	private QuizQuestion currentQuestion;
	private QuizAnswer currentAnswer;
	private int score;

	/**
	 * Initialization method.
	 */
	public void init() 
	{
		super.init();
		
		nickname = getParameter("nickname");
		
		// Just to be sure...
		if(nickname==null)
		{
			nickname="testuser";
		}
		
		score = 0;
		this.quizMode=false;

		// Register the client with the server
		String host = getCodeBase().getHost();
		if(host==null){
//			host="localhost";
		}
		connect(host);
		

		try {
			UnicastRemoteObject.exportObject(this);
			server.register(this);
			initGUI();
		} catch (RemoteException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.applet.Applet#destroy()
	 */
	public void destroy() {
		disconnect();
		super.destroy();
	}

	/**
	 * Handles all events produced by the applet
	 */
	public void actionPerformed(ActionEvent e) 
	{
		//user entered chat message
		if (e.getSource() == input) {
			if (!"".equals(input.getText())) {
				ChatMessage message = new ChatMessage(input.getText());
				//message.setSender(this);
				message.setNickname(this.nickname);
				try {
					server.takeMessage(message);
				} catch (Exception re) {
					System.err.println("Exception caught in actionPerformed: "	+ re.getMessage());
					re.printStackTrace();
				}
				
				input.setText("");
			}
		}

		//Quit
		if (e.getSource() == connectButton) 
		{
			//if we're connected, disconnect
			if(this.connected){
				this.connectButton.setText("Connect");
				disconnect();
			}
			//otherwise, re-init applet
			else
			{
				init();
			}
		}
		
		//user clicks join game button
		if(e.getSource() == startGame)
		{
			if(!this.quizMode)
			{
				try{
					this.quizMode = server.joinGame(this);
					pointsLabel.setVisible(true);
					startGame.setText("Leave game");
				}catch(RemoteException re){
					System.err.println(re.getMessage());
					re.printStackTrace();
				}
			}
			else
			{
				// If client is in quizmode, we're requesting to leave the game now
				try{
					this.setQuizMode(this.server.requestLeaveGame(this));			
				} catch(RemoteException e1)
				{
					System.err.println(e1.getMessage());
					e1.printStackTrace();
				}
				
				this.currentQuestion=null;
				startGame.setText("Join game");
			}
			
			// Disable the quiz UI-elements, if quiz has ended
			if(!this.quizMode)
			{
				this.questionLabel.setVisible(false);
				
				for(int i=0; i<NR_OF_ANSWERS; i++)
				{
					this.answerButtons[i].setEnabled(false);
					this.answerButtons[i].setVisible(false);
				}
			}
			
			// Show the quiz UI-elements, if quiz has started
			if(this.quizMode)
			{
				this.questionLabel.setVisible(true);
				
				for(int i=0; i<NR_OF_ANSWERS; i++)
				{
					this.answerButtons[i].setEnabled(true);
					this.answerButtons[i].setVisible(true);
				}
			}
			
		}
		
		//check if one of the answer buttons is pressed
		List buttons =  Arrays.asList(answerButtons);
		int answerNo = buttons.indexOf(e.getSource());
		
		if(answerNo != -1 && currentQuestion!=null && this.quizMode==true)
		{			
			// Produce an answer
			this.currentAnswer = new QuizAnswer(answerNo, currentQuestion.getId(), this);
			try {
				server.addAnswer(this.currentAnswer);
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
			
			//disable all answer buttons (user can only answer once per question)
			for(int i=0; i<this.answerButtons.length; i++){
				this.answerButtons[i].setEnabled(false);
			}
		}
	}

	/**
	 * Invokes the text of a ChatMessage to be displayed in the chat area.
	 */
	public void display(ChatMessage msg) throws RemoteException
	{
		chatArea.append("\n<" + msg.getNickname() + "> " + msg.getBody());
		chatArea.setCaretPosition(chatArea.getDocument().getLength());
	}

	/*
	 * (non-Javadoc)
	 * @see client.QuizClientServices#notify(messaging.QuizQuestion)
	 */
	public void display(QuizQuestion msg) throws RemoteException 
	{
		//reset current answer
		this.currentAnswer = null;
		//store current question in internal memory
		this.currentQuestion = msg;
		
		//write question on label
		questionLabel.setText(msg.getQuestion());
		//write answers on buttons
		Vector answers = msg.getAnswers();
		for(int i=0; i<answers.size(); i++)
		{
			answerButtons[i].setText((String)answers.elementAt(i));
			answerButtons[i].setVisible(true);
			//enable buttons again
			answerButtons[i].setEnabled(true);
		}
	}


	/**
	 * Handles system messages from the server. 
	 */
	public void display(SystemMessage msg) throws RemoteException 
	{
		if(msg.getOpCode() == SystemMessage.RIGHT_ANSWER)
		{
			chatArea.append("\n<Quizmaster> " + msg.getBody());
			chatArea.setCaretPosition(chatArea.getDocument().getLength());
		}
	}
	/**
	 * Returns the applet's owner's nickname.
	 */
	public String getNickname() throws RemoteException {
		return this.nickname;
	}

	/**
	 * Setter method for nickname.
	 */
	public void setNickname(String nickname) throws RemoteException {
		this.nickname = nickname;
	}

	/*
	 * (non-Javadoc)
	 * @see client.QuizClientServices#isQuizMode()
	 */
	public boolean isQuizMode() throws RemoteException {
		return this.quizMode;
	}

	/*
	 * (non-Javadoc)
	 * @see client.QuizClientServices#setQuizMode(boolean)
	 */
	public void setQuizMode(boolean b) throws RemoteException {
		this.quizMode = b;
	}

	/*
	 * (non-Javadoc)
	 * @see client.QuizClientServices#updateScore(int)
	 */
	public void updateScore(int points) throws RemoteException {
		this.score += points;
		this.pointsLabel.setText(this.score + " points");
	}

	/*
	 *  (non-Javadoc)
	 * @see client.QuizClientServices#updateClientList(java.lang.String[])
	 */
	public void updateClientList(String[] clients) throws RemoteException 
	{
		if (clientList != null) {
			clientList.setListData(clients);
		}
	}

	/**
	 * Notifies the client that the current quiz has ended. 
	 */
	public void gameEnded()
	{
		try {
			System.out.println("gameEnded(), client" + this.getNickname());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		this.quizMode=false;
	}
	
	/**
	 * Initializes all swing components of applet.
	 * 
	 * @throws RemoteException
	 */
	private void initGUI() throws RemoteException 
	{
		pane = getContentPane();
		pane.setLayout(new FlowLayout(FlowLayout.LEFT));

		//top panel
		top = new JPanel();
		top.setPreferredSize(new Dimension(TOTAL_OUTER_WIDTH, 330));
		top.setLayout(new FlowLayout(FlowLayout.LEFT));

		JPanel menu = new JPanel(new FlowLayout(FlowLayout.LEFT));
		menu.setPreferredSize(new Dimension(TOTAL_INNER_WIDTH, 35));
		
		connectButton = new JButton("Disconnect");
		connectButton.addActionListener(this);
		menu.add(connectButton);
		startGame = new JButton("Join game");
		startGame.addActionListener(this);
		menu.add(startGame);		
		
		//spacer
		JLabel spacer = new JLabel();
		spacer.setPreferredSize(new Dimension(450, 10));
		menu.add(spacer);
		
		//points display
		this.pointsLabel = new JLabel(this.score + " points");
		this.pointsLabel.setVisible(false);

		menu.add(pointsLabel);

		top.add(menu);

		//question
		questionLabel = new JTextArea();
		questionLabel.setBackground(null);
		questionLabel.setAlignmentX(JTextArea.CENTER_ALIGNMENT);
		questionLabel.setLineWrap(true);
		questionLabel.setWrapStyleWord(true);
		questionLabel.setPreferredSize(new Dimension(LEFT_INNER_WIDTH, 45));
		top.add(questionLabel);
		
		//answer buttons
		answerButtons = new JButton[NR_OF_ANSWERS];
		for (int i = 0; i < answerButtons.length; i++) {
			answerButtons[i] = new JButton("");
			answerButtons[i].setPreferredSize(new Dimension(LEFT_INNER_WIDTH, 30));
			answerButtons[i].addActionListener(this);
			
			//make buttons invisible at first
			answerButtons[i].setVisible(false);
			top.add(answerButtons[i]);
		}

		pane.add(top);

		//bottom panel
		bottom = new JPanel();
		bottom.setPreferredSize(new Dimension(TOTAL_OUTER_WIDTH, 250));

		//chat window
		chatArea = new JTextArea();
		chatArea.setEditable(false);
		chatArea.setLineWrap(true);
		chatArea.setWrapStyleWord(true);
		JScrollPane scrollPane = new JScrollPane(chatArea);
		scrollPane.setPreferredSize(new Dimension(LEFT_INNER_WIDTH,
				BOTTOM_INNER_HEIGHT));
		bottom.add(scrollPane);

		clientList = new JList(server.getClientNames());
		clientList.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		clientListPane = new JScrollPane(clientList);
		clientListPane.setPreferredSize(new Dimension(RIGHT_INNER_WIDTH,
				BOTTOM_INNER_HEIGHT));
		bottom.add(clientListPane);

		//input field
		input = new JTextField();
		input.setPreferredSize(new Dimension(TOTAL_INNER_WIDTH, 20));
		input.setAlignmentY(JComponent.BOTTOM_ALIGNMENT);
		input.addActionListener(this);

		bottom.add(input);
		pane.add(bottom);
		input.requestFocus();
	}

	/**
	 * Method to connect to the RMI server application
	 * @param hostname
	 */
	private void connect(String hostname) 
	{
		try {
			System.out.println("trying to connect to " + hostname + "...");
			String name = "rmi://" + hostname + "/Quizmaster";
			this.server = (QuizServices) Naming.lookup(name);
			this.connected = true;
			System.out.println("Successfully connected to " + hostname);
			System.out.println("");
		} catch (RemoteException e) {
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
	}
	
	/**
	 * Disconnects from server.
	 */
	private void disconnect()
	{
		try {
			server.unregister(this);
			UnicastRemoteObject.unexportObject(this, true);
			this.connected = false;
		} catch (NoSuchObjectException e) 
		{
			e.printStackTrace();
		} catch (RemoteException e) 
		{
			e.printStackTrace();
		}
	}
}