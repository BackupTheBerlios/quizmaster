/* Message.java
 * 
 * Created on 05.01.2005
 */
package messaging;

import java.io.Serializable;

import client.QuizClientServices;

/**
 * @author reinhard
 *
 * All data which is passed between the clients and the server
 * has to be derived from this class
 */
public abstract class Message implements Serializable {

	private int id;
	private QuizClientServices sender;
	
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
	 * @return Returns the sender.
	 */
	public QuizClientServices getSender() {
		return sender;
	}
	/**
	 * @param sender The sender to set.
	 */
	public void setSender(QuizClientServices sender) {
		this.sender = sender;
	}
}
