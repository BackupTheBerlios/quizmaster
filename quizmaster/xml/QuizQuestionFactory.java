/*
 * Created on 12.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package xml;

import java.io.File;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import messaging.QuizQuestion;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class QuizQuestionFactory {
	
	private Vector questions;
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
		Document doc = null;
		
		System.out.println("Reading question from file: "+this.filename);
		File docFile = new File( this.filename );

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(docFile);
        } catch (java.io.IOException e) {
            System.out.println("Can't find the file");
        } catch (Exception e) {
            System.out.println("Problem parsing the file.");
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }
        
        // Iterate over question elements
    		NodeList questions = doc.getElementsByTagName("question");
		
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
			
			anArray = this.shuffle(anArray);
			
			int points = Integer.parseInt(question.getAttributes().getNamedItem("points").getNodeValue());
			
			QuizQuestion q = new QuizQuestion(questionText);
			q.setId(questionID);
			q.setPoints(points);
			
			Vector a = new Vector();	
			String[] antworten = new String[4];
			
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
					// HACK
					q.setCorrectAnswer(j+1);
				}
				
			}	
			q.setAnswers(a);
			
			this.questions.add(q);
		}
        
		System.out.println(i+" questions read from file");
	}
	
	/*
	 * Debug method for testing the results of the shuffle method...
	 */
	public static void printOutReadQuestion(QuizQuestion q)
	{
		System.out.println(q.getQuestion());
		
		for(int i=0; i<4; i++)
		{
			System.out.println((String)q.getAnswers().elementAt(i));
		}
		
		System.out.println("Correct Answer: #"+q.getCorrectAnswer());
		System.out.println();
	}
	
	/**
	 * Method to shuffle an array
	 * @param array
	 */
	private Node[] shuffle(Node[] array) 
	{
		for (int lastPlace = array.length-1; lastPlace > 0; lastPlace--) 
		{
			// Choose a random location from among 0,1,...,lastPlace.
			int randLoc = (int)(Math.random()*(lastPlace+1));
			// Swap items in locations randLoc and lastPlace.
			Node temp = array[randLoc];
			array[randLoc] = array[lastPlace];
			array[lastPlace] = temp;
		}
		
		return array;
	}

	
	/**
	 * Method for accessing a specific quiz question
	 * @param number
	 * @return
	 */
	public QuizQuestion getQuestionNr(int number)
	{
		return (QuizQuestion) this.questions.elementAt(number);
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
}
