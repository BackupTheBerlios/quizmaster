/* ChatMessage.java
 * 
 * Created on 05.01.2005
 */

package messaging;

/**
 * @author reinhard
 *
 * A simple class for chat-messages
 */
public class ChatMessage extends Message {
	
	private String text;
	
	/**
	 * Constructor which takes a String argument for the message text
	 * @param body
	 */
	public ChatMessage(String body)
	{
		this.text = body;
	}
	
	/**
	 * @return Returns the message text
	 */
	public String getBody() {
		return this.text;
	}
	
	/**
	 * @param body The body to set.
	 */
	public void setBody(String body) {
		this.text = body;
	}
}
