package it.polito.lastfm.query;

import java.io.*;

public class Main {
	static public void main(String argv[]) {    


		String fileName = null;
		Lexer l = null;
		try{
			if (argv.length < 1){
				/*System.out.print("Insert the script file name: ");
				InputStreamReader input = new InputStreamReader (System.in);
				BufferedReader keyboard = new BufferedReader (input);
				fileName = new String();
				fileName = keyboard.readLine();*/
				System.err.println("Missing input file.");
				System.exit(1);
			}
			else 
				fileName = argv[0];

			try {
				l = new Lexer(new FileReader(fileName));
			} catch (FileNotFoundException e) {
				System.err.println("Error: unable to open " + fileName);
			} catch (NullPointerException e) {
				System.err.println("Error: unable to open " + fileName);
			}
			parser p = new parser(l);
			@SuppressWarnings("unused")
			Object result = p.parse();      
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("<abort>");
		}

		System.out.println("<done>");
	}
}

