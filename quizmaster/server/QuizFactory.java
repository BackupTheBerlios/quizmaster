/*
 * Created on 12.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package server;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class QuizFactory {

    public static void main (String args[]) {
        File docFile = new File("futurama.xml");

        Document doc = null;
        try {
            DocumentBuilderFactory dbf = 
                     DocumentBuilderFactory.newInstance();
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
        
        	System.out.println("The root element is " + 
                                       root.getNodeName() + ".\n");
    		
        	//iterate over question elements
        	NodeList l = root.getChildNodes();
    		for(int i=0; i<l.getLength(); i++){
    			Node n = l.item(i);
//    			Element e = (Element) n;
    			System.out.println(n.getNodeName() + ": " + n.getAttributes().getNamedItem("text"));
    		}
    }
}
