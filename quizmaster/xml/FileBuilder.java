/* FileBuilder.java
 * 
 * Created on 16.01.2005
 */
package xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import messaging.QuizQuestion;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import server.CliParamParser;

/**
 * @author reinhard
 *
 * A simple tool to generate quiz xml files
 */
public class FileBuilder {
	
	private static Vector recentQuestions;
	private static String filename="";
	private static String quizdesc="";
	
	/**
	 * This method checks if the specified file exists and backs up questions
	 *
	 */
	private static void checkAndBackup()
	{
		File f = new File(filename);
		if(!f.exists())
		{
			return;
		}
		
		Document doc = XMLHandler.readDocFromFile(f);
		
		if(doc==null)
		{
			System.err.println("No Document constructed from XML-File");
			System.err.println("File: "+ filename);
		}
        
		// Read the quiz description
		NodeList quiz = doc.getElementsByTagName("quiz");
		quizdesc = quiz.item(0).getAttributes().getNamedItem("description").getNodeValue();
		quiz=null;
		
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
					// HACK
					q.setCorrectAnswer(j);
				}
				
			}	
			q.setAnswers(a);
			
			recentQuestions.add(q);
		}
	}


	/**
	 * The main method does all the work
	 * @param args
	 */
	public static void main(String[] args) 
	{
		// Reading commandline arguments
		CliParamParser parser = new CliParamParser(args, ":");	
	
		if(!parser.existsParam("-file") || parser.paramCount()<1)
		{
			System.out.println("Usage:");
			System.out.println("FileBuilder -file:<newfile>");
			System.out.println();
			return;
		}
		
		filename=parser.getStringValue("-file");

		parser = null;
		
		XMLHandler xmlhandler = new XMLHandler();
		
		int id=0;
		
		recentQuestions = new Vector();
		checkAndBackup();
		
		// We need an InputStreamReader for reading user input
		InputStreamReader reader = new InputStreamReader(System.in);
		// Wrap the reader with a buffered reader.
		BufferedReader buf_in = new BufferedReader (reader);
		
		String tmp=null;
		
		xmlhandler.setDocType("1.0", "UTF-8", "quiz", "quiz.dtd"); 
		
		if(recentQuestions.size()==0)
		{
			System.out.print("Please enter a short description for this quiz: ");
			try {
				quizdesc = buf_in.readLine();
			} catch (IOException e) {
				System.err.println("Client IOException in SimpleClient.sendMessage()");
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
		
		xmlhandler.appendForSaving("<quiz description=\""+quizdesc+"\">");
		
		if(recentQuestions.size()>0)
		{
			for(int i=0; i<recentQuestions.size(); i++)
			{
				QuizQuestion theQuestion = (QuizQuestion) recentQuestions.elementAt(i);
				String question="\t<question id=\""+theQuestion.getId()+
								"\" text=\""+theQuestion.getQuestion()+"\" points=\""+
								theQuestion.getPoints()+"\">";
			
				xmlhandler.appendForSaving(question);
				
				for(int j=0; j < theQuestion.getAnswers().size(); j++)
				{
					String answer="\t\t<answer";
					if(theQuestion.getCorrectAnswer()==j)
					{
						answer+=" correct=\"true\"";
					}
					answer=answer+">"+(String) theQuestion.getAnswers().elementAt(j)+"</answer>";
					
					xmlhandler.appendForSaving(answer);
				}
				
				xmlhandler.appendForSaving("\t</question>");
				
				id+=1;
			}
		}
		

		// Reading more questions.... or not...
		System.out.print("More questions? [y/n]: ");
		try {
			tmp = buf_in.readLine();
		} catch (IOException e) {
			System.err.println("Client IOException in SimpleClient.sendMessage()");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		while(tmp.equals("y")) {
			System.out.print("\nPoints for this question: ");
			String question=null;
			String answer=null;
			String points=null;
			
			try {
				tmp = buf_in.readLine();
				
			} catch (IOException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
			
			points = tmp;
			
			System.out.print("Enter question: ");
			try {
				tmp = buf_in.readLine();
			} catch (IOException e) {
				System.err.println("Client IOException in SimpleClient.sendMessage()");
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
			question="\t<question id=\""+id+"\" text=\""+tmp+"\" points=\""+points+"\">";
			
			xmlhandler.appendForSaving(question);

			System.out.println();
			System.out.println("Please enter the correct solution first!");
			for(int i=0; i<4; i++)
			{
				System.out.print("Answer #"+(i+1)+": ");
				try {
					tmp = buf_in.readLine();
				} catch (IOException e) {
					System.err.println("Client IOException in SimpleClient.sendMessage()");
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
				
				if(i==0)
				{
					answer="\t\t<answer correct=\"true\">"+tmp+"</answer>";
				}
				else
				{
					answer="\t\t<answer>"+tmp+"</answer>";
				}
				
				xmlhandler.appendForSaving(answer);

			}
			
			xmlhandler.appendForSaving("\t</question>");
			
			id+=1;
			
			System.out.print("More questions? [y/n]: ");
			try {
				tmp = buf_in.readLine();
			} catch (IOException e) {
				System.err.println("Client IOException in SimpleClient.sendMessage()");
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		};
		
		xmlhandler.appendForSaving("</quiz>");
		
		xmlhandler.saveFile(filename);
	
		System.out.println();
		
	}
}
