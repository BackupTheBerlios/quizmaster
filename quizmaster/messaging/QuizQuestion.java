/* QuizQuestion.java
 * 
 * Created on 05.01.2005
 */
package messaging;

import java.util.Vector;

/**
 * @author reinhard
 *
 * A simple class for QuizQuestions
 */
public class QuizQuestion extends Message {
	
	private String question;
	private int correctAnswer;
	private Vector answers;
	private int points;
	
	/**
	 * Constructor
	 * @param question The text of the question
	 */
	public QuizQuestion(String question)
	{
		this.question = question;
	}

	/**
	 * @return Returns the points.
	 */
	public int getPoints()
	{
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
	 * Return the answers to this question as a string array
	 * @return
	 */
	public String[] getAnswersAsArray()
	{
		String[] array = new String[4];
		
		for(int i=0; i<this.answers.size(); i++)
		{
			array[i] = (String) this.answers.elementAt(i);
		}
		
		return array;
	}
	
	/**
	 * Set a questions answer from a string array
	 * @param array
	 */
	public void setAnswersFromArray(String[] array)
	{
		Vector v = new Vector();
		
		for(int i=0; i<this.answers.size(); i++)
		{
			v.add(array[i]);
		}
		
		this.answers = v;
		
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
	 * Get the correct answer text
	 * @return
	 */
	public String getCorrectAnswerText()
	{
		return (String) this.answers.elementAt(this.correctAnswer);
	}
	
	/**
	 * @param correctAnswer The correctAnswer to set.
	 */
	public void setCorrectAnswer(int correctAnswer) {
		this.correctAnswer = correctAnswer;
	}
}
