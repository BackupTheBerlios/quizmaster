/*
 * Created on 10.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package client;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
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
import server.QuizServices;

/**
 * @author hannes
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
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

	private JButton[] answerButtons;

	private JButton quit;

	private JScrollPane clientListPane;

	private JTextArea chatArea;

	private JTextField input;

	private JList clientList;

	/**
	 * Initialization method.
	 */
	public void init() {
		super.init();
		nickname = getParameter("nickname");

		//		 Set the codebase for this application
		//		System.setProperty("java.rmi.server.codebase",
		// "http://localhost/classes/");

		connect(getParameter("host"));
		//		 Register the client with the server
		try {
			UnicastRemoteObject.exportObject(this);
			server.register((QuizClientServices) this);
			initGUI();
		} catch (RemoteException e) {
			System.out.println("RemoteException in SimpleClient.main():");
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
		System.out.println("destroy...");
		try {
			server.unregister((QuizClientServices) this);
		} catch (RemoteException re) {
			System.out.println("RemoteException caught in destroy(): "	+ re.getMessage());
			re.printStackTrace();
		}
		super.destroy();
	}

	/**
	 * Handles all events.
	 */
	public void actionPerformed(ActionEvent e) {
		//user entered chat message
		if (e.getSource() == input) {
			if (!"".equals(input.getText())) {
				ChatMessage message = new ChatMessage(input.getText());
				message.setSender(nickname);
				try {
					server.takeMessage(message);
				} catch (Exception re) {
					System.out.println("Exception caught in actionPerformed: "	+ re.getMessage());
					re.printStackTrace();
				}
				//				apend input to textarea (replace by sendMessage call later)
				//				display(message);
				//clear input line
				input.setText("");
			}
		}

		//Quit
		if (e.getSource() == quit) {
			destroy();
		}

	}

	/**
	 * Invokes the text of a ChatMessage to be displayed in the chat area.
	 */
	public void display(ChatMessage msg) {
		chatArea.append("\n<" + msg.getSender() + "> " + msg.getBody());
		chatArea.setCaretPosition(chatArea.getDocument().getLength());
		//TODO special treatment for own messages (display those immediately)
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
	 * @see client.QuizClientServices#getNrOfGamesWon()
	 */
	public String getNrOfGamesWon() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see client.QuizClientServices#notify(messaging.QuizQuestion)
	 */
	public void display(QuizQuestion msg) throws RemoteException {
		// TODO Auto-generated method stub
		//		this.answers = msg.getAnswers();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see client.QuizClientServices#readAnswer()
	 */
	public QuizAnswer readAnswer() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see client.QuizClientServices#isQuizMode()
	 */
	public boolean isQuizMode() throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see client.QuizClientServices#setQuizMode(boolean)
	 */
	public void setQuizMode(boolean b) throws RemoteException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see client.QuizClientServices#updateScore(int)
	 */
	public void updateScore(int points) throws RemoteException {
		// TODO Auto-generated method stub

	}

	/**
	 * Displays list of connected clients in client list combo box.
	 */
	public void updateClientList(String[] clients) throws RemoteException {
		if (clientList != null) {
			clientList.setListData(clients);
		}
	}

	/**
	 * Initializes all swing components of applet.
	 * 
	 * @throws RemoteException
	 */
	private void initGUI() throws RemoteException {
		pane = getContentPane();
		pane.setLayout(new FlowLayout(FlowLayout.LEFT));

		//top panel
		top = new JPanel();
		top.setPreferredSize(new Dimension(TOTAL_OUTER_WIDTH, 330));
		top.setLayout(new FlowLayout(FlowLayout.LEFT));

		JPanel menu = new JPanel(new FlowLayout(FlowLayout.LEFT));
		menu.setPreferredSize(new Dimension(TOTAL_INNER_WIDTH, 35));
		quit = new JButton("Disconnect");
		quit.addActionListener(this);
		menu.add(quit);
		top.add(menu);

		//question
		questionLabel = new JLabel(getCurrentQuestion());
		questionLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		questionLabel.setPreferredSize(new Dimension(LEFT_INNER_WIDTH, 30));
		top.add(questionLabel);

		//answer buttons
		answerButtons = new JButton[NR_OF_ANSWERS];
		for (int i = 0; i < answerButtons.length; i++) {
			answerButtons[i] = new JButton("Answer #" + i);
			answerButtons[i].setPreferredSize(new Dimension(LEFT_INNER_WIDTH,
					30));
			top.add(answerButtons[i]);
		}

		//dev
		{
			Vector dummyAnswers = new Vector();
			for (int i = 0; i < answerButtons.length; i++) {
				dummyAnswers.add("Answer #" + i);
			}
			setAnswersToButtons(dummyAnswers);
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
	private void connect(String hostname) {
		try {
			String name = "//" + hostname + "/Quizmaster";
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

		System.out.println("Successfully connected to " + hostname);
		System.out.println("");
	}

	/**
	 * Returns current quiz question.
	 * 
	 * @return
	 */
	private String getCurrentQuestion() {
		return "What is Fry's full name?";
	}
}