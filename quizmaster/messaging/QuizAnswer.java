/* QuizAnswer.java
 * 
 * Created on 05.01.2005
 */
package messaging;

/**
 * @author reinhard
 *
 * A simple class for an answer to a quiz-question
 */
public class QuizAnswer extends Message {
	
	private int answer;
	private int questionid;
	
	public QuizAnswer(int answer, int questionid)
	{
		this.answer = answer;
		this.questionid = questionid;
	}

	/**
	 * @return Returns the answer.
	 */
	public int getAnswer() {
		return answer;
	}
	/**
	 * @param answer The answer to set.
	 */
	public void setAnswer(int answer) {
		this.answer = answer;
	}
}
