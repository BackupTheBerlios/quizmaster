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
	private String nickname;
	
	/**
	 * @return Returns the nickname.
	 */
	public String getNickname() {
		return nickname;
	}
	/**
	 * @param nickname The nickname to set.
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	/**
	 * @return Returns the text.
	 */
	public String getText() {
		return text;
	}
	/**
	 * @param text The text to set.
	 */
	public void setText(String text) {
		this.text = text;
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
