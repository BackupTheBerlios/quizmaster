/* Message.java
 * 
 * Created on 05.01.2005
 */
package messaging;

import java.io.Serializable;

import client.QuizClientServices;

/**
 * All data which is passed between the clients and the server
 * has to be derived from this class
 *
 *  @author yvonne
 */
public abstract class Message implements Serializable {

	private int id;
	private QuizClientServices sender;
	
	/**
	 * Method for getting the message id
	 * @return Returns the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method for setting the message id
	 * @param id The message id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Method for getting a message's sender
	 * @return Returns the sender
	 */
	public QuizClientServices getSender() {
		return sender;
	}
	
	/**
	 * Method for setting a message's sender
	 * @param sender The sender to set
	 */
	public void setSender(QuizClientServices sender) {
		this.sender = sender;
	}
}
