/* QuizQuestionFactory.java
 * 
 * Created on 12.01.2005
 */
package xml;

import java.io.File;
import java.util.Vector;

import messaging.QuizQuestion;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class for reading quiz questions
 * 
 * @author hannes
 */
public class QuizQuestionFactory {
	
	private Vector questions;
	private String quizDesc;
	private String filename;
	
	/**
	 * Constructor which takes the filename to use as an argument
	 * @param filename
	 */
	public QuizQuestionFactory(String filename)
	{
		this.questions = new Vector();
		if(filename!=null)
		{
			this.filename = filename;
		}
		else
		{
			System.out.println("QuizQuestionFactory: No filename specified for reading questions!\n");
			System.exit(-1);
		}
	}
	
	/**
	 * Method to read the questions from an xml file
	 *
	 */
	public void readQuestions()
	{
		File f = new File(this.filename);
		
		Document doc = XMLHandler.readDocFromFile(f);
		
		if(doc==null)
		{
			System.err.println("No Document constructed from XML-File");
			System.err.println("File: "+ this.filename);
		}
		
		// Get quiz description
		NodeList quiz = doc.getElementsByTagName("quiz");
		this.quizDesc = quiz.item(0).getAttributes().getNamedItem("description").getNodeValue();
		quiz=null;
        
        // Iterate over question elements
    		NodeList questions = doc.getElementsByTagName("question");
		
    		Vector qs = new Vector();
    		int i=0;
    		
    		for(i=0; i<questions.getLength(); i++)
		{
			Node question = questions.item(i);
			
			int questionID = Integer.parseInt(question.getAttributes().getNamedItem("id").getNodeValue());
			
			String questionText =  question.getAttributes().getNamedItem("text").getNodeValue();

			NodeList answers = ((Element)question).getElementsByTagName("answer");
			
			Node[] anArray = new Node[4];
			
			for(int k=0; k<answers.getLength(); k++)
			{
				anArray[k]=answers.item(k);
			}
			
			anArray = (Node[]) QuizQuestionFactory.shuffle(anArray);
			
			int points = Integer.parseInt(question.getAttributes().getNamedItem("points").getNodeValue());
			
			QuizQuestion q = new QuizQuestion(questionText);
			q.setId(questionID);
			q.setPoints(points);

			Vector a = new Vector();	
			
			//Iterate over answer elements
			for(int j=0; j<anArray.length; j++)
			{				
				boolean correct = false;
				Node answer = anArray[j];
				Node correctNode = answer.getAttributes().getNamedItem("correct");
				
				if(correctNode!=null)
				{
					correct = Boolean.valueOf(correctNode.getNodeValue()).booleanValue();
				}
				
				String ans = answer.getFirstChild().getNodeValue();
				
				a.add(ans);

				if(correct)
				{
					// QUICK HACK
					q.setCorrectAnswer(j);
				}
				
			}	
			q.setAnswers(a);
			
			qs.add(q);
		}
    		
    		this.questions=QuizQuestionFactory.mixQuestions(qs);
    		
		System.out.println(i+" questions read from file");
	}
	
	/**
	 * @return Returns the filename.
	 */
	public String getFilename() {
		return filename;
	}
	
	/**
	 * @param filename The filename to set.
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	/**
	 * @return Returns the questions.
	 */
	public Vector getQuestions() {
		return questions;
	}
	
	/**
	 * @param questions The questions to set.
	 */
	public void setQuestions(Vector questions) {
		this.questions = questions;
	}
	
	/**
	 * @return Returns the quizDesc.
	 */
	public String getQuizDesc() {
		return quizDesc;
	}
	
	/**
	 * Class method which takes a vector of questions and shuffles it
	 * @param q
	 * @return Newly sorted vector
	 */
	public static Vector mixQuestions(Vector q)
	{
		int i=0;
		
		// We want a different question order each game, so once again, we're shuffling
		QuizQuestion[] questionArray = new QuizQuestion[q.size()];
		
		for(i=0; i<q.size(); i++)
		{
			questionArray[i] = (QuizQuestion) q.elementAt(i);
		}
		
		// Shuffle questions
		questionArray = (QuizQuestion[]) shuffle(questionArray);
		
		q = new Vector();
		
		// Now we're adding all read questions
		for (i=0; i<questionArray.length; i++)
		{
			QuizQuestion thisQuestion = questionArray[i];
			String correct = thisQuestion.getCorrectAnswerText();
			
			String[] answers = thisQuestion.getAnswersAsArray();
			
			// Shuffle answers
			answers = (String[]) shuffle(answers);
			
			int newCorrectIndex=0;
			
			// Update new correct index
			for(newCorrectIndex=0; newCorrectIndex<answers.length; newCorrectIndex++)
			{
				String s = answers[newCorrectIndex];
				
				if(s.equals(correct))
				{
					break;
				}
			}
			
			thisQuestion.setAnswersFromArray(answers);
			thisQuestion.setCorrectAnswer(newCorrectIndex);
			
			q.add(thisQuestion);
			
		}
		
		return q;
	}
	
	/**
	 * Method to shuffle an array
	 * @param array
	 */
	private static Object[] shuffle(Object[] array) 
	{
		for (int lastPlace = array.length-1; lastPlace > 0; lastPlace--) 
		{
			// Choose a random location from among 0,1,...,lastPlace.
			int randLoc = (int)(Math.random()*(lastPlace+1));
			// Swap items in locations randLoc and lastPlace.
			Object temp = array[randLoc];
			array[randLoc] = array[lastPlace];
			array[lastPlace] = temp;
		}
		
		return array;
	}
}
