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
	
	private String body;
	
	/**
	 * Constructor which takes a String argument for the body
	 * @param body
	 */
	public ChatMessage(String body)
	{
		this.body = body;
	}
	
	
	/**
	 * @return Returns the body.
	 */
	public String getBody() {
		return body;
	}
	/**
	 * @param body The body to set.
	 */
	public void setBody(String body) {
		this.body = body;
	}
}
