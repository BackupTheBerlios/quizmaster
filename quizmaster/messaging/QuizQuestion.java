/* QuizQuestion.java
 * 
 * Created on 05.01.2005
 */
package messaging;

import java.util.Vector;

/**
 * @author reinhard
 *
 *
 */
public class QuizQuestion extends Message {
	
	private String question;
	private int correctAnswer;
	private Vector answers;
	private int points;

	/**
	 * @return Returns the points.
	 */
	public int getPoints() {
		return points;
	}
	/**
	 * @param points The points to set.
	 */
	public void setPoints(int points) {
		this.points = points;
	}
	/**
	 * @return Returns the answers.
	 */
	public Vector getAnswers() {
		return answers;
	}
	/**
	 * @param answers The answers to set.
	 */
	public void setAnswers(Vector answers) {
		this.answers = answers;
	}

	/**
	 * @return Returns the question.
	 */
	public String getQuestion() {
		return question;
	}
	/**
	 * @param question The question to set.
	 */
	public void setQuestion(String question) {
		this.question = question;
	}
	/**
	 * @return Returns the correctAnswer.
	 */
	public int getCorrectAnswer() {
		return correctAnswer;
	}
	/**
	 * @param correctAnswer The correctAnswer to set.
	 */
	public void setCorrectAnswer(int correctAnswer) {
		this.correctAnswer = correctAnswer;
	}
}
