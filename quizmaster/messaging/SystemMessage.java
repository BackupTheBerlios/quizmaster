/* SystemMessage.java
 * 
 * Created on 16.01.2005
 */
package messaging;

/**
 * A simple class for SystemMessages from the server
 *
 * @author yvonne
 */
public class SystemMessage extends Message {
	
	public static final int RIGHT_ANSWER = 5;
	public static final int WRONG_ANSWER = 6;
	public static final int QUIZ_ENDED = 10;
	public static final int ENTERLEAVE = 15;
	public static final int QUIZ_DESC = 20;
	
	private int opCode;
	private String body;
	
	/**
	 * Access the body of the message
	 * @return Returns the body.
	 */
	public String getBody() {
		return body;
	}
	
	/**
	 * Set the body of the message
	 * @param body The body to set.
	 */
	public void setBody(String body) {
		this.body = body;
	}
	
	/**
	 * Access the opCode of the message
	 * @return Returns the opCode.
	 */
	public int getOpCode() {
		return opCode;
	}
	
	/**
	 * Set the opCode of the message
	 * @param opCode The opCode to set.
	 */
	public void setOpCode(int opCode) {
		this.opCode = opCode;
	}
}
