/* Message.java
 * 
 * Created on 05.01.2005
 */
package messaging;

import java.io.Serializable;

/**
 * @author reinhard
 *
 * All data which is passed between the clients and the server
 * has to be derived from this class
 */
public abstract class Message implements Serializable {

	private int id;
	private String sender;
	
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
	public String getSender() {
		return sender;
	}
	/**
	 * @param sender The sender to set.
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}
}
