/* ChatMessage.java
 * 
 * Created on 05.01.2005
 */

package messaging;

/**
 * A simple class for chat-messages
 * 
 * @author yvonne
 */
public class ChatMessage extends Message {
	
	private String text;
	private String nickname;
	
	/**
	 * Getter for nickname. 
	 * @return Returns the nickname.
	 */
	public String getNickname() {
		return nickname;
	}
	/**
	 * Sets nickname. 
	 * @param nickname The nickname to set.
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	/**
	 * Constructor which takes a String argument for the message text
	 * @param body
	 */
	public ChatMessage(String body)
	{
		this.text = body;
	}
	
	/**
	 * Gets body. 
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
