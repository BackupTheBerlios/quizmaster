/*
 * Created on 12.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package server;

import java.io.File;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import messaging.QuizQuestion;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class QuizFactory {

    public static void main (String args[]) {
        File docFile = new File("futurama.xml");

        Document doc = null;
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

        Element root = doc.getDocumentElement();
    		
        	//iterate over question elements
        	NodeList questions = doc.getElementsByTagName("question");
    		for(int i=0; i<questions.getLength(); i++){
    			Node question = questions.item(i);
    			String questionText =  question.getAttributes().getNamedItem("text").getNodeValue();
    			System.out.println("\nQuestion: " + questionText);
    			NodeList answers = ((Element)question).getElementsByTagName("answer");
    			
    			QuizQuestion q = new QuizQuestion(questionText);
    			Vector v = new Vector();
    			//iterate over answer elements
    			for(int j=0; j<answers.getLength(); j++){
    				boolean correct = false;
    				Node answer = answers.item(j);
    				Node correctNode = answer.getAttributes().getNamedItem("correct");
    				if(correctNode!=null){
					correct = Boolean.valueOf(correctNode.getNodeValue()).booleanValue();
    				}
    				String answerText = answer.getFirstChild().getNodeValue();
    				v.add(answerText);
    				if(correct){
    					q.setCorrectAnswer(j);
    				}
    				System.out.println("answer: " + answer.getFirstChild().getNodeValue() + ", " + correct);
    			}
    		}
    }
}
