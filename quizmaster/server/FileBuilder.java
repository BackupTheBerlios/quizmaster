/* FileBuilder.java
 * 
 * Created on 16.01.2005
 */
package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author reinhard
 *
 *
 */
public class FileBuilder {

	public static void main(String[] args) {
	
		if(args.length!=1)
		{
			System.out.println("Usage:");
			System.out.println("FileBuilder <newfile>");
			System.out.println();
			return;
		}
		
		int id=0;
		
		FileWriter fileout=null;

		try {
			fileout = new FileWriter(args[0]);
		} catch (IOException e1) {
			System.err.println(e1.getMessage());
			e1.printStackTrace();
		}

		BufferedWriter out = new BufferedWriter(fileout);
		String tmp=null;
		
		try {
			out.write("<quiz>\n");
		} catch (IOException e3) {
			e3.printStackTrace();
		}

		do {
			System.out.print("Points for this question: ");
			String question=null;
			String answer=null;
			String points=null;
			
			// We need an InputStreamReader for reading user input
			InputStreamReader reader = new InputStreamReader(System.in);
			// Wrap the reader with a buffered reader.
			BufferedReader buf_in = new BufferedReader (reader);
			
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
			
			try {
				out.write(question+"\n");
			} catch (IOException e2) {
				System.err.println(e2.getMessage());
				e2.printStackTrace();
			}

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
					answer="\t\t<answer correct=\"true\">"+tmp+"</answer>\n";
				}
				else
				{
					answer="\t\t<answer>"+tmp+"</answer>\n";
				}
				
				try {
					out.write(answer);
				} catch (IOException e2) {
					System.err.println(e2.getMessage());
					e2.printStackTrace();
				}

			}
			
			try {
				out.write("\t</question>\n");
			} catch (IOException e2) {
				System.err.println(e2.getMessage());
				e2.printStackTrace();
			}
			
			id+=1;
			
			System.out.print("More questions? [y/n] ");
			try {
				tmp = buf_in.readLine();
			} catch (IOException e) {
				System.err.println("Client IOException in SimpleClient.sendMessage()");
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
						
		} while(tmp.equals("y"));
		
		try {
			out.write("</quiz>\n");
		} catch (IOException e3) {
			e3.printStackTrace();
		}
		
		try {
			out.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	
		System.out.println();
		
	}
}
