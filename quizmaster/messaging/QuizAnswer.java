/* QuizAnswer.java
 * 
 * Created on 05.01.2005
 */
package messaging;

import client.QuizClientServices;

/**
 * @author reinhard
 *
 * A simple class for an answer to a quiz-question
 */
public class QuizAnswer extends Message {
	
	private int answer;
	private int questionid;
	
	/**
	 * Constructor
	 * @param answer The answer of the sender
	 * @param questionid The id of the question this object is related to
	 * @param client The sender of this answer
	 */
	public QuizAnswer(int answer, int questionid, QuizClientServices client)
	{
		this.answer = answer;
		this.questionid = questionid;
		this.setSender(client);
	}

	/**
	 * @return Returns the answer
	 */
	public int getAnswer() {
		return answer;
	}
	
	/**
	 * @param answer The answer to set
	 */
	public void setAnswer(int answer) {
		this.answer = answer;
	}
	
	/**
	 * @return The id of the question the answer is related to
	 */
	public int getQuestionId()
	{
		return this.questionid;
	}
}
