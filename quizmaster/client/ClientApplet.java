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
 * Swing-based GUI client applet for Quizmaster, a client-server quiz game application. 
 */
public class ClientApplet extends JApplet implements QuizClientServices,
		ActionListener {

	private static final int NR_OF_ANSWERS = 4;

	private static final int LEFT_INNER_WIDTH = 580;

	private static final int RIGHT_INNER_WIDTH = 190;

	private static final int TOTAL_OUTER_WIDTH = 790;

	private static final int TOTAL_INNER_WIDTH = TOTAL_OUTER_WIDTH - 10;

	private static final int BOTTOM_INNER_HEIGHT = 215;

	private QuizServices server;

	private Vector clients;

	private String[] answers;

	private String nickname;

	private Container pane;

	private JPanel top;

	private JPanel bottom;

	private JLabel questionLabel;
	
	private JLabel pointsLabel;

	private JButton[] answerButtons;

	private JButton connectButton;
	
	private JButton startGame;

	private JScrollPane clientListPane;

	private JTextArea chatArea;

	private JTextField input;

	private JList clientList;
	
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
		score = 0;

		// Register the client with the server
		connect(getCodeBase().getHost());

		try {
			UnicastRemoteObject.exportObject(this);
			server.register((QuizClientServices) this);
			initGUI();
		} catch (RemoteException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.applet.Applet#destroy()
	 */
	public void destroy() {
		disconnect();
		super.destroy();
	}

	/**
	 * Handles all events.
	 */
	public void actionPerformed(ActionEvent e) 
	{
		//user entered chat message
		if (e.getSource() == input) {
			if (!"".equals(input.getText())) {
				ChatMessage message = new ChatMessage(input.getText());
				message.setSender(this);
				
				try {
					server.takeMessage(message);
				} catch (Exception re) {
					System.out.println("Exception caught in actionPerformed: "	+ re.getMessage());
					re.printStackTrace();
				}
				
				//clear input line
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
			else{
				System.out.println("initializing...");
				init();
			}
		}
		
		//user clicks join game button
		if(e.getSource() == startGame)
		{
			try{
				server.joinGame(this);
				pointsLabel.setVisible(true);
				startGame.setEnabled(false);
			}catch(RemoteException re){
				System.err.println(re.getMessage());
				re.printStackTrace();
			}
		}
		
		//check if one of the answer buttons is pressed
		List buttons =  Arrays.asList(answerButtons);
		int answerNo = buttons.indexOf(e.getSource());
		if(answerNo != -1 && currentQuestion != null)
		{
			//set current answer so that server can fetch it later
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
	public void display(ChatMessage msg){
		try {
			chatArea.append("\n<" + msg.getSender().getNickname() + "> " + msg.getBody());
			chatArea.setCaretPosition(chatArea.getDocument().getLength());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
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
	 * Handles system messages. 
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
	 * Setter for nickname.
	 */
	public void setNickname(String nickname) throws RemoteException {
		this.nickname = nickname;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see client.QuizClientServices#readAnswer()
	 */
	public QuizAnswer readAnswer() throws RemoteException {
		System.out.println("readAnswer on " + this.getNickname() + " invoked.");
		return this.currentAnswer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see client.QuizClientServices#isQuizMode()
	 */
	public boolean isQuizMode() throws RemoteException {
		return this.quizMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see client.QuizClientServices#setQuizMode(boolean)
	 */
	public void setQuizMode(boolean b) throws RemoteException {
		this.quizMode = b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see client.QuizClientServices#updateScore(int)
	 */
	public void updateScore(int points) throws RemoteException {
		this.score += points;
		this.pointsLabel.setText(this.score + " points");
	}

	/**
	 * Displays list of connected clients in client list combo box.
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
		//Quiz has ended. Make join game button clickable again. 
		this.startGame.setEnabled(true);
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
		startGame = new JButton("Start/Join Quiz");
		startGame.addActionListener(this);
		if(server.isActiveQuiz()){
			startGame.setVisible(false);
		}
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
		questionLabel = new JLabel();
		questionLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		questionLabel.setPreferredSize(new Dimension(LEFT_INNER_WIDTH, 30));
		top.add(questionLabel);
		
		//answer buttons
		answerButtons = new JButton[NR_OF_ANSWERS];
		for (int i = 0; i < answerButtons.length; i++) {
			answerButtons[i] = new JButton("Answer #" + i);
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
	 * Sign the buttons with the quiz answers
	 * @param v Vector of answers
	 */
	private void setAnswersToButtons(Vector v) {
		for (int i = 0; i < v.size(); i++) {
			answerButtons[i].setText((String) v.elementAt(i));
		}
	}

	/**
	 * Connection method.
	 * 
	 * @param hostname
	 */
	private void connect(String hostname) 
	{
		try {
			String name = "rmi://" + hostname + "/Quizmaster";
			this.server = (QuizServices) Naming.lookup(name);
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
		this.connected = true;
		System.out.println("Successfully connected to " + hostname);
		System.out.println("");
	}
	
	/**
	 * Disconnects from server.
	 */
	private void disconnect()
	{
		try {
			server.unregister((QuizClientServices) this);
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