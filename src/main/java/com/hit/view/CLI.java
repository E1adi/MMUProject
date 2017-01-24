package com.hit.view;


import java.io.PrintWriter;
import java.util.Scanner;

public class CLI {

	private Scanner in;
	private PrintWriter out;
	
	public CLI(java.io.InputStream in,
			   java.io.OutputStream out) {
		this.in = new Scanner(in);
		this.out = new PrintWriter(out);
	}
	
	private String read() {
		return in.nextLine();
	}
	
	public String[] getConfiguration() {
		String userInput;
		write("Please write start to start");
		userInput = read();
		while(userInput.toLowerCase() != "start") {
			if(userInput.toLowerCase() == "stop") {
				write("There is notheing to stop at the moment, the MMU is not running.");
				write("Please write start to start.");
			}
			else { 
				write("Not a valid commnad, Please try again.");
			}
			userInput = read();
		}
		
	}
	
	public void write(String string) {
		out.println(string);
		out.flush();
	}
	
}
