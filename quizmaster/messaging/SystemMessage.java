/*
 * Created on 16.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package messaging;

/**
 * @author hannes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SystemMessage extends Message {
	
	public static final int RIGHT_ANSWER = 5;
	public static final int WRONG_ANSWER = 6;
	public static final int QUIZ_ENDED = 10;
	
	private int opCode;
	private String body;
	
	
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
	/**
	 * @return Returns the opCode.
	 */
	public int getOpCode() {
		return opCode;
	}
	/**
	 * @param opCode The opCode to set.
	 */
	public void setOpCode(int opCode) {
		this.opCode = opCode;
	}
}
